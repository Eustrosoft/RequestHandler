package com.eustrosoft.core.handlers.cms;

import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;

import static com.eustrosoft.core.Constants.REQUEST_SQL;
import static com.eustrosoft.core.Constants.SUBSYSTEM_SQL;

public final class CMSReuestBlock implements RequestBlock {
    private String query;
    private String method;
    private final HttpServletRequest request;

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public CMSReuestBlock(HttpServletRequest request, QJson qJson) {
        this.request = request;
        parseQJson(qJson);
    }

    public CMSReuestBlock(HttpServletRequest request, String query) {
        this.request = request;
        this.query = query;
    }

    @Override
    public String getS() {
        return SUBSYSTEM_SQL;
    }

    @Override
    public String getR() {
        return REQUEST_SQL;
    }

    public CMSReuestBlock(HttpServletRequest request) {
        this(request, "");
    }

    @Override
    public HttpServletRequest getHttpRequest() {
        return this.request;
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
        setMethod(qJson.getItemString("method"));
    }
}
