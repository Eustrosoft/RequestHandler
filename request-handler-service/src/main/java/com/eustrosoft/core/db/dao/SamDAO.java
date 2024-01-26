package com.eustrosoft.core.db.dao;

import com.eustrosoft.core.db.util.DBUtils;
import com.eustrosoft.core.model.user.User;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static com.eustrosoft.core.constants.Constants.UNKNOWN;
import static com.eustrosoft.core.constants.DBConstants.SID;

public class SamDAO extends BasicDAO {

    public SamDAO(QDBPConnection poolConnection) {
        super(poolConnection);
    }

    @SneakyThrows
    public Long getUserSid() {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = DBStatements.getFunctionStatement(connection, "SAM.get_user_sid");
        Long zsid = null;
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            zsid = resultSet.getLong("get_user_sid");
            preparedStatement.close();
            resultSet.close();
        }
        return zsid;
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
    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            return new User(UNKNOWN, UNKNOWN);
        }
        User user = new User();
        try {
            ResultSet userResultSetById = getUserResultSetById(id);
            userResultSetById.next();
            user.fillFromResultSet(userResultSetById);
            userResultSetById.close();
        } catch (Exception ex) {
            user.setId(id);
            if (!id.toString().isEmpty()) {
                user.setUsername(String.format("%s_%d", UNKNOWN, id));
                user.setFullName(String.format("%s_%d", UNKNOWN, id));
            } else {
                user.setUsername(UNKNOWN);
                user.setFullName(UNKNOWN);
            }
        }
        return user;
    }

    @SneakyThrows
    public ResultSet getUserResultSetById(Long id) {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM SAM.V_User where id = ?"
        );
        DBUtils.setLongOrNull(preparedStatement, 1, id);
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null;
    }

    @SneakyThrows
    public ResultSet getUserResultSet(String login) {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT * FROM SAM.V_User where db_user = ?");
        DBUtils.setStringOrNull(preparedStatement, 1, login);
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

    @SneakyThrows
    public List<Long> getZsids(String objectType) {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM SAM.V_ScopeCurrentUserCreatea where obj_type = ?"
        );
        DBUtils.setStringOrNull(preparedStatement, 1, objectType);
        List<Long> scopes = new ArrayList<>();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long sid = resultSet.getLong(SID);
                if (sid != null) {
                    scopes.add(sid);
                }
            }
            preparedStatement.close();
            resultSet.close();
        }
        return scopes;
    }
}
