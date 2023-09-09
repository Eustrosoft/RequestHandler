package com.eustrosoft.core.db.dao;

import com.eustrosoft.core.db.ExecStatus;
import com.eustrosoft.core.db.Query;
import lombok.Getter;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.eustrosoft.core.constants.DBConstants.ZOID;

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
    public ExecStatus openObject(String type, Long zoid) {
        return openObject(type, zoid, "null");
    }

    @SneakyThrows
    public ExecStatus openObject(String type, Long zoid, String zver) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("TIS.open_object")
                        .leftBracket()
                        .add(String.format(
                                "'%s', %s, %s",
                                type, zoid, zver
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
    public ExecStatus createObjectInScope(String type, String scopeZoid) {
        return createObjectInScope(type, scopeZoid, "null");
    }

    @SneakyThrows
    public ExecStatus createObjectInScope(String type, String scopeZoid, String zlvl) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("TIS.create_object")
                        .leftBracket()
                        .add(String.format(
                                "'%s', %s, %s::smallint",
                                type, scopeZoid, zlvl
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
    public ExecStatus commitObject(String type, Long objectZoid, Long objectVer) {
        Connection connection = poolConnection.get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("TIS.commit_object")
                        .leftBracket()
                        .add(String.format(
                                "'%s', %s, %s",
                                type,
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
}
