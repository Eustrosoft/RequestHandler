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
import java.util.ArrayList;
import java.util.List;

import static com.eustrosoft.dbdatasource.constants.DBConstants.DESCRIPTION;
import static com.eustrosoft.dbdatasource.constants.DBConstants.F_NAME;
import static com.eustrosoft.dbdatasource.constants.DBConstants.NAME;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZLVL;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZOID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZRID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZSID;
import static com.eustrosoft.dbdatasource.core.DBStatements.getStatementForPath;
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
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = getStatementForPath(connection, path);
        List<CMSObject> cmsObjects = new ArrayList<>();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            cmsObjects = processResultSetToCMSObjects(resultSet, new File(path).getPath());
            preparedStatement.close();
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
        String dest = params.getDestination();
        String crc32 = params.getCrc32();
        String recordId = params.getRecordId();
        String recordVer = params.getRecordVer();
        String filePid = params.getFilePid();
        String hex = params.getHex();
        Long chunk = params.getChunkNumber();
        Long chunkCount = params.getChunkCount();

        File file = new File(dest);
        String filePath = file.getPath();
        String parentZoid = filePath.substring(filePath.lastIndexOf('/') + 1);
        DBFunctions dbFunctions = new DBFunctions(poolConnection);
        ResultSet selectObject = dbFunctions.selectObject(parentZoid);
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
            ExecStatus commited = dbFunctions.commitObject(
                    recordId,
                    recordVer
            );
            if (!commited.isOk()) {
                throw new Exception(commited.getCaption()); // TODO
            }
        }
        return new HexFileResult(recordId, recordVer, filePid, filePath);
    }

    @Override
    public String createDirectory(String path) throws Exception {
        File file = new File(path);
        String parentPath = file.getParent();
        String parentZoid = parentPath.substring(parentPath.lastIndexOf('/') + 1);
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
    public String getFullPath(String source) {
        return null;
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
        String zoid = getLastLevelFromPath(path);
        return new DBFunctions(poolConnection)
                .getFileInputStream(zoid);
    }

    @Override
    public FileDetails getFileDetails(String path) throws Exception {
        Connection connection = poolConnection.get();
        String zoid = getLastLevelFromPath(path);
        PreparedStatement fileDetailsPS = DBStatements.getFileDetails(connection, zoid);
        FileDetails fileDetails = new FileDetails();
        try {
            ResultSet resultSet = fileDetailsPS.executeQuery();
            if (resultSet != null) {
                resultSet.next();
                if (!resultSet.getString("type").equals("B")) {
                    throw new Exception("Type not match."); // todo: create file details for dir
                }
                fileDetails.setMimeType(resultSet.getString("mimetype"));
                fileDetails.setFileName(resultSet.getString("name"));
                if (resultSet.next()) {
                    throw new Exception("More than 1 file present in this path.\nCall administrator to resolve.");
                }
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
        File file = new File(path);
        String dirName = file.getName();
        String parentPath = file.getParent();
        String parentZoid = parentPath.substring(parentPath.lastIndexOf('/') + 1);

        DBFunctions dbFunctions = new DBFunctions(poolConnection);
        ExecStatus open = dbFunctions.openObject(parentZoid);
        if (!open.isOk()) {
            throw new Exception(open.getCaption());
        }
        ResultSet directoryByNameAndId = dbFunctions.getDirectoryByNameAndId(parentZoid, dirName);
        ExecStatus commit = null;
        if (directoryByNameAndId != null) {
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
                commit = dbFunctions.commitObject(parentZoid, String.valueOf(open.getZver()));
                directoryByNameAndId.close();
            }
        }
        if (commit == null) {
            commit = dbFunctions.commitObject(parentZoid, String.valueOf(open.getZver()));
        }
        if (!commit.isOk()) {
            throw new Exception(open.getCaption());
        }
        return commit.isOk();
    }

    private String getFirstLevelFromPath(String path) {
        String firSlashRem = path.substring(1);
        int nextSlash = firSlashRem.indexOf('/');
        if (nextSlash == -1) {
            return firSlashRem;
        } else {
            return firSlashRem.substring(0, nextSlash);
        }
    }

    private String getLastLevelFromPath(String path) throws Exception {
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1) {
            throw new Exception("Illegal path.");
        } else {
            return path.substring(lastSlash + 1);
        }
    }

    @SneakyThrows
    private List<CMSObject> processResultSetToCMSObjects(ResultSet resultSet, String fullPath) {
        List<CMSObject> objects = new ArrayList<>();
        try {
            while (resultSet.next()) {
                try {
                    String name = getValueOrEmpty(resultSet, NAME);
                    String fname = getValueOrEmpty(resultSet, F_NAME);
                    CMSType type = getType(resultSet);
                    String sid = getZsid(resultSet);
                    String zoid = getZoid(resultSet);
                    String fid = getFid(resultSet);
                    String descr = getValueOrEmpty(resultSet, DESCRIPTION);
                    objects.add(
                            CMSGeneralObject.builder()
                                    .id(fid.isEmpty() ? zoid : fid)
                                    .description(descr)
                                    .fullPath(fullPath)
                                    .fileName(fname)
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
