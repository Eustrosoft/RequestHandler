/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms.dbdatasource;

import org.eustrosoft.cms.dbdatasource.ranges.FileType;
import org.eustrosoft.cms.model.FDir;
import org.eustrosoft.cms.util.DBStatements;
import org.eustrosoft.cms.util.FSDao;
import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.core.db.ExecStatus;
import org.eustrosoft.handlers.cms.dto.CMSGeneralObject;
import org.eustrosoft.handlers.cms.dto.CMSObject;
import org.eustrosoft.handlers.cms.dto.CMSObjectUpdateParameters;
import org.eustrosoft.handlers.cms.dto.CMSType;
import org.eustrosoft.handlers.cms.dto.FileDetails;
import org.eustrosoft.handlers.cms.dto.HexFileParams;
import org.eustrosoft.handlers.cms.dto.HexFileResult;
import org.eustrosoft.qdbp.QDBPConnection;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.eustrosoft.cms.util.DBStatements.getSelectForPath;
import static org.eustrosoft.cms.util.DBStatements.getViewStatementForPath;
import static org.eustrosoft.cms.util.FileUtils.getFirstLevelFromPath;
import static org.eustrosoft.cms.util.FileUtils.getLastLevelFromPath;
import static org.eustrosoft.cms.util.FileUtils.getParentPath;
import static org.eustrosoft.cms.util.FileUtils.getPathLvl;
import static org.eustrosoft.cms.util.FileUtils.getPathParts;
import static org.eustrosoft.constants.DBConstants.TYPE;
import static org.eustrosoft.core.db.util.DBUtils.getFid;
import static org.eustrosoft.core.db.util.DBUtils.getStrValueOrEmpty;
import static org.eustrosoft.core.db.util.DBUtils.getZoid;
import static org.eustrosoft.core.db.util.DBUtils.getZsid;

public class DBDataSource implements CMSDataSource {
    private final QDBPConnection poolConnection;

    public DBDataSource(QDBPConnection poolConnection) throws Exception {
        if (poolConnection == null) {
            throw new Exception("Connection was null.");
        }
        this.poolConnection = poolConnection;
        this.poolConnection.get().setAutoCommit(true);
    }

