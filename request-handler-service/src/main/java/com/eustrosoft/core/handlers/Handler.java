package com.eustrosoft.core.handlers;

import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;

import javax.servlet.http.HttpServletRequest;

public interface Handler {
    ResponseBlock processRequest(RequestBlock requestBlock, HttpServletRequest request) throws Exception;
}
