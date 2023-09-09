package com.eustrosoft.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DBObject implements ResultSetConverter<DBObject>, JsonFormat {
    private Long zoid;
    private Long zver;
    private Long zrid;

    public DBObject(ResultSet resultSet) throws SQLException {
        fillFromResultSet(resultSet);
    }

    @Override
    public <T extends DBObject> void fillFromResultSet(ResultSet resultSet) throws SQLException {

    }
}
