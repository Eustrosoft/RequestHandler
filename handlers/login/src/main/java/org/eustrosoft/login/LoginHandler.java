/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.login;

import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.core.annotation.Handler;
import org.eustrosoft.handlers.login.dto.LoginDTO;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.ResponseBlock;
import org.eustrosoft.spec.request.BasicRequestBlock;

import static org.eustrosoft.spec.Constants.REQUEST_LOGIN;
import static org.eustrosoft.spec.Constants.REQUEST_LOGOUT;
import static org.eustrosoft.spec.Constants.SUBSYSTEM_LOGIN;

@Handler(SUBSYSTEM_LOGIN)
public final class LoginHandler implements BasicHandler {

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        LoginService service = new LoginService();
        BasicRequestBlock rb = (BasicRequestBlock) requestBlock;
        switch (requestBlock.getR()) {
            case REQUEST_LOGIN:
                rb.setData(new LoginDTO());
                return service.login(rb);
            case REQUEST_LOGOUT:
                return service.logout(rb);
            default:
                return null;
        }
    }
}
