package com.eustrosoft.dbdatasource.core;

import com.eustrosoft.dbdatasource.queries.Query;
import com.eustrosoft.dbdatasource.ranges.FileType;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.eustrosoft.dbdatasource.constants.DBConstants.F_NAME;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZOID;

public final class DBFunctions {
    private QDBPConnection poolConnection;

    public DBFunctions(QDBPConnection poolConnection) {
        this.poolConnection = poolConnection;
    }

    @SneakyThrows
    public ExecStatus openObject(String zoid) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("TIS.open_object")
                        .leftBracket()
                        .add(String.format(
                                "'%s', %s, %s",
                                "FS.F", zoid, "null" // TODO
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
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("TIS.create_object")
                        .leftBracket()
                        .add(String.format(
                                "'%s', %s, %s",
                                "FS.F", scopeZoid, "null"
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
    public ExecStatus createFDir(String objectZoid, String objectVer, String parentVer,
                                 String fileZoid, String name) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("FS.create_FDir")
                        .leftBracket()
                        .add(String.format(
                                "%s, %s, %s, %s, %s",
                                objectZoid,
                                objectVer,
                                parentVer,
                                fileZoid,
                                name
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
    public ExecStatus getFDir(String objectZoid, String objectVer) {
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
                        .where(String.format("%s = %s and %s == '%s'", ZOID, dirId, F_NAME, dirName))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            resultSet.close();
            return resultSet;
        }
        return null; // todo
    }
}
