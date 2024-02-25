/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.login;

import org.eustrosoft.core.annotations.Handler;
import org.eustrosoft.core.interfaces.BasicHandler;
import org.eustrosoft.core.request.BasicRequestBlock;
import org.eustrosoft.core.request.RequestBlock;
import org.eustrosoft.core.response.ResponseBlock;
import org.eustrosoft.handlers.login.dto.LoginDto;

import static org.eustrosoft.constants.Constants.REQUEST_LOGIN;
import static org.eustrosoft.constants.Constants.REQUEST_LOGOUT;
import static org.eustrosoft.constants.Constants.SUBSYSTEM_LOGIN;

@Handler(SUBSYSTEM_LOGIN)
public final class LoginHandler implements BasicHandler {

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        LoginService service = new LoginService();
        BasicRequestBlock rb = (BasicRequestBlock) requestBlock;
        switch (requestBlock.getR()) {
            case REQUEST_LOGIN:
                rb.setData(new LoginDto());
                return service.login(rb);
            case REQUEST_LOGOUT:
                return service.logout(rb);
            default:
                return null;
        }
    }
}
