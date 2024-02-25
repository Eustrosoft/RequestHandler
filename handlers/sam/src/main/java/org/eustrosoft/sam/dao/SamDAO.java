package org.eustrosoft.sam.dao;

import org.eustrosoft.constants.Constants;
import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.core.db.dao.BasicDAO;
import org.eustrosoft.core.db.dao.DBStatements;
import org.eustrosoft.core.db.model.User;
import org.eustrosoft.core.db.util.DBUtils;
import org.eustrosoft.qdbp.QDBPConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SamDAO extends BasicDAO {

    public SamDAO(QDBPConnection poolConnection) {
        super(poolConnection);

    }

    public Long getUserSid() throws SQLException {
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

    public Long getUserId() throws SQLException {
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

    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            return new User(Constants.UNKNOWN, Constants.UNKNOWN);
        }
        User user = new User();
        try {
            ResultSet userResultSetById = getUserResultSetById(id);
            userResultSetById.next();
            user.fillFromResultSet(userResultSetById);
            userResultSetById.close();
        } catch (Exception ex) {
            user.setZOID(id);
            if (id != null) {
                user.setUsername(String.format("%s_%d", Constants.UNKNOWN, id));
                user.setFullName(String.format("%s_%d", Constants.UNKNOWN, id));
            } else {
                user.setUsername(Constants.UNKNOWN);
                user.setFullName(Constants.UNKNOWN);
            }
        }
        return user;
    }

    public ResultSet getUserResultSetById(Long id) throws SQLException {
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

    public ResultSet getUserResultSet(String login) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT * FROM SAM.V_User where db_user = ?");
        DBUtils.setStringOrNull(preparedStatement, 1, login);
        if (preparedStatement != null) {
            return preparedStatement.executeQuery();
        }
        return null;
    }

    public String getUserLogin() throws SQLException {
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

    public String getUserLang() throws SQLException {
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

    public Integer getUserSLvl() throws SQLException {
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

    public int[] getUserAvailableSlvl() throws SQLException {
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

    public List<Long> getZsids(String objectType) throws SQLException {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM SAM.V_ScopeCurrentUserCreatea where obj_type = ?"
        );
        DBUtils.setStringOrNull(preparedStatement, 1, objectType);
        List<Long> scopes = new ArrayList<>();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long sid = resultSet.getLong(DBConstants.SID);
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
