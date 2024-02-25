package org.eustrosoft.handlers.sql.dto;

import org.eustrosoft.core.json.QJson;
import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonParsable;

public class SqlDto implements JsonParsable<SqlDto> {
    public static final String PARAM_SQL = "sql";

    private String sql;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public SqlDto convertToObject(QJson qJson) throws JsonException {
        setSql(qJson.getItemString(PARAM_SQL));
        return this;
    }
}
