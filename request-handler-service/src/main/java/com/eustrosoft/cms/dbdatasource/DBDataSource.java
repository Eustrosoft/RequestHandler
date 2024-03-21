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
import com.eustrosoft.cms.parameters.CMSObjectUpdateParameters;
import com.eustrosoft.cms.parameters.FileDetails;
import com.eustrosoft.cms.parameters.HexFileParams;
import com.eustrosoft.cms.parameters.HexFileResult;
import com.eustrosoft.cms.util.DBStatements;
import com.eustrosoft.cms.util.FSDao;
import com.eustrosoft.core.db.ExecStatus;
import com.eustrosoft.core.model.FDir;
import com.eustrosoft.core.providers.MimeType;
import org.eustrosoft.qdbp.QDBPConnection;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.eustrosoft.cms.util.DBStatements.getSelectForPath;
import static com.eustrosoft.cms.util.DBStatements.getViewStatementForPath;
import static com.eustrosoft.cms.util.FileUtils.getFirstLevelFromPath;
import static com.eustrosoft.cms.util.FileUtils.getLastLevelFromPath;
import static com.eustrosoft.cms.util.FileUtils.getParentPath;
import static com.eustrosoft.cms.util.FileUtils.getPathLvl;
import static com.eustrosoft.cms.util.FileUtils.getPathParts;
import static com.eustrosoft.core.constants.DBConstants.*;
import static com.eustrosoft.core.db.util.DBUtils.getFidOrZoid;
import static com.eustrosoft.core.db.util.DBUtils.getStrValueOrEmpty;
import static com.eustrosoft.core.db.util.DBUtils.getType;
import static com.eustrosoft.core.db.util.DBUtils.getZoid;
import static com.eustrosoft.core.db.util.DBUtils.getZsid;
import static com.eustrosoft.core.handlers.cms.CMSHandler.VIRTUAL_SUBSYSTEM;

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

    private void fillSpaceForFiles(List<CMSObject> objects) throws SQLException { // todo: make a single query
        FSDao functions = new FSDao(poolConnection);
        for (int i = 0; i < objects.size(); i++) {
            CMSObject cmsObject = objects.get(i);
            if (cmsObject instanceof CMSGeneralObject) {
                ((CMSGeneralObject) cmsObject)
                        .setSpace(functions.getFileLength(((CMSGeneralObject) cmsObject).getFileId()));
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
        FSDao fsDao = new FSDao(poolConnection);
        ExecStatus opened = fsDao.openObject("FS.F", parentZoid);
        if (!opened.isOk()) {
            throw new Exception(opened.getCaption());
        }
        ExecStatus objectInScope = fsDao.createObjectInScope("FS.F", scopeZoid);
        if (!objectInScope.isOk()) {
            fsDao.rollbackObject(
                    "FS.F",
                    opened.getZoid(),
                    opened.getZver(),
                    'Y'
            );
            throw new Exception(objectInScope.getCaption());
        }
        String fileName = filePath.substring(filePath.lastIndexOf(SEPARATOR));
        ExecStatus fFile = fsDao.createFFile(
                objectInScope.getZoid().toString(),
                objectInScope.getZver().toString(),
                null,
                FileType.FILE,
                fileName,
                MimeType.getMimeTypeByFileName(fileName)
        );
        if (!fFile.isOk()) {
            fsDao.rollbackObject(
                    "FS.F",
                    opened.getZoid(),
                    opened.getZver(),
                    'Y'
            );
            throw new Exception(fFile.getCaption()); // TODO
        }
        ExecStatus commited = fsDao.commitObject(
                "FS.F",
                opened.getZoid(),
                opened.getZver()
        );
        if (!commited.isOk()) {
            fsDao.rollbackObject(
                    "FS.F",
                    opened.getZoid(),
                    opened.getZver(),
                    'Y'
            );
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

        Long lastLvlPath = Long.parseLong(getLastLevelFromPath(Paths.get(dest).toUri().toString()));
        FSDao fsDao = new FSDao(poolConnection);
        ResultSet selectObject = fsDao.selectObject(lastLvlPath);
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
                        MimeType.getMimeTypeByFileName(fileName),
                        params.getDescription()
                );
                if (!fDir.isOk()) {
                    fsDao.rollbackObject(
                            "FS.F",
                            opened.getZoid(),
                            opened.getZver(),
                            'Y'
                    );
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
                        MimeType.getMimeTypeByFileName(fileName),
                        params.getSecurityLevel(),
                        params.getDescription()
                );
                if (!fFile.isOk()) {
                    fsDao.rollbackObject(
                            "FS.F",
                            opened.getZoid(),
                            opened.getZver(),
                            'Y'
                    );
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
        FSDao fsDao = new FSDao(poolConnection);
        ExecStatus opened = fsDao.openObject("FS.F", parentZoid);
        if (!opened.isOk()) {
            throw new Exception(opened.getCaption());
        }
        String slvl = securityLevel == null ? null : securityLevel.toString();
        ExecStatus objectInScope = fsDao.createObjectInScope("FS.F", scopeZoid, slvl);

        String dirName = path.substring(path.lastIndexOf(SEPARATOR) + 1);
        ExecStatus fFile = fsDao.createFFile(
                objectInScope.getZoid().toString(),
                objectInScope.getZver().toString(),
                null,
                FileType.DIRECTORY,
                dirName,
                null,
                securityLevel,
                description
        );
        if (!fFile.isOk()) {
            fsDao.rollbackObject(
                    "FS.F",
                    opened.getZoid(),
                    opened.getZver(),
                    'Y'
            );
            throw new Exception(fFile.getCaption());
        }
        ExecStatus commited = fsDao.commitObject(
                "FS.F",
                objectInScope.getZoid(),
                objectInScope.getZver()
        );
        if (!commited.isOk()) {
            fsDao.rollbackObject(
                    "FS.F",
                    opened.getZoid(),
                    opened.getZver(),
                    'Y'
            );
            throw new Exception(commited.getCaption());
        }
        ExecStatus fDir = fsDao.createFDir(
                opened.getZoid(),
                opened.getZver(),
                fFile.getZver(),
                objectInScope.getZoid(),
                dirName,
                description
        );
        if (!fDir.isOk()) {
            fsDao.rollbackObject(
                    "FS.F",
                    opened.getZoid(),
                    opened.getZver(),
                    'Y'
            );
            throw new Exception(fDir.getCaption());
        }
        ExecStatus objectCommited = fsDao.commitObject(
                "FS.F",
                opened.getZoid(),
                opened.getZver()
        );
        if (!objectCommited.isOk()) {
            fsDao.rollbackObject(
                    "FS.F",
                    opened.getZoid(),
                    opened.getZver(),
                    'Y'
            );
            throw new Exception(objectCommited.getCaption());
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
                        if (columnName.equals("zoid")) {
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
    public boolean update(String path, CMSObjectUpdateParameters data) throws Exception {
        if (isEmpty(path) || data.getDescription() == null) {
            return true;
        }
        //String fileName = getLastLevelFromPath(path);
        Long zoid = Long.parseLong(getLastLevelFromPath(getFullPath(path)));
        FSDao fsDao = new FSDao(poolConnection);
        FDir fDir = fsDao.getFDir(zoid);
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
        FDir fDir = fsDao.getFDirByFileId(fileId, lastLevelDist);
        ExecStatus opened = fsDao.openObject("FS.F", dirId);
        try {
            if (!opened.isOk()) {
                throw new Exception(opened.getCaption());
            }
            ExecStatus objectInScope = fsDao.createObjectInScope(
                    "FS.F", fDir.getZsid(), fDir.getZlvl()
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

        String sourcePath = source.substring(0, source.length() - lastLevelSource.length());
        String targetPath = direction.substring(0, direction.length() - lastLevelDist.length());

        if (!source.equals(direction) && !sourcePath.equals(targetPath)) {
            copy(source, direction);
            delete(source);
        } else if (!lastLevelSource.equals(lastLevelDist)) {
            String zoid = getLastLevelFromPath(getFullPath(source));
            if (!zoid.isEmpty()) {
                FSDao fsDao = new FSDao(poolConnection);
                fsDao.renameFile(
                        Long.parseLong(zoid),
                        lastLevelSource,
                        lastLevelDist
                );
            }
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
        String fullPath = getFullPath(path);
        String dirName = getLastLevelFromPath(path);
        String parentPath = getParentPath(fullPath);
        Long parentZoid = Long.parseLong(getLastLevelFromPath(parentPath));

        FSDao fsDao = new FSDao(poolConnection);
        ExecStatus open = fsDao.openObject("FS.F", parentZoid);
        if (!open.isOk()) {
            fsDao.rollbackObject("FS.F", open.getZoid(), open.getZver(), 'Y');
            throw new Exception(open.getCaption());
        }
        ExecStatus commit;
        ResultSet directoryByNameAndId = null;
        try {
            directoryByNameAndId = fsDao.getDirectoryByNameAndId(parentZoid, dirName);

            directoryByNameAndId.next();
            long zrid = directoryByNameAndId.getLong(ZRID);
            ExecStatus delete = fsDao.deleteFDir(
                    open.getZoid(),
                    zrid,
                    open.getZver()
            );
            if (!delete.isOk()) {
                throw new Exception(delete.getCaption());
            }
        } catch (Exception ex) {
            fsDao.rollbackObject("FS.F", open.getZoid(), open.getZver(), 'Y');
            throw new Exception(ex.getMessage());
        } finally {
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

    private List<CMSObject> processResultSetToCMSObjects(ResultSet resultSet, String fullPath) throws Exception {
        List<CMSObject> objects = new ArrayList<>();
        int pathLvl = getPathLvl(fullPath);
        fullPath = VIRTUAL_SUBSYSTEM + fullPath;
        try {
            while (resultSet.next()) {
                try {
                    String name = getStrValueOrEmpty(resultSet, NAME);
                    String fname = getStrValueOrEmpty(resultSet, F_NAME);
                    CMSType type = pathLvl < 2 ? CMSType.DIRECTORY : getType(resultSet);
                    Long sid = getZsid(resultSet);
                    String zlvl = getStrValueOrEmpty(resultSet, ZLVL);
                    String descr = getStrValueOrEmpty(resultSet, DESCRIPTION);
                    String mimeType = getStrValueOrEmpty(resultSet, MIME_TYPE);
                    String finalName = fname.isEmpty() ? name : fname;
                    String fid = getFidOrZoid(resultSet);
                    Long zoid = getZoid(resultSet);
                    CMSGeneralObject.CMSGeneralObjectBuilder builder = CMSGeneralObject.builder()
                            .description(descr)
                            .fullPath(new File(fullPath, finalName).getPath())
                            .fileName(finalName)
                            .mimeType(mimeType)
                            .type(type);
                    try {
                        builder.securityLevel(Integer.valueOf(zlvl, 10));
                        if (fid != null && !fid.isEmpty()) {
                            builder.fileId(Long.parseLong(fid));
                        }
                    } catch (Exception ex) {
                        // ex.printStackTrace();
                    }
                    CMSGeneralObject build = builder.build();
                    build.setZoid(zoid);
                    build.setZsid(sid);
                    objects.add(build);
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
}
