/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.sql;

import com.eustrosoft.core.handlers.requests.BasicRequest;
import com.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eustrosoft.core.constants.Constants.REQUEST_SQL;
import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_SQL;

public final class SQLRequestBlock extends BasicRequest {
    private String query;
    private String method;

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public SQLRequestBlock(HttpServletRequest request,
                           HttpServletResponse response,
                           QJson qJson) {
        super(request, response);
        parseQJson(qJson);
    }

    public SQLRequestBlock(HttpServletRequest request,
                           HttpServletResponse response,
                           String query) {
        super(request, response);
        this.query = query;
    }

    public SQLRequestBlock(HttpServletRequest request, HttpServletResponse response) {
        this(request, response, "");
    }

    @Override
    public String getS() {
        return SUBSYSTEM_SQL;
    }

    @Override
    public String getR() {
        return REQUEST_SQL;
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
    }
}
