/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.dbdatasource.core;

import com.eustrosoft.dbdatasource.core.model.FDir;
import com.eustrosoft.dbdatasource.core.model.FFile;
import com.eustrosoft.dbdatasource.queries.Query;
import com.eustrosoft.dbdatasource.ranges.FileType;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPConnection;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import static com.eustrosoft.dbdatasource.constants.DBConstants.*;

public final class DBFunctions {
    private QDBPConnection poolConnection;

    public DBFunctions(QDBPConnection poolConnection) {
        this.poolConnection = poolConnection;
    }

    @SneakyThrows
    public ResultSet selectObject(String zoid) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("TIS.V_ZObject")
                        .where(String.format("%s = %s", ZOID, zoid))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null; // todo
    }

    @SneakyThrows
    public Long getFileLength(String zoid) {
        Connection connection = poolConnection.get();
        PreparedStatement blobLengthPS = DBStatements.getBlobLength(connection, zoid);
        try {
            ResultSet resultSet = blobLengthPS.executeQuery();
            if (resultSet != null) {
                resultSet.next();
                return resultSet.getLong("sum");
            }
        } finally {
            blobLengthPS.close();
        }
        return -1L;
    }

    @SneakyThrows
    public InputStream getFileInputStream(String zoid) {
        Connection connection = poolConnection.get();
        PreparedStatement blobDetailsPS = DBStatements.getBlobDetails(connection, zoid);
        try {
            ResultSet resultSet = blobDetailsPS.executeQuery();
            Vector<InputStream> streams = new Vector<>();
            while (resultSet.next()) {
                streams.add(resultSet.getBinaryStream("chunk"));
            }
            return new SequenceInputStream(streams.elements());
        } finally {
            blobDetailsPS.close();
        }
    }

    @SneakyThrows
    public ExecStatus openObject(String zoid) {
        return openObject(zoid, "null");
    }

    @SneakyThrows
    public ExecStatus openObject(String zoid, String zver) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("TIS.open_object")
                        .leftBracket()
                        .add(String.format(
                                "'%s', %s, %s",
                                "FS.F", zoid, zver
                        ))
                        .rightBracket()
                        .buildWithSemicolon()
                        .toString()
        );
        ExecStatus status = new ExecStatus();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            status.fillFromResultSet(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        return status;
    }

    @SneakyThrows
    public ExecStatus createObjectInScope(String scopeZoid) {
        return createObjectInScope(scopeZoid, "null");
    }

    @SneakyThrows
    public ExecStatus createObjectInScope(String scopeZoid, String zlvl) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("TIS.create_object")
                        .leftBracket()
                        .add(String.format(
                                "'%s', %s, %s::smallint",
                                "FS.F", scopeZoid, zlvl
                        ))
                        .rightBracket()
                        .buildWithSemicolon()
                        .toString()
        );
        ExecStatus status = new ExecStatus();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            status.fillFromResultSet(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        return status;
    }

    @SneakyThrows
    public ExecStatus createFFile(String objectZoid, String objectVer, String parentVer,
                                  FileType type, String name) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("FS.create_FFile")
                        .leftBracket()
                        .add(String.format(
                                "%s, %s, %s, '%s', '%s', '%s', null, null, null, null, null, null, null, null, null, null",
                                objectZoid,
                                objectVer,
                                parentVer,
                                name,
                                type.getValue(),
                                "N" // TODO
                        ))
                        .rightBracket()
                        .buildWithSemicolon()
                        .toString()
        );
        ExecStatus status = new ExecStatus();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            status.fillFromResultSet(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        return status;
    }

    @SneakyThrows
    public ExecStatus createFFile(String objectZoid, String objectVer, String parentVer,
                                  FileType type, String name, String securityLevel, String description) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("FS.create_FFile")
                        .leftBracket()
                        .add(String.format(
                                "%s, %s, %s, '%s', '%s', '%s', null, null, null, null, null, null, null, null, null, null",
                                objectZoid,
                                objectVer,
                                parentVer,
                                name,
                                type.getValue(),
                                "N" // TODO
                        ))
                        .rightBracket()
                        .buildWithSemicolon()
                        .toString()
        );
        ExecStatus status = new ExecStatus();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            status.fillFromResultSet(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        return status;
    }

    @SneakyThrows
    public ExecStatus commitObject(String objectZoid, String objectVer) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("TIS.commit_object")
                        .leftBracket()
                        .add(String.format(
                                "'%s', %s, %s",
                                "FS.F",
                                objectZoid,
                                objectVer
                        ))
                        .rightBracket()
                        .buildWithSemicolon()
                        .toString()
        );
        ExecStatus status = new ExecStatus();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            status.fillFromResultSet(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        return status;
    }

    @SneakyThrows
    public ExecStatus createFDir(String objectZoid, String objectVer, String parentZrid,
                                 String fileZoid, String name, String description) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("FS.create_FDir")
                        .leftBracket()
                        .add(String.format(
                                "%s, %s, %s, %s, '%s', null, '%s'",
                                objectZoid,
                                objectVer,
                                "1", // TODO
                                fileZoid,
                                name,
                                description
                        ))
                        .rightBracket()
                        .buildWithSemicolon()
                        .toString()
        );
        ExecStatus status = new ExecStatus();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            status.fillFromResultSet(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        return status;
    }

    @SneakyThrows
    public ExecStatus createFBlob(String zoid, String zver, String zpid,
                                  String hex, String chunk, String allChunks, String crc32) {
        Connection connection = poolConnection.get();
        String query = Query.builder()
                .select()
                .add("FS.create_FBlob")
                .leftBracket()
                .add(String.format(
                        "%s, %s, %s, '\\x%s', %s, %s, %s",
                        zoid,
                        zver,
                        zpid,
                        hex,
                        chunk,
                        allChunks,
                        Integer.parseInt(crc32.substring(3), 16)
                ))
                .rightBracket()
                .buildWithSemicolon()
                .toString();
        Statement statement = connection.createStatement();
        ExecStatus status = new ExecStatus();
        if (statement != null) {
            ResultSet resultSet = statement.executeQuery(query);
            status.fillFromResultSet(resultSet);
            statement.close();
            resultSet.close();
        }
        return status;
    }

    @SneakyThrows
    public ExecStatus deleteFDir(String zoid, String zrid, String zver) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("FS.delete_FDir")
                        .leftBracket()
                        .add(String.format(
                                "%s, %s, %s",
                                zoid, zver, zrid
                        ))
                        .rightBracket()
                        .buildWithSemicolon()
                        .toString()
        );
        ExecStatus status = new ExecStatus();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            status.fillFromResultSet(resultSet);
            preparedStatement.close();
            resultSet.close();
        }
        return status;
    }

    @SneakyThrows
    public ResultSet getDirectoryByNameAndId(String dirId, String dirName) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("FS.V_FDir")
                        .where(String.format("%s = %s and %s = %s", ZOID, dirId, FILE_ID, dirName))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null; // todo
    }

    @SneakyThrows
    public void renameFile(String zoid, String name, String targetName) {
        FDir fDir = getFDir(zoid, name);
        FFile fFile = getFFile(zoid, name);
        if (fDir == null) {
            throw new Exception("FDir was null while renaming");
        }
        fDir.setFileName(targetName);
        updateFDir(fDir);
        if (fFile == null) {
            throw new Exception("FFile was null while renaming");
        }
        fFile = getFFile(zoid, name);
        fFile.setFileName(targetName);
        updateFFile(fFile);
    }

    @SneakyThrows
    public FFile getFFile(String zoid, String name) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("FS.V_FFile")
                        .where(String.format("%s = %s and %s = '%s'", ZOID, zoid, NAME, name))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
                return new FFile(resultSet);
            } finally {
                preparedStatement.close();
                resultSet.close();
            }
        }
        return null;
    }

    @SneakyThrows
    public FDir getFDir(String zoid, String name) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("FS.V_FDir")
                        .where(String.format("%s = %s and %s = '%s'", FILE_ID, zoid, F_NAME, name))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
                return new FDir(resultSet);
            } finally {
                preparedStatement.close();
                resultSet.close();
            }
        }
        return null;
    }

    @SneakyThrows
    public void updateFDir(FDir fDir) {
        ExecStatus status = null;
        ExecStatus fDirOpen = null;
        try {
            if (fDir == null) {
                throw new Exception("FDir is null while updating.");
            }
            status = openObject(fDir.getFileId().toString());
            fDirOpen = openObject(fDir.getZoid().toString());
            fDir.setZver(fDirOpen.getZver());
            if (status.isOk() && fDirOpen.isOk()) {
                Connection connection = poolConnection.get();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        Query.builder()
                                .select()
                                .add("FS.update_FDir")
                                .leftBracket()
                                .add(fDir.toUpdateString())
                                .rightBracket()
                                .buildWithSemicolon()
                                .toString()
                );
                ExecStatus updatedStatus = new ExecStatus();
                if (preparedStatement != null) {
                    ResultSet resultSet = preparedStatement.executeQuery();
                    updatedStatus.fillFromResultSet(resultSet);
                    preparedStatement.close();
                    resultSet.close();
                }
            }
        } finally {
            if (status != null) {
                commitObject(status.getZoid().toString(), status.getZver().toString());
            }
            if (fDirOpen != null) {
                commitObject(fDirOpen.getZoid().toString(), fDirOpen.getZver().toString());
            }
        }
    }

    @SneakyThrows
    public void updateFFile(FFile fFile) {
        ExecStatus status = null;
        try {
            if (fFile == null) {
                throw new Exception("FFile is null while updating.");
            }
            status = openObject(fFile.getZoid().toString());
            fFile.setZver(status.getZver());
            if (status.isOk()) {
                Connection connection = poolConnection.get();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        Query.builder()
                                .select()
                                .add("FS.update_FFile")
                                .leftBracket()
                                .add(fFile.toUpdateString())
                                .rightBracket()
                                .buildWithSemicolon()
                                .toString()
                );
                ExecStatus updatedStatus = new ExecStatus();
                if (preparedStatement != null) {
                    ResultSet resultSet = preparedStatement.executeQuery();
                    updatedStatus.fillFromResultSet(resultSet);
                    preparedStatement.close();
                    resultSet.close();
                }
            }
        } finally {
            if (status != null) {
                commitObject(status.getZoid().toString(), status.getZver().toString());
            }
        }
    }
}
