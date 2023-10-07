/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.cms.dbdatasource;

import com.eustrosoft.cms.CMSDataSource;
import com.eustrosoft.cms.CMSType;
import com.eustrosoft.cms.dbdatasource.ranges.FileType;
import com.eustrosoft.cms.dto.CMSGeneralObject;
import com.eustrosoft.cms.dto.CMSObject;
import com.eustrosoft.cms.exception.CMSException;
import com.eustrosoft.cms.parameters.CMSObjectUpdateParameters;
import com.eustrosoft.cms.parameters.FileDetails;
import com.eustrosoft.cms.parameters.HexFileParams;
import com.eustrosoft.cms.parameters.HexFileResult;
import com.eustrosoft.cms.util.DBStatements;
import com.eustrosoft.cms.util.FSDao;
import com.eustrosoft.core.db.ExecStatus;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPConnection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.eustrosoft.cms.util.DBStatements.getSelectForPath;
import static com.eustrosoft.cms.util.DBStatements.getViewStatementForPath;
import static com.eustrosoft.cms.util.FileUtils.getFirstLevelFromPath;
import static com.eustrosoft.cms.util.FileUtils.getLastLevelFromPath;
import static com.eustrosoft.cms.util.FileUtils.getPathLvl;
import static com.eustrosoft.cms.util.FileUtils.getPathParts;
import static com.eustrosoft.core.constants.DBConstants.DESCRIPTION;
import static com.eustrosoft.core.constants.DBConstants.F_NAME;
import static com.eustrosoft.core.constants.DBConstants.ID;
import static com.eustrosoft.core.constants.DBConstants.NAME;
import static com.eustrosoft.core.constants.DBConstants.SEPARATOR;
import static com.eustrosoft.core.constants.DBConstants.ZLVL;
import static com.eustrosoft.core.constants.DBConstants.ZOID;
import static com.eustrosoft.core.constants.DBConstants.ZRID;
import static com.eustrosoft.core.constants.DBConstants.ZSID;
import static com.eustrosoft.core.db.util.DBUtils.getFid;
import static com.eustrosoft.core.db.util.DBUtils.getStrValueOrEmpty;
import static com.eustrosoft.core.db.util.DBUtils.getType;
import static com.eustrosoft.core.db.util.DBUtils.getZoid;
import static com.eustrosoft.core.db.util.DBUtils.getZsid;

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

    private void fillSpaceForFiles(List<CMSObject> objects) { // todo: make a single query
        FSDao functions = new FSDao(poolConnection);
        for (int i = 0; i < objects.size(); i++) {
            CMSObject cmsObject = objects.get(i);
            if (cmsObject instanceof CMSGeneralObject) {
                ((CMSGeneralObject) cmsObject).setSpace(functions.getFileLength(cmsObject.getZoid()));
            }
        }
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
        Long parentZoid = Long.parseLong(filePath.substring(filePath.lastIndexOf(SEPARATOR) + 1));
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
        String fileName = filePath.substring(filePath.lastIndexOf(SEPARATOR));
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

        Long lastLvlPath = Long.parseLong(getLastLevelFromPath(new File(dest).getPath()));
        FSDao FSDao = new FSDao(poolConnection);
        ResultSet selectObject = FSDao.selectObject(lastLvlPath);
        Long zoid = null;
        String zlvl = null;
        String zsid = null;
        try {
            if (selectObject != null) {
                selectObject.next();
                zoid = selectObject.getLong(ZOID);
                zlvl = selectObject.getString(ZLVL);
                zsid = selectObject.getString(ZSID);
                if (selectObject.next()) {
                    throw new Exception("Multiple directories was found.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (selectObject != null) {
                selectObject.close();
            }
        }
        if (chunk == 0) {
            String fileName = params.getRecordId();
            ExecStatus opened = FSDao.openObject("FS.F", zoid);
            try {
                if (!opened.isOk()) {
                    throw new Exception(opened.getCaption());
                }
                ExecStatus objectInScope = FSDao.createObjectInScope(
                        "FS.F",
                        zsid,
                        String.valueOf(params.getSecurityLevel())
                );
                if (!objectInScope.isOk()) {
                    throw new Exception(objectInScope.getCaption());
                }
                recordId = objectInScope.getZoid().toString();
                recordVer = objectInScope.getZver().toString();
                ExecStatus fDir = FSDao.createFDir(
                        opened.getZoid(),
                        opened.getZver(),
                        zoid,
                        objectInScope.getZoid(),
                        fileName,
                        params.getDescription()
                );
                if (!fDir.isOk()) {
                    throw new Exception(fDir.getCaption()); // TODO
                }
                ExecStatus commitFDir = FSDao.commitObject(
                        "FS.F",
                        opened.getZoid(),
                        opened.getZver()
                );
                if (!commitFDir.isOk()) {
                    throw new Exception(commitFDir.getCaption()); // TODO
                }
                ExecStatus fFile = FSDao.createFFile(
                        objectInScope.getZoid().toString(),
                        objectInScope.getZver().toString(),
                        null,
                        FileType.FILE,
                        fileName,
                        String.valueOf(params.getSecurityLevel()),
                        params.getDescription()
                );
                if (!fFile.isOk()) {
                    throw new Exception(fFile.getCaption()); // TODO
                }
                filePid = fFile.getZoid().toString();
            } finally {
                try {
                    FSDao.commitObject("FS.F", opened.getZoid(), opened.getZver());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ExecStatus fBlob = FSDao.createFBlob(
                recordId,
                recordVer,
                filePid,
                hex,
                String.valueOf(chunk),
                String.valueOf(chunkCount),
                crc32
        );
        if (chunk == chunkCount - 1) {
            // todo: add size
            ExecStatus commited = FSDao.commitObject(
                    "FS.F",
                    Long.parseLong(recordId),
                    Long.parseLong(recordVer)
            );
            if (!commited.isOk()) {
                throw new Exception(commited.getCaption()); // TODO
            }
        }
        return new HexFileResult(recordId, recordVer, filePid, params.getDestination());
    }

    @Override
    public String createDirectory(String path) throws Exception {
        String parentPath = getFullPath(new File(path).getParent());
        Long parentZoid = Long.parseLong(getLastLevelFromPath(parentPath));
        String scopeZoid = getFirstLevelFromPath(parentPath);
        FSDao FSDao = new FSDao(poolConnection);
        ExecStatus opened = FSDao.openObject("FS.F", parentZoid);
        if (!opened.isOk()) {
            throw new Exception(opened.getCaption());
        }
        ExecStatus objectInScope = FSDao.createObjectInScope("FS.F", scopeZoid);
        if (!objectInScope.isOk()) {
            throw new Exception(objectInScope.getCaption());
        }
        String dirName = path.substring(path.lastIndexOf(SEPARATOR) + 1);
        ExecStatus fFile = FSDao.createFFile(
                objectInScope.getZoid().toString(),
                objectInScope.getZver().toString(),
                null,
                FileType.DIRECTORY,
                dirName
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
                String.format("%s directory created.", dirName)
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
        if (source.trim().equalsIgnoreCase(SEPARATOR)) {
            return SEPARATOR;
        }
        String selectForPath = getSelectForPath(source);
        Connection connection = poolConnection.get();
        Statement statement = connection.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery(selectForPath);
            if (resultSet.next()) {
                String id = getStrValueOrEmpty(resultSet, ID);
                String zoid = getStrValueOrEmpty(resultSet, ZOID);
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
                    pathBuilder.append(SEPARATOR);
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
    public boolean update(String path, CMSObjectUpdateParameters data) throws CMSException {
        return false;
    }

    @Override
    public boolean copy(String source, String direction) throws IOException, CMSException {
        return false;
    }

    // todo: now works as rename
    @Override
    public boolean move(String source, String direction) throws Exception {
        String fullPath = getFullPath(source);
        Long fileId = Long.parseLong(getLastLevelFromPath(fullPath));
        FSDao functions = new FSDao(poolConnection);
        functions.renameFile(
                fileId,
                getLastLevelFromPath(source),
                getLastLevelFromPath(direction)
        );
        return true;
    }

    @Override
    public InputStream getFileStream(String path) throws Exception {
        try {
            // todo: try to avoid this logic
            path = getFullPath(path);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String zoid = getLastLevelFromPath(path);
        return new FSDao(poolConnection).getFileInputStream(zoid);
    }

    @Override
    public FileDetails getFileDetails(String path) throws Exception {
        try {
            path = getFullPath(path);
        } catch (Exception ex) {
            ex.printStackTrace();
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
        File file = new File(path);
        String dirName = getLastLevelFromPath(path);
        String parentPath = file.getParent();
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
            long zrid = directoryByNameAndId.getLong(ZRID);
            ExecStatus delete = fsDao.deleteFDir(
                    String.valueOf(open.getZoid()),
                    String.valueOf(zrid),
                    String.valueOf(open.getZver())
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

    @SneakyThrows
    private List<CMSObject> processResultSetToCMSObjects(ResultSet resultSet, String fullPath) {
        List<CMSObject> objects = new ArrayList<>();
        int pathLvl = getPathLvl(fullPath);
        try {
            while (resultSet.next()) {
                try {
                    String name = getStrValueOrEmpty(resultSet, NAME);
                    String fname = getStrValueOrEmpty(resultSet, F_NAME);
                    CMSType type = pathLvl < 2 ? CMSType.DIRECTORY : getType(resultSet);
                    Long sid = getZsid(resultSet);
                    Long zoid = getZoid(resultSet);
                    String fid = getFid(resultSet);
                    String zlvl = getStrValueOrEmpty(resultSet, ZLVL);
                    String descr = getStrValueOrEmpty(resultSet, DESCRIPTION);
                    String finalName = fname.isEmpty() ? name : fname;
                    Long id = fid.isEmpty() ? zoid : Long.parseLong(fid);
                    CMSGeneralObject.CMSGeneralObjectBuilder builder = CMSGeneralObject.builder()
                            .description(descr)
                            .fullPath(new File(fullPath, finalName).getPath())
                            .fileName(finalName)
                            .type(type);
                    try {
                        builder.securityLevel(Integer.valueOf(zlvl, 10));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    CMSGeneralObject build = builder.build();
                    build.setZoid(id);
                    objects.add(build);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return objects;
    }
}
