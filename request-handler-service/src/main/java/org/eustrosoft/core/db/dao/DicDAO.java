package org.eustrosoft.core.db.dao;

import lombok.SneakyThrows;
import org.eustrosoft.core.constants.DBConstants;
import org.eustrosoft.core.model.DIC;
import org.eustrosoft.qdbp.QDBPConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DicDAO extends BasicDAO {

    public DicDAO(QDBPConnection poolConnection) {
        super(poolConnection);
    }

    @SneakyThrows
    public List<String> getDicNames() {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT DISTINCT dic FROM DIC.V_QDIC ORDER BY dic"
        );
        List<String> values = new ArrayList<>();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
                values = getDicNamesFromRS(resultSet);
            } finally {
                preparedStatement.close();
                if (resultSet != null) {
                    resultSet.close();
                }
            }
        }
        return values;
    }

    @SneakyThrows
    public List<DIC> getDictionaryValues(String dicName) {
        Connection connection = getPoolConnection().get();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM DIC.V_QDIC where dic = ?"
        );
        preparedStatement.setString(1, dicName);
        List<DIC> dics = new ArrayList<>();
        if (preparedStatement != null) {
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
                while (resultSet.next()) {
                    DIC dic = new DIC();
                    dic.fillFromResultSet(resultSet);
                    dics.add(dic);
                }
            } finally {
                preparedStatement.close();
                if (resultSet != null) {
                    resultSet.close();
                }
            }
        }
        return dics;
    }

    private List<String> getDicNamesFromRS(ResultSet resultSet) throws SQLException {
        List<String> values = new ArrayList<>();
        while (resultSet.next()) {
            String nString = resultSet.getString(DBConstants.DIC);
            if (nString != null && !nString.isEmpty()) {
                values.add(nString.trim());
            }
        }
        return values;
    }
}
