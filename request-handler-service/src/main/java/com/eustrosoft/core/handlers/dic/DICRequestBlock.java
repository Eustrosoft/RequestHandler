/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.dic;

import com.eustrosoft.core.handlers.requests.BasicRequest;
import com.eustrosoft.core.tools.QJson;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_DIC;

@Getter
@Setter
public class DICRequestBlock extends BasicRequest {
    private String id;
    private String dic;

    public DICRequestBlock(HttpServletRequest request,
                           HttpServletResponse response,
                           QJson qJson) {
        super(request, response, qJson);
        parseQJson(qJson);
    }

    @Override
    public String getS() {
        return SUBSYSTEM_DIC;
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
        setDic(qJson.getItemString("dic"));
    }
}
