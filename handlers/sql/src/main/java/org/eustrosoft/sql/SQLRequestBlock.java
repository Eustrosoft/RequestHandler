/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.sql;

import org.eustrosoft.core.constants.Constants;
import org.eustrosoft.core.handlers.requests.BasicRequest;
import org.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class SQLRequestBlock extends BasicRequest {
    private String query;

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
        return Constants.SUBSYSTEM_SQL;
    }

    @Override
    public String getR() {
        return Constants.REQUEST_SQL;
    }

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        setQuery(qJson.getItemString("query"));
    }
}
