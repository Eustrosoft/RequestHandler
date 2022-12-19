package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;

public final class SQLHandler implements Handler {
    @Override
    public void processRequest(RequestBlock requestBlock) {
        SQLRequestBlock sqlRequest = (SQLRequestBlock) requestBlock;
    }
}
