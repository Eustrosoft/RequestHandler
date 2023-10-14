/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.db.dao;

import com.eustrosoft.core.db.ExecStatus;
import lombok.Getter;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.eustrosoft.core.db.util.DBUtils.setLongOrNull;
import static com.eustrosoft.core.db.util.DBUtils.setShortOrNull;
import static com.eustrosoft.core.db.util.DBUtils.setStringOrNull;

public class BasicDAO {
    @Getter
    private final QDBPConnection poolConnection;

    public BasicDAO(QDBPConnection poolConnection) {
        this.poolConnection = poolConnection;
    }

    @SneakyThrows
    public ResultSet selectObject(Long zoid) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM TIS.V_ZObject WHERE ZOID = ?"
        );
        preparedStatement.setLong(1, zoid);
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null; // todo
    }

    @SneakyThrows
    public ExecStatus openObject(String type, Long zoid) {
        return openObject(type, zoid, (Long) null);
    }

    @SneakyThrows
    @Deprecated
    public ExecStatus openObject(String type, Long zoid, Long zver) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT TIS.open_object(?, ?, ?)"
        );
        preparedStatement.setString(1, type);
        setLongOrNull(preparedStatement, 2, zoid);
        setLongOrNull(preparedStatement, 3, zver);
        return execute(preparedStatement);
    }

    @SneakyThrows
    @Deprecated
    public ExecStatus openObject(String type, Long zoid, String zver) {
        if (zver.equalsIgnoreCase("null")) {
            return openObject(type, zoid, (Long) null);
        }
        return openObject(type, zoid, Long.valueOf(zver));
    }

    @SneakyThrows
    public ExecStatus deleteObject(String type, Long zoid, Long zver) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT TIS.delete_object(?, ?, ?)"
        );
        preparedStatement.setString(1, type);
        setLongOrNull(preparedStatement, 2, zoid);
        setLongOrNull(preparedStatement, 3, zver);
        return execute(preparedStatement);
    }

    @SneakyThrows
    public ExecStatus createObjectInScope(String type, String scopeZoid) {
        return createObjectInScope(type, Long.valueOf(scopeZoid));
    }

    @SneakyThrows
    public ExecStatus createObjectInScope(String type, String scopeZoid, Short slvl) {
        return createObjectInScope(type, Long.valueOf(scopeZoid), slvl);
    }

    @SneakyThrows
    public ExecStatus createObjectInScope(String type, Long scopeZoid) {
        return createObjectInScope(type, scopeZoid, null);
    }

    @SneakyThrows
    public ExecStatus createObjectInScope(String type, String scopeZoid, String slvl) {
        return createObjectInScope(type, Long.valueOf(scopeZoid), Short.valueOf(slvl));
    }

    @SneakyThrows
    public ExecStatus createObjectInScope(String type, Long scopeZoid, Short slvl) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT TIS.create_object(?, ?, ?)"
        );
        setStringOrNull(preparedStatement, 1, type);
        setLongOrNull(preparedStatement, 2, scopeZoid);
        setShortOrNull(preparedStatement, 3, slvl);
        return execute(preparedStatement);
    }

    @SneakyThrows
    public ExecStatus commitObject(String type, Long objectZoid, Long objectVer) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT TIS.commit_object(?, ?, ?)"
        );
        setStringOrNull(preparedStatement, 1, type);
        setLongOrNull(preparedStatement, 2, objectZoid);
        setLongOrNull(preparedStatement, 3, objectVer);
        return execute(preparedStatement);
    }

    @SneakyThrows
    protected ExecStatus execute(PreparedStatement preparedStatement) {
        if (preparedStatement == null) {
            throw new NullPointerException("Prepared Statement was null");
        }
        ResultSet resultSet = preparedStatement.executeQuery();
        ExecStatus status = new ExecStatus();
        status.fillFromResultSet(resultSet);
        preparedStatement.close();
        resultSet.close();
        if (!status.isOk()) {
            throw new Exception(status.getCaption());
        }
        return status;
    }
}
