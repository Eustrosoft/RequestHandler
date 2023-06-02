package com.eustrosoft.dbdatasource.core;

import com.eustrosoft.datasource.exception.CMSException;
import com.eustrosoft.datasource.sources.CMSDataSource;
import com.eustrosoft.datasource.sources.FileDetails;
import com.eustrosoft.datasource.sources.HexFileParams;
import com.eustrosoft.datasource.sources.HexFileResult;
import com.eustrosoft.datasource.sources.model.CMSGeneralObject;
import com.eustrosoft.datasource.sources.model.CMSObject;
import com.eustrosoft.datasource.sources.model.CMSType;
import com.eustrosoft.datasource.sources.parameters.CMSObjectUpdateParameters;
import com.eustrosoft.dbdatasource.ranges.FileType;
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

import static com.eustrosoft.dbdatasource.constants.DBConstants.DESCRIPTION;
import static com.eustrosoft.dbdatasource.constants.DBConstants.F_NAME;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.NAME;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZLVL;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZOID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZRID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZSID;
import static com.eustrosoft.dbdatasource.core.DBStatements.getFirstLevelFromPath;
import static com.eustrosoft.dbdatasource.core.DBStatements.getLastLevelFromPath;
import static com.eustrosoft.dbdatasource.core.DBStatements.getPathLvl;
import static com.eustrosoft.dbdatasource.core.DBStatements.getPathParts;
import static com.eustrosoft.dbdatasource.core.DBStatements.getSelectForPath;
import static com.eustrosoft.dbdatasource.core.DBStatements.getViewStatementForPath;
import static com.eustrosoft.dbdatasource.util.ResultSetUtils.getFid;
import static com.eustrosoft.dbdatasource.util.ResultSetUtils.getType;
import static com.eustrosoft.dbdatasource.util.ResultSetUtils.getValueOrEmpty;
import static com.eustrosoft.dbdatasource.util.ResultSetUtils.getZoid;
import static com.eustrosoft.dbdatasource.util.ResultSetUtils.getZsid;

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
            preparedStatement.close();
            cmsObjects = processResultSetToCMSObjects(resultSet, new File(path).getPath());
            resultSet.close();
        }
        return cmsObjects;
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
        String parentZoid = filePath.substring(filePath.lastIndexOf('/') + 1);
        String scopeZoid = getFirstLevelFromPath(filePath);
        DBFunctions dbFunctions = new DBFunctions(poolConnection);
        ExecStatus opened = dbFunctions.openObject(parentZoid);
        if (!opened.isOk()) {
            throw new Exception(opened.getCaption());
        }
        ExecStatus objectInScope = dbFunctions.createObjectInScope(scopeZoid);
        if (!objectInScope.isOk()) {
            throw new Exception(objectInScope.getCaption());
        }
        String fileName = filePath.substring(filePath.lastIndexOf('/'));
        ExecStatus fFile = dbFunctions.createFFile(
                objectInScope.getZoid().toString(),
                objectInScope.getZver().toString(),
                null,
                FileType.FILE,
                fileName
        );
        if (!fFile.isOk()) {
            throw new Exception(fFile.getCaption()); // TODO
        }
        ExecStatus commited = dbFunctions.commitObject(
                opened.getZoid().toString(),
                opened.getZver().toString()
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

        String lastLvlPath = getLastLevelFromPath(new File(dest).getPath());
        DBFunctions dbFunctions = new DBFunctions(poolConnection);
        ResultSet selectObject = dbFunctions.selectObject(lastLvlPath);
        String zoid = null;
        String zlvl = null;
        String zsid = null;
        try {
            if (selectObject != null) {
                selectObject.next();
                zoid = selectObject.getString(ZOID);
                zlvl = selectObject.getString(ZLVL);
                zsid = selectObject.getString(ZSID);
                if (selectObject.next()) {
                    throw new Exception("Multiple directories was found.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            selectObject.close();
        }
        if (chunk == 0) {
            String fileName = params.getRecordId();
            ExecStatus opened = dbFunctions.openObject(zoid);
            try {
                if (!opened.isOk()) {
                    throw new Exception(opened.getCaption());
                }
                ExecStatus objectInScope = dbFunctions.createObjectInScope(zsid);
                if (!objectInScope.isOk()) {
                    throw new Exception(objectInScope.getCaption());
                }
                recordId = objectInScope.getZoid().toString();
                recordVer = objectInScope.getZver().toString();
                ExecStatus fDir = dbFunctions.createFDir(
                        opened.getZoid().toString(),
                        opened.getZver().toString(),
                        zoid,
                        objectInScope.getZoid().toString(),
                        fileName,
                        "FDir was created"
                );
                if (!fDir.isOk()) {
                    throw new Exception(fDir.getCaption()); // TODO
                }
                ExecStatus commitFDir = dbFunctions.commitObject(
                        opened.getZoid().toString(),
                        opened.getZver().toString()
                );
                if (!commitFDir.isOk()) {
                    throw new Exception(commitFDir.getCaption()); // TODO
                }
                ExecStatus fFile = dbFunctions.createFFile(
                        objectInScope.getZoid().toString(),
                        objectInScope.getZver().toString(),
                        null,
                        FileType.FILE,
                        fileName
                );
                if (!fFile.isOk()) {
                    throw new Exception(fFile.getCaption()); // TODO
                }
                filePid = fFile.getZoid().toString();
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    dbFunctions.commitObject(opened.getZoid().toString(), opened.getZver().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ExecStatus fBlob = dbFunctions.createFBlob(
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
            ExecStatus commited = dbFunctions.commitObject(
                    recordId,
                    recordVer
            );
            if (!commited.isOk()) {
                throw new Exception(commited.getCaption()); // TODO
            }
        }
        return new HexFileResult(recordId, recordVer, filePid, params.getDestination());
    }

    @Override
    public String createDirectory(String path) throws Exception {
        File file = new File(path);
        String parentPath = file.getParent();
        parentPath = getFullPath(parentPath);
        String parentZoid = getLastLevelFromPath(parentPath);
        String scopeZoid = getFirstLevelFromPath(parentPath);
        DBFunctions dbFunctions = new DBFunctions(poolConnection);
        ExecStatus opened = dbFunctions.openObject(parentZoid);
        if (!opened.isOk()) {
            throw new Exception(opened.getCaption());
        }
        ExecStatus objectInScope = dbFunctions.createObjectInScope(scopeZoid);
        if (!objectInScope.isOk()) {
            throw new Exception(objectInScope.getCaption());
        }
        String dirName = path.substring(path.lastIndexOf('/') + 1);
        ExecStatus fFile = dbFunctions.createFFile(
                objectInScope.getZoid().toString(),
                objectInScope.getZver().toString(),
                null,
                FileType.DIRECTORY,
                dirName
        );
        if (!fFile.isOk()) {
            throw new Exception(fFile.getCaption()); // TODO
        }
        ExecStatus commited = dbFunctions.commitObject(
                objectInScope.getZoid().toString(),
                objectInScope.getZver().toString()
        );
        if (!commited.isOk()) {
            throw new Exception(commited.getCaption()); // TODO
        }
        ExecStatus fDir = dbFunctions.createFDir(
                opened.getZoid().toString(),
                opened.getZver().toString(),
                fFile.getZver().toString(),
                objectInScope.getZoid().toString(),
                dirName,
                String.format("%s directory created.", dirName)
        );
        if (!fDir.isOk()) {
            throw new Exception(fDir.getCaption()); // TODO
        }
        ExecStatus objectCommited = dbFunctions.commitObject(
                opened.getZoid().toString(),
                opened.getZver().toString()
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
        if (source.trim().equalsIgnoreCase("/")) {
            return "/";
        }
        String selectForPath = getSelectForPath(source);
        Connection connection = poolConnection.get();
        Statement statement = connection.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery(selectForPath);
            if (resultSet.next()) {
                String id = getValueOrEmpty(resultSet, ID);
                String zoid = getValueOrEmpty(resultSet, ZOID);
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
                    pathBuilder.append("/");
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

    @Override
    public boolean move(String source, String direction) throws IOException, CMSException {
        return false;
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
        return new DBFunctions(poolConnection).getFileInputStream(zoid);
    }

    @Override
    public FileDetails getFileDetails(String path) throws Exception {
        try {
            path = getFullPath(path);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Connection connection = poolConnection.get();
        String zoid = getLastLevelFromPath(path);
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
        DBFunctions functions = new DBFunctions(poolConnection);
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
        String parentZoid = getLastLevelFromPath(parentPath);

        DBFunctions dbFunctions = new DBFunctions(poolConnection);
        ExecStatus open = dbFunctions.openObject(parentZoid);
        if (!open.isOk()) {
            throw new Exception(open.getCaption());
        }
        ResultSet directoryByNameAndId = dbFunctions.getDirectoryByNameAndId(parentZoid, dirName);
        ExecStatus commit = null;
        try {
            directoryByNameAndId.next();
            long zrid = directoryByNameAndId.getLong(ZRID);
            ExecStatus delete = dbFunctions.deleteFDir(
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
            commit = dbFunctions.commitObject(parentZoid, String.valueOf(open.getZver()));
            directoryByNameAndId.close();
        }
        if (commit == null) {
            commit = dbFunctions.commitObject(parentZoid, String.valueOf(open.getZver()));
        }
        if (!commit.isOk()) {
            throw new Exception(open.getCaption());
        }
        return commit.isOk();
    }

    @SneakyThrows
    private List<CMSObject> processResultSetToCMSObjects(ResultSet resultSet, String fullPath) {
        List<CMSObject> objects = new ArrayList<>();
        DBFunctions functions = new DBFunctions(poolConnection);
        try {
            while (resultSet.next()) {
                try {
                    String name = getValueOrEmpty(resultSet, NAME);
                    String fname = getValueOrEmpty(resultSet, F_NAME);
                    CMSType type = getPathLvl(fullPath) < 2 ? CMSType.DIRECTORY : getType(resultSet);
                    String sid = getZsid(resultSet);
                    String zoid = getZoid(resultSet);
                    String fid = getFid(resultSet);
                    String descr = getValueOrEmpty(resultSet, DESCRIPTION);
                    String finalName = fname.isEmpty() ? name : fname;
                    String id = fid.isEmpty() ? zoid : fid;
                    objects.add(
                            CMSGeneralObject.builder()
                                    .id(id)
                                    .space(functions.getFileLength(id))
                                    .description(descr)
                                    .fullPath(new File(fullPath, finalName).getPath())
                                    .fileName(finalName)
                                    .type(type)
                                    .build()
                    );
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
