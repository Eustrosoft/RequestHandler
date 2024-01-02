package org.eustrosoft.core.model.interfaces;

import org.eustrosoft.core.model.DBObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetConverter<T> {

    <T extends DBObject> void fillFromResultSet(ResultSet resultSet) throws SQLException;
}
