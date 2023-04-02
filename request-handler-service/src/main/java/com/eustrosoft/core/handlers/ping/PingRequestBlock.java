package com.eustrosoft.core.handlers.ping;

import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;

import static com.eustrosoft.core.Constants.*;

public class PingRequestBlock implements RequestBlock {
    private final HttpServletRequest request;

    public PingRequestBlock(HttpServletRequest request, QJson qJson) {
        this.request = request;
        parseQJson(qJson);
    }

    public PingRequestBlock(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String getS() {
        return SUBSYSTEM_PING;
    }

    @Override
    public String getR() {
        return "";
    }

    @Override
    public HttpServletRequest getHttpRequest() {
        return this.request;
    }

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
    }
}
