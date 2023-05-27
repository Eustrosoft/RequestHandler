package com.eustrosoft.dbdatasource.core;

import com.eustrosoft.datasource.exception.CMSException;
import com.eustrosoft.datasource.sources.CMSDataSource;
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

import static com.eustrosoft.dbdatasource.constants.DBConstants.*;
import static com.eustrosoft.dbdatasource.core.DBStatements.getStatementForPath;
import static com.eustrosoft.dbdatasource.util.ResultSetUtils.*;

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
    public String createLink(String source, String target) {
        return null;
    }

    @Override
    public String createFile(String path, InputStream stream) throws Exception {
        return createFile(path, new File(path).getName());
    }

    @Override
    public String createFile(String path, String name) throws Exception {
        File file = new File(path);
        String parentPath = file.getParent();
        String parentZoid = parentPath.substring(parentPath.lastIndexOf('\\') + 1);
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
        String dirName = path.substring(path.lastIndexOf('\\'));
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
                fFile.getZoid().toString(),
                fFile.getZver().toString()
        );
        if (!commited.isOk()) {
            throw new Exception(commited.getCaption()); // TODO
        }
        return commited.getZoid().toString();
    }

    @Override
    public String createDirectory(String path) throws Exception {
        File file = new File(path);
        String parentPath = file.getParent();
        String parentZoid = parentPath.substring(parentPath.lastIndexOf('\\') + 1);
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
        String dirName = path.substring(path.lastIndexOf('\\') + 1);
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
                "null",
                fFile.getZoid().toString(),
                dirName
        );
        if (!fDir.isOk()) {
            throw new Exception(fDir.getCaption()); // TODO
        }
        ExecStatus objectCommited = dbFunctions.commitObject(
                objectInScope.getZoid().toString(),
                objectInScope.getZver().toString()
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
        ResultSet directoryByNameAndId = dbFunctions.getDirectoryByNameAndId(parentPath, dirName);
        if (directoryByNameAndId != null) {
            long zoid = directoryByNameAndId.getLong(ZOID);
            long zrid = directoryByNameAndId.getLong(ZRID);
            long zver = directoryByNameAndId.getLong(ZVER);
            ExecStatus delete = dbFunctions.deleteFDir(
                    String.valueOf(zoid),
                    String.valueOf(zrid),
                    String.valueOf(zver)
            );
            if (!delete.isOk()) {
                throw new Exception(open.getCaption());
            }
        }
        ExecStatus commit = dbFunctions.commitObject(parentZoid, String.valueOf(open.getZver()));
        if (!commit.isOk()) {
            throw new Exception(open.getCaption());
        }
        return commit.isOk();
    }

    private String getFirstLevelFromPath(String path) {
        String firSlashRem = path.substring(1);
        int nextSlash = firSlashRem.indexOf('\\');
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
                    String descr = getValueOrEmpty(resultSet, DESCRIPTION);
                    objects.add(
                            CMSGeneralObject.builder()
                                    .id(zoid)
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
