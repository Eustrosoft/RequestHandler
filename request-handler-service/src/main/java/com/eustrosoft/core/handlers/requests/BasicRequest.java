package com.eustrosoft.core.handlers.requests;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BasicRequest implements RequestBlock {
    protected final HttpServletResponse response;
    protected final HttpServletRequest request;

    public BasicRequest(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public HttpServletRequest getHttpRequest() {
        return this.request;
    }

    @Override
    public HttpServletResponse getHttpResponse() {
        return this.response;
    }
}
