package com.eustrosoft.core.handlers;

import com.eustrosoft.core.handlers.requests.RequestBlock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public abstract class BasicAuthHandler implements Handler {
    private String message;
    private Integer errCode;

    public BasicAuthHandler(RequestBlock requestBlock) throws Exception {
        HttpServletRequest httpRequest = requestBlock.getHttpRequest();
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            this.message = "No session";
            this.errCode = 401;
        }
    }
}
