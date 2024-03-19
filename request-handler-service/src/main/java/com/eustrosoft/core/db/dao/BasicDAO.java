/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.db.dao;

import com.eustrosoft.core.db.ExecStatus;
import org.eustrosoft.qdbp.QDBPConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.eustrosoft.core.constants.DBConstants.ZOID;
import static com.eustrosoft.core.constants.DBConstants.ZVER;
import static com.eustrosoft.core.db.util.DBUtils.setCharOrNull;
import static com.eustrosoft.core.db.util.DBUtils.setLongOrNull;
import static com.eustrosoft.core.db.util.DBUtils.setShortOrNull;
import static com.eustrosoft.core.db.util.DBUtils.setStringOrNull;

public class BasicDAO {
    private final QDBPConnection poolConnection;

    public QDBPConnection getPoolConnection() {
        return poolConnection;
    }

    public BasicDAO(QDBPConnection poolConnection) {
        this.poolConnection = poolConnection;
    }

    public ResultSet selectObject(Long zoid) throws SQLException {
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

    public ExecStatus openObject(String type, Long zoid) throws Exception {
        return openObject(type, zoid, (Long) null);
    }

    @Deprecated
    public ExecStatus openObject(String type, Long zoid, Long zver) throws Exception {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT TIS.open_object(?, ?, ?)"
        );
        preparedStatement.setString(1, type);
        setLongOrNull(preparedStatement, 2, zoid);
        setLongOrNull(preparedStatement, 3, zver);
        return execute(preparedStatement);
    }

    @Deprecated
    public ExecStatus openObject(String type, Long zoid, String zver) throws Exception {
        if (zver.equalsIgnoreCase("null")) {
            return openObject(type, zoid, (Long) null);
        }
        return openObject(type, zoid, Long.valueOf(zver));
    }

    public ExecStatus deleteObject(String type, Long zoid, Long zver) throws Exception {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT TIS.delete_object(?, ?, ?)"
        );
        preparedStatement.setString(1, type);
        setLongOrNull(preparedStatement, 2, zoid);
        setLongOrNull(preparedStatement, 3, zver);
        return execute(preparedStatement);
    }

    public ExecStatus createObjectInScope(String type, String scopeZoid) throws Exception {
        Long scopeProc = null;
        if (scopeZoid != null && !scopeZoid.equals("null")) {
            scopeProc = Long.parseLong(scopeZoid);
        }
        return createObjectInScope(type, scopeProc);
    }

    public ExecStatus createObjectInScope(String type, Long scopeZoid) throws Exception {
        return createObjectInScope(type, scopeZoid, null);
    }

    public ExecStatus createObjectInScope(String type, String scopeZoid, String slvl) throws Exception {
        Short slvlProc = null;
        if (slvl != null && !slvl.equals("null")) {
            slvlProc = Short.parseShort(slvl);
        }
        Long scopeProc = null;
        if (scopeZoid != null && !scopeZoid.equals("null")) {
            scopeProc = Long.parseLong(scopeZoid);
        }
        return createObjectInScope(type, scopeProc, slvlProc);
    }

    public ExecStatus createObjectInScope(String type, Long scopeZoid, Short slvl) throws Exception {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT TIS.create_object(?, ?, ?)"
        );
        setStringOrNull(preparedStatement, 1, type);
        setLongOrNull(preparedStatement, 2, scopeZoid);
        setShortOrNull(preparedStatement, 3, slvl);
        return execute(preparedStatement);
    }

    public ExecStatus commitObject(String type, Long objectZoid, Long objectVer) throws Exception {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT TIS.commit_object(?, ?, ?)"
        );
        setStringOrNull(preparedStatement, 1, type);
        setLongOrNull(preparedStatement, 2, objectZoid);
        setLongOrNull(preparedStatement, 3, objectVer);
        return execute(preparedStatement);
    }

    public ExecStatus rollbackObject(String type, Long objectZoid, Long objectVer) throws Exception {
        return rollbackObject(type, objectZoid, objectVer, 'Y');
    }

    // Character = 'Y'/'N'
    public ExecStatus rollbackObject(String type, Long objectZoid, Long objectVer, Character force) throws Exception {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT TIS.rollback_object(?, ?, ?, ?)"
        );
        setStringOrNull(preparedStatement, 1, type);
        setLongOrNull(preparedStatement, 2, objectZoid);
        setLongOrNull(preparedStatement, 3, objectVer);
        setCharOrNull(preparedStatement, 4, force);
        return execute(preparedStatement);
    }

    public ExecStatus setZLvl(String type, Long objectZoid, Long objectVer, Short zlvl) throws Exception {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT TIS.set_slevel(?, ?, ?, ?)"
        );
        setStringOrNull(preparedStatement, 1, type);
        setLongOrNull(preparedStatement, 2, objectZoid);
        setLongOrNull(preparedStatement, 3, objectVer);
        setShortOrNull(preparedStatement, 4, zlvl);
        return execute(preparedStatement);
    }

    public String getZType(Long zoid, Long zver) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = DBStatements.getFunctionStatement(
                connection,
                "TIS.zobject",
                String.format("%s = %s", ZOID, zoid),
                String.format("%s = %s", ZVER, zver)
        );
        String ztype = null;
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            ztype = resultSet.getString("ztype");
            preparedStatement.close();
            resultSet.close();
        }
        return ztype;
    }


    protected ExecStatus execute(PreparedStatement preparedStatement) throws Exception {
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
