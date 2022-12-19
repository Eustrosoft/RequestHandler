package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.handlers.requests.RequestBlock;

public final class SQLRequestBlock implements RequestBlock {
    private String query;

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String getSubsystem() {
        return "sql";
    }

    @Override
    public String getRequest() {
        return "sql";
    }
}
