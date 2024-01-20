/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.msg;

import lombok.Getter;
import lombok.Setter;
import org.eustrosoft.core.constants.Constants;
import org.eustrosoft.core.handlers.requests.BasicRequest;
import org.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Getter
@Setter
public class MSGRequestBlock extends BasicRequest {
    private String id;
    private MsgParams params;

    public MSGRequestBlock(HttpServletRequest request,
                           HttpServletResponse response,
                           QJson qJson) {
        super(request, response, qJson);
        parseQJson(qJson);
    }

    @Override
    public String getS() {
        return Constants.SUBSYSTEM_MSG;
    }

    @Override
    public String getR() {
        return this.requestType;
    }

    @Override
    public HttpServletRequest getHttpRequest() {
        return this.request;
    }

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        try {
            setId(qJson.getItemString("id"));
        } catch (Exception ex) {
        }
        try {
            setParams(MsgParams.fromJson(qJson.getItemQJson("params")));
        } catch (Exception ex) {
        }
    }
}
