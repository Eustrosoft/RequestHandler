package com.eustrosoft.dbdatasource.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
@AllArgsConstructor
public abstract class DBObject implements ResultSetConverter<DBObject>, JsonFormat {
    private Long zoid;
    private Long zver;
    private Long zrid;

    public DBObject(ResultSet resultSet) throws SQLException {
        fillFromResultSet(resultSet);
    }
}
