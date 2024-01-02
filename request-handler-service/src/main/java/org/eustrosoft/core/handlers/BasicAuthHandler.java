/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.handlers;

import org.eustrosoft.core.handlers.requests.RequestBlock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public abstract class BasicAuthHandler implements Handler {
    private String message;
    private Integer errCode;

    public BasicAuthHandler(RequestBlock requestBlock) throws Exception {
        HttpServletRequest httpRequest = requestBlock.getHttpRequest();
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            this.message = "No session";
            this.errCode = 401;
        }
    }
}