    @Override
    public List<CMSObject> getContent(String path) throws Exception {
        String newPath = getFullPath(path); // todo
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = getViewStatementForPath(connection, newPath);
        List<CMSObject> cmsObjects = new ArrayList<>();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            cmsObjects = processResultSetToCMSObjects(resultSet, new File(path).getPath());
            preparedStatement.close();
            resultSet.close();
            fillSpaceForFiles(cmsObjects);
        }
        return cmsObjects;
    }

    public static CMSType getType(ResultSet resultSet) {
        return getType(resultSet, CMSType.UNKNOWN);
    }

    @Override
    public String createLink(String source, String target) throws Exception {
        throw new Exception("Links is not supported for data source.");
    }

    @Override
    public String createFile(String path, InputStream stream) throws Exception {
        throw new Exception("Streams is not supported for data source.");
    }

    @Override
    public String createFile(String path, String name) throws Exception {
        path = getFullPath(path); // todo
        File file = new File(path);
        String filePath = file.getPath();
        Long parentZoid = Long.parseLong(filePath.substring(filePath.lastIndexOf(DBConstants.SEPARATOR) + 1));
        String scopeZoid = getFirstLevelFromPath(filePath);
        FSDao FSDao = new FSDao(poolConnection);
        ExecStatus opened = FSDao.openObject("FS.F", parentZoid);
        if (!opened.isOk()) {
            throw new Exception(opened.getCaption());
        }
        ExecStatus objectInScope = FSDao.createObjectInScope("FS.F", scopeZoid);
        if (!objectInScope.isOk()) {
            throw new Exception(objectInScope.getCaption());
        }
        String fileName = filePath.substring(filePath.lastIndexOf(DBConstants.SEPARATOR));
        ExecStatus fFile = FSDao.createFFile(
                objectInScope.getZoid().toString(),
                objectInScope.getZver().toString(),
                null,
                FileType.FILE,
                fileName
        );
        if (!fFile.isOk()) {
            throw new Exception(fFile.getCaption()); // TODO
        }
        ExecStatus commited = FSDao.commitObject(
                "FS.F",
                opened.getZoid(),
                opened.getZver()
        );
        if (!commited.isOk()) {
            throw new Exception(commited.getCaption()); // TODO
        }
        return commited.getZoid().toString();
    }

    @Override
    public HexFileResult createFileHex(HexFileParams params) throws Exception {
        // TODO add zlvl
        // SELECT * FROM TIS.V_ZObject WHERE zoid = 1441804 (ZSID = 2312314, zlvl = 63)
        // open dir (zsid)
        // create_Object(zsid, zlvl)  (zsid, lvl_trust) (object)
        // create_FDir(zoid, zver (1))
        // commit_object(zoid) -> commit dir (1)
        // create_FFile (2) (pid = null, ) -> zver
        // create_FBlob (id - object zoid, zver - object version, pid - row id (1-st param), hex, chunk, chunks, size, crc32)
        // create_FBlob (id - object zoid, zver - object version, pid - row id (1-st param), hex, chunk, chunks, size, crc32) - until last chunk
        // commit_object(object)
        String dest = getFullPath(params.getDestination());
        String crc32 = params.getCrc32();
        String recordId = params.getRecordId();
        String recordVer = params.getRecordVer();
        String filePid = params.getFilePid();
        String hex = params.getHex();
        Long chunk = params.getChunkNumber();
        Long chunkCount = params.getChunkCount();
        Long chunkSize = params.getChunkSize();

        Long lastLvlPath = Long.parseLong(getLastLevelFromPath(new File(dest).getPath()));
        FSDao fsDao = new FSDao(poolConnection);
        ResultSet selectObject = fsDao.selectObject(lastLvlPath);
        Long zoid = null;
        String zlvl = null;
        String zsid = null;
        try {
            if (selectObject != null) {
                selectObject.next();
                zoid = selectObject.getLong(DBConstants.ZOID);
                zlvl = selectObject.getString(DBConstants.ZLVL);
                zsid = selectObject.getString(DBConstants.ZSID);
                if (selectObject.next()) {
                    throw new Exception("Multiple directories was found.");
                }
            }
        } catch (Exception ex) {
            // ex.printStackTrace();
        } finally {
            if (selectObject != null) {
                selectObject.close();
            }
        }
        if (chunk == 0) {
            String fileName = params.getRecordId();
            ExecStatus opened = fsDao.openObject("FS.F", zoid);
            try {
                if (!opened.isOk()) {
                    throw new Exception(opened.getCaption());
                }
                ExecStatus objectInScope = fsDao.createObjectInScope(
                        "FS.F",
                        zsid,
                        String.valueOf(params.getSecurityLevel())
                );
                if (!objectInScope.isOk()) {
                    throw new Exception(objectInScope.getCaption());
                }
                recordId = objectInScope.getZoid().toString();
                recordVer = objectInScope.getZver().toString();
                ExecStatus fDir = fsDao.createFDir(
                        opened.getZoid(),
                        opened.getZver(),
                        zoid,
                        objectInScope.getZoid(),
                        fileName,
                        params.getDescription()
                );
                if (!fDir.isOk()) {
                    throw new Exception(fDir.getCaption());
                }
                ExecStatus commitFDir = fsDao.commitObject(
                        "FS.F",
                        opened.getZoid(),
                        opened.getZver()
                );
                if (!commitFDir.isOk()) {
                    throw new Exception(commitFDir.getCaption());
                }
                ExecStatus fFile = fsDao.createFFile(
                        objectInScope.getZoid().toString(),
                        objectInScope.getZver().toString(),
                        null,
                        FileType.FILE,
                        fileName,
                        String.valueOf(params.getSecurityLevel()),
                        params.getDescription()
                );
                if (!fFile.isOk()) {
                    throw new Exception(fFile.getCaption());
                }
                filePid = fFile.getZoid().toString();
            } finally {
                try {
                    fsDao.commitObject("FS.F", opened.getZoid(), opened.getZver());
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }
        fsDao.createFBlob(
                recordId,
                recordVer,
                filePid,
                hex,
                String.valueOf(chunk),
                String.valueOf(chunkSize),
                crc32
        );
        if (chunk == chunkCount - 1) {
            ExecStatus committed = fsDao.commitObject(
                    "FS.F",
                    Long.parseLong(recordId),
                    Long.parseLong(recordVer)
            );
            if (!committed.isOk()) {
                throw new Exception(committed.getCaption());
            }
        }
        return new HexFileResult(recordId, recordVer, filePid, params.getDestination());
    }

    @Override
    public String createDirectory(String path, String description, Integer securityLevel) throws Exception {
        String parentPath = getFullPath(getParentPath(path));
        Long parentZoid = Long.parseLong(getLastLevelFromPath(parentPath));
        String scopeZoid = getFirstLevelFromPath(parentPath);
        FSDao FSDao = new FSDao(poolConnection);
        ExecStatus opened = FSDao.openObject("FS.F", parentZoid);
        if (!opened.isOk()) {
            throw new Exception(opened.getCaption());
        }
        String slvl = securityLevel == null ? null : securityLevel.toString();
        ExecStatus objectInScope = FSDao.createObjectInScope("FS.F", scopeZoid, slvl);

        String dirName = path.substring(path.lastIndexOf(DBConstants.SEPARATOR) + 1);
        ExecStatus fFile = FSDao.createFFile(
                objectInScope.getZoid().toString(),
                objectInScope.getZver().toString(),
                null,
                FileType.DIRECTORY,
                dirName,
                slvl,
                description
        );
        if (!fFile.isOk()) {
            throw new Exception(fFile.getCaption()); // TODO
        }
        ExecStatus commited = FSDao.commitObject(
                "FS.F",
                objectInScope.getZoid(),
                objectInScope.getZver()
        );
        if (!commited.isOk()) {
            throw new Exception(commited.getCaption()); // TODO
        }
        ExecStatus fDir = FSDao.createFDir(
                opened.getZoid(),
                opened.getZver(),
                fFile.getZver(),
                objectInScope.getZoid(),
                dirName,
                description
        );
        if (!fDir.isOk()) {
            throw new Exception(fDir.getCaption()); // TODO
        }
        ExecStatus objectCommited = FSDao.commitObject(
                "FS.F",
                opened.getZoid(),
                opened.getZver()
        );
        if (!objectCommited.isOk()) {
            throw new Exception(objectCommited.getCaption()); // TODO
        }
        return objectCommited.getZoid().toString();
    }

    @Override
    public String getFullPath(String source) throws Exception {
        if (source == null || source.isEmpty()) {
            return "";
        }
        if (source.trim().equalsIgnoreCase(DBConstants.SEPARATOR)) {
            return DBConstants.SEPARATOR;
        }
        String selectForPath = getSelectForPath(source);
        Connection connection = poolConnection.get();
        Statement statement = connection.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery(selectForPath);
            if (resultSet.next()) {
                String id = getStrValueOrEmpty(resultSet, DBConstants.ID);
                String zoid = getStrValueOrEmpty(resultSet, DBConstants.ZOID);
                List<String> fIds = new ArrayList<>();
                if (getPathParts(source).length > 2) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        if (columnName.equals("f_id")) {
                            String columnVal = resultSet.getString(i);
                            if (columnVal == null || columnName.isEmpty()) {
                                throw new Exception("f_id was null for one of the files in path.");
                            }
                            fIds.add(columnVal);
                        }
                    }
                }
                List<String> pathParts = new ArrayList<>();
                pathParts.add(id);
                if (!zoid.isEmpty()) {
                    pathParts.add(zoid);
                }
                pathParts.addAll(fIds);
                StringBuilder pathBuilder = new StringBuilder();
                for (String pathPart : pathParts) {
                    pathBuilder.append(DBConstants.SEPARATOR);
                    pathBuilder.append(pathPart);
                }
                return pathBuilder.toString();
            } else {
                throw new IllegalArgumentException("Can not find this path.");
            }
        } finally {
            statement.close();
        }
    }

    @Override
    public boolean update(String path, CMSObjectUpdateParameters data) throws Exception {
        if (isEmpty(path) || data.getDescription() == null) {
            return true;
        }
        String fullPath = getFullPath(path);
        Long zoid = Long.parseLong(getLastLevelFromPath(fullPath));
        FSDao fsDao = new FSDao(poolConnection);
        FDir fDir = fsDao.getFDir(zoid, getLastLevelFromPath(path));
        if (Objects.nonNull(data.getDescription())) {
            fDir.setDescription(data.getDescription());
        }
        fsDao.updateFDir(fDir);
        return true;
    }

    @Override
    public boolean copy(String source, String direction) throws Exception {
        String fullPath = getFullPath(source);
        String lastLevelFromPath = getLastLevelFromPath(fullPath);
        Long fileId = Long.parseLong(lastLevelFromPath);
        String lastLevelDist = getLastLevelFromPath(direction);
        FSDao fsDao = new FSDao(poolConnection);

        String dirToMove = direction.substring(0, direction.length() - lastLevelDist.length() - 1);
        Long dirId = Long.parseLong(getLastLevelFromPath(getFullPath(dirToMove)));
        FDir fDir = fsDao.getFDir(fileId, lastLevelDist);
        ExecStatus opened = fsDao.openObject("FS.F", dirId);
        try {
            if (!opened.isOk()) {
                throw new Exception(opened.getCaption());
            }
            ExecStatus objectInScope = fsDao.createObjectInScope(
                    "FS.F", fDir.getZSID(), fDir.getZLVL()
            );

            if (!objectInScope.isOk()) {
                throw new Exception(objectInScope.getCaption());
            }
            fsDao.createFDir(
                    opened.getZoid(),
                    opened.getZver(),
                    null,
                    fDir.getFileId(),
                    fDir.getFileName(),
                    fDir.getDescription()
            );
        } finally {
            try {
                fsDao.commitObject("FS.F", opened.getZoid(), opened.getZver());
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean move(String source, String direction) throws Exception {
        String lastLevelSource = getLastLevelFromPath(source);
        String lastLevelDist = getLastLevelFromPath(direction);

        if (lastLevelSource.equals(lastLevelDist)) {
            copy(source, direction);
            delete(source);
        } else {
            String fullPath = getFullPath(source);
            String lastLevelFromPath = getLastLevelFromPath(fullPath);
            Long fileId = Long.parseLong(lastLevelFromPath);
            FSDao fsDao = new FSDao(poolConnection);
            fsDao.renameFile(
                    fileId,
                    lastLevelSource,
                    lastLevelDist
            );
        }
        return true;
    }

    @Override
    public InputStream getFileStream(String path) throws Exception {
        try {
            // todo: try to avoid this logic
            path = getFullPath(path);
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        String zoid = getLastLevelFromPath(path);
        return new FSDao(poolConnection).getFileInputStream(zoid);
    }

    @Override
    public FileDetails getFileDetails(String path) throws Exception {
        try {
            path = getFullPath(path);
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        Connection connection = poolConnection.get();
        Long zoid = Long.parseLong(getLastLevelFromPath(path));
        PreparedStatement fileDetailsPS = DBStatements.getFileDetails(connection, zoid);
        FileDetails fileDetails = new FileDetails();
        try {
            ResultSet resultSet = fileDetailsPS.executeQuery();
            int index = 0;
            while (resultSet.next()) {
                if (index >= 1) {
                    throw new Exception("More than 1 file present in this path.\nCall administrator to resolve.");
                }
                index++;
                if (!resultSet.getString("type").equals("B")) {
                    throw new Exception("Type not match."); // todo: create file details for dir
                }
                fileDetails.setMimeType(resultSet.getString("mimetype"));
                fileDetails.setFileName(resultSet.getString("name"));
            }
        } finally {
            fileDetailsPS.close();
        }
        FSDao functions = new FSDao(poolConnection);
        fileDetails.setFileLength(functions.getFileLength(zoid));
        fileDetails.setEncoding("UTF-8");
        return fileDetails;
    }

    @Override
    public boolean delete(String path) throws Exception {
        path = getFullPath(path);
        String dirName = getLastLevelFromPath(path);
        String parentPath = getParentPath(path);
        Long parentZoid = Long.parseLong(getLastLevelFromPath(parentPath));

        FSDao fsDao = new FSDao(poolConnection);
        ExecStatus open = fsDao.openObject("FS.F", parentZoid);
        if (!open.isOk()) {
            throw new Exception(open.getCaption());
        }
        ResultSet directoryByNameAndId = fsDao.getDirectoryByNameAndId(parentZoid, dirName);
        ExecStatus commit = null;
        try {
            directoryByNameAndId.next();
            long zrid = directoryByNameAndId.getLong(DBConstants.ZRID);
            ExecStatus delete = fsDao.deleteFDir(
                    open.getZoid(),
                    zrid,
                    open.getZver()
            );
            if (!delete.isOk()) {
                throw new Exception(delete.getCaption());
            }
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        } finally {
            // todo: rollback
            commit = fsDao.commitObject("FS.F", parentZoid, open.getZver());
            directoryByNameAndId.close();
        }
        if (commit == null) {
            commit = fsDao.commitObject("FS.F", parentZoid, open.getZver());
        }
        if (!commit.isOk()) {
            throw new Exception(open.getCaption());
        }
        return commit.isOk();
    }

    public static CMSType getType(ResultSet resultSet, CMSType defaultValue) {
        CMSType val = defaultValue;
        try {
            String typeStr = resultSet.getObject(TYPE).toString();
            if (typeStr.equals("R") || typeStr.equals("D")) { // todo
                val = CMSType.DIRECTORY;
            }
            if (typeStr.equals("B")) { // todo
                val = CMSType.FILE;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return val;
    }

    private void fillSpaceForFiles(List<CMSObject> objects) throws SQLException { // todo: make a single query
        FSDao functions = new FSDao(poolConnection);
        for (int i = 0; i < objects.size(); i++) {
            CMSObject cmsObject = objects.get(i);
            if (cmsObject instanceof CMSGeneralObject) {
                ((CMSGeneralObject) cmsObject).setSpace(functions.getFileLength(cmsObject.getZOID()));
            }
        }
    }

    private List<CMSObject> processResultSetToCMSObjects(ResultSet resultSet, String fullPath) throws Exception {
        List<CMSObject> objects = new ArrayList<>();
        int pathLvl = getPathLvl(fullPath);
        try {
            while (resultSet.next()) {
                try {
                    String name = getStrValueOrEmpty(resultSet, DBConstants.NAME);
                    String fname = getStrValueOrEmpty(resultSet, DBConstants.F_NAME);
                    CMSType type = pathLvl < 2 ? CMSType.DIRECTORY : getType(resultSet);
                    Long sid = getZsid(resultSet);
                    Long zoid = getZoid(resultSet);
                    String fid = getFid(resultSet);
                    String zlvl = getStrValueOrEmpty(resultSet, DBConstants.ZLVL);
                    String descr = getStrValueOrEmpty(resultSet, DBConstants.DESCRIPTION);
                    String finalName = fname.isEmpty() ? name : fname;
                    Long id = fid.isEmpty() ? zoid : Long.parseLong(fid);
                    CMSGeneralObject object = new CMSGeneralObject();
                    object.setDescription(descr);
                    object.setFullPath(new File(fullPath, finalName).getPath());
                    object.setFileName(finalName);
                    object.setType(type);
                    try {
                        object.setSecurityLevel(Integer.valueOf(zlvl, 10));
                    } catch (Exception ex) {
                        // ex.printStackTrace();
                    }
                    object.setZOID(id);
                    objects.add(object);
                } catch (Exception ex) {
                    // ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        return objects;
    }

    private boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    private boolean isEmpty(Integer val) {
        return val == null;
    }
}
