package com.eustrosoft.dbdatasource.core.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetConverter<T> {

    <T extends DBObject> void fillFromResultSet(ResultSet resultSet) throws SQLException;
}
