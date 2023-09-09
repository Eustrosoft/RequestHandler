package com.eustrosoft.core.model;

import com.eustrosoft.core.model.interfaces.IDBObject;
import com.eustrosoft.core.model.interfaces.JsonFormat;
import com.eustrosoft.core.model.interfaces.ResultSetConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.eustrosoft.core.constants.DBConstants.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DBObject implements IDBObject, ResultSetConverter<DBObject>, JsonFormat {
    private Long zoid;
    private Long zver;
    private Long zrid;

    public DBObject(ResultSet resultSet) throws SQLException {
        fillFromResultSet(resultSet);
    }

    @Override
    public <T extends DBObject> void fillFromResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet == null) {
            throw new SQLException("Result set is null while processing DBObject from ResultSet.");
        }
        if (resultSet.next()) {
            setZoid(resultSet.getLong(ZOID));
            setZver(resultSet.getLong(ZVER));
            setZrid(resultSet.getLong(ZRID));
        }
    }
}
