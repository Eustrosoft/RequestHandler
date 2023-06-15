/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

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
