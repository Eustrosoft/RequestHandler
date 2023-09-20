package com.eustrosoft.core.db.dao;

import com.eustrosoft.core.db.Query;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SamDAO extends BasicDAO {

    public SamDAO(QDBPConnection poolConnection) {
        super(poolConnection);
    }

    @SneakyThrows
    public String getUserSid() {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = DBStatements.getFunctionStatement(connection, "SAM.get_user_sid");
        String userLogin = null;
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            userLogin = resultSet.getString("get_user_sid");
            preparedStatement.close();
            resultSet.close();
        }
        return userLogin;
    }

    @SneakyThrows
    public Long getUserId() {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = DBStatements.getFunctionStatement(connection, "SAM.get_user");
        Long userId = null;
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            userId = resultSet.getLong("get_user");
            preparedStatement.close();
            resultSet.close();
        }
        return userId;
    }

    @SneakyThrows
    public ResultSet getUserResultSetById(Long id) {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("SAM.V_User")
                        .where(String.format("id = %s", id))
                        .buildWithSemicolon()
                        .toString()
        );
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null;
    }

    @SneakyThrows
    public ResultSet getUserResultSet(String login) {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement =
                connection.prepareStatement(String.format("SELECT * FROM SAM.V_User where db_user = '%s'", login));
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null;
    }

    @SneakyThrows
    public String getUserLogin() {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = DBStatements.getFunctionStatement(connection, "SAM.get_user_login");
        String userLogin = null;
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            userLogin = resultSet.getString("get_user_login");
            preparedStatement.close();
            resultSet.close();
        }
        return userLogin;
    }

    @SneakyThrows
    public String getUserLang() {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = DBStatements.getFunctionStatement(connection, "SAM.get_user_lang");
        String userLang = null;
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            userLang = resultSet.getString("get_user_lang");
            preparedStatement.close();
            resultSet.close();
        }
        return userLang;
    }

    @SneakyThrows
    public Integer getUserSLvl() {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = DBStatements.getFunctionStatement(connection, "SAM.get_user_slevel");
        Integer userSlvl = null;
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            userSlvl = resultSet.getInt("get_user_slevel");
            preparedStatement.close();
            resultSet.close();
        }
        return userSlvl;
    }

    @SneakyThrows
    public int[] getUserAvailableSlvl() {
        Connection connection = getPoolConnection().get();
        int[] slvls = new int[2];
        PreparedStatement minPrep = DBStatements.getFunctionStatement(connection, "SAM.get_user_slevel_min");
        if (minPrep != null) {
            ResultSet resultSet = minPrep.executeQuery();
            resultSet.next();
            slvls[0] = resultSet.getInt("get_user_slevel_min");
            minPrep.close();
            resultSet.close();
        }
        PreparedStatement maxPrep = DBStatements.getFunctionStatement(connection, "SAM.get_user_slevel_max");
        if (maxPrep != null) {
            ResultSet resultSet = maxPrep.executeQuery();
            resultSet.next();
            slvls[1] = resultSet.getInt("get_user_slevel_max");
            maxPrep.close();
            resultSet.close();
        }
        return slvls;
    }
}
