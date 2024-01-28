/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.ping;

import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.core.RequestContextHolder;
import org.eustrosoft.core.annotation.Handler;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.ResponseBlock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.eustrosoft.spec.Constants.REQUEST_PING;
import static org.eustrosoft.spec.Constants.SUBSYSTEM_PING;

@Handler(SUBSYSTEM_PING)
public class PingHandler implements BasicHandler {
    private final HttpServletResponse response;
    private final HttpServletRequest request;


    public PingHandler() {
        RequestContextHolder.ServletAttributes attributes = RequestContextHolder.getAttributes();
        this.request = attributes.getRequest();
        this.response = attributes.getResponse();
    }

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        PingService pingService = new PingService(request, response, requestBlock);

        switch (requestBlock.getR()) {
            case REQUEST_PING:
                return pingService.getPing();
            default:
                return null;
        }
    }
}
