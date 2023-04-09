package com.eustrosoft.core.handlers.ping;

import com.eustrosoft.core.handlers.requests.BasicRequest;
import com.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eustrosoft.core.Constants.*;

public class PingRequestBlock extends BasicRequest {
    public PingRequestBlock(HttpServletRequest request,
                            HttpServletResponse response,
                            QJson qJson) {
        super(request, response);
        parseQJson(qJson);
    }

    public PingRequestBlock(HttpServletRequest request,
                            HttpServletResponse response) {
        super(request, response);
    }

    @Override
    public String getS() {
        return SUBSYSTEM_PING;
    }

    @Override
    public String getR() {
        return SUBSYSTEM_PING;
    }

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
    }
}
