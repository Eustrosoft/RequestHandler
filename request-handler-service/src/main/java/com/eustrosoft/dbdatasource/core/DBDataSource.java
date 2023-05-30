package com.eustrosoft.dbdatasource.core;

import com.eustrosoft.datasource.exception.CMSException;
import com.eustrosoft.datasource.sources.CMSDataSource;
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
            cmsObjects = processResultSetToCMSObjects(resultSet);
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
        String dist = params.getDestination();
        String crc32 = params.getCrc32();
        String recordId = params.getRecordId();
        String recordVer = params.getRecordVer();
        String filePid = params.getFilePid();
        String hex = params.getHex();
        Long chunk = params.getChunkNumber();
        Long chunkCount = params.getChunkCount();

        File file = new File(dist);
        String filePath = file.getPath();
        String parentZoid = filePath.substring(filePath.lastIndexOf('/') + 1);
        DBFunctions dbFunctions = new DBFunctions(poolConnection);
        ResultSet selectObject = dbFunctions.selectObject(parentZoid);
        String zoid = null;
        String zlvl = null;
        try {
            if (selectObject != null) {
                selectObject.next();
                zoid = selectObject.getString(ZOID);
                zlvl = selectObject.getString(ZLVL);
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
            String fileName = filePath.substring(filePath.lastIndexOf('/'));
            ExecStatus opened = dbFunctions.openObject(zoid);
            if (!opened.isOk()) {
                throw new Exception(opened.getCaption());
            }
            ExecStatus objectInScope = dbFunctions.createObjectInScope(zoid, zlvl);
            if (!objectInScope.isOk()) {
                throw new Exception(objectInScope.getCaption());
            }
            ExecStatus fDir = dbFunctions.createFDir(
                    objectInScope.getZoid().toString(),
                    objectInScope.getZver().toString(),
                    zoid,
                    null,
                    fileName,
                    "FDir was created"
            );
            if (!fDir.isOk()) {
                throw new Exception(fDir.getCaption()); // TODO
            }
            recordId = fDir.getZoid().toString();
            recordVer = fDir.getZver().toString();
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
        }
        ExecStatus fBlob = dbFunctions.createFBlob(
                recordId,
                recordId,
                filePid,
                hex,
                String.valueOf(chunk),
                String.valueOf(chunkCount),
                crc32
        );
        if (chunk == chunkCount - 1) {
            ExecStatus commited = dbFunctions.commitObject(
                    fBlob.getZoid().toString(),
                    fBlob.getZver().toString()
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

    @SneakyThrows
    private List<CMSObject> processResultSetToCMSObjects(ResultSet resultSet) {
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
                                    .fullPath(name)
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
