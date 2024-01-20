/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handler.requests;

import org.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.eustrosoft.core.constants.Constants.REQUEST;

public abstract class BasicRequest implements RequestBlock {
    protected final HttpServletResponse response;
    protected final HttpServletRequest request;
    protected final String requestType;

    public BasicRequest(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.requestType = "";
    }

    public BasicRequest(HttpServletRequest request, HttpServletResponse response, QJson qJson) {
        this.request = request;
        this.response = response;
        this.requestType = qJson.getItemString(REQUEST);
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
