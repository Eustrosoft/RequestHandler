package com.eustrosoft.core.handlers;

import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;

public interface Handler {
    ResponseBlock processRequest(RequestBlock requestBlock) throws Exception;
}
