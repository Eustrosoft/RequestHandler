package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.tools.QJson;

import static com.eustrosoft.core.Constants.REQUEST_SQL;
import static com.eustrosoft.core.Constants.SUBSYSTEM_SQL;

public final class SQLRequestBlock implements RequestBlock {
    private String query;
    private String method;

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public SQLRequestBlock(QJson qJson) {
        parseQJson(qJson);
    }

    public SQLRequestBlock(String query) {
        this.query = query;
    }

    @Override
    public String getSubsystem() {
        return SUBSYSTEM_SQL;
    }

    @Override
    public String getRequest() {
        return REQUEST_SQL;
    }

    public SQLRequestBlock() {
        this("");
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        setQuery(qJson.getItemString("query"));
        setMethod(qJson.getItemString("method"));
    }
}
