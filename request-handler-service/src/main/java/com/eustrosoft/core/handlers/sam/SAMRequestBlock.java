/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.sam;

import com.eustrosoft.core.handlers.requests.BasicRequest;
import com.eustrosoft.core.tools.QJson;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_SAM;

@Getter
@Setter
public class SAMRequestBlock extends BasicRequest {
    private String id;
    private String requestType;

    public SAMRequestBlock(HttpServletRequest request,
                           HttpServletResponse response,
                           QJson qJson) {
        super(request, response);
        parseQJson(qJson);
    }

    @Override
    public String getS() {
        return SUBSYSTEM_SAM;
    }

    @Override
    public String getR() {
        return this.requestType;
    }

    @Override
    public HttpServletRequest getHttpRequest() {
        return this.request;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
    }
}
