package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;

public final class SQLHandler implements Handler {
    private DBWrapper dbWrapper;

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        this.dbWrapper = DBWrapper.getInstance("", "", ""); // credentials // TODO
        SQLRequestBlock sqlRequest = (SQLRequestBlock) requestBlock;
        String query = sqlRequest.getQuery();
        String method = sqlRequest.getMethod();

        String answer = this.dbWrapper.executeStringQuery(query);
        return new SQLResponseBlock(answer);
    }
}
