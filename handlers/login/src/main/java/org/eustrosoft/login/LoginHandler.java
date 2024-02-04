/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.login;

import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.core.annotation.Handler;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.ResponseBlock;

import static org.eustrosoft.spec.Constants.REQUEST_LOGIN;
import static org.eustrosoft.spec.Constants.REQUEST_LOGOUT;
import static org.eustrosoft.spec.Constants.SUBSYSTEM_LOGIN;

@Handler(SUBSYSTEM_LOGIN)
public final class LoginHandler implements BasicHandler {

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        LoginService service = new LoginService(requestBlock);
        switch (requestBlock.getR()) {
            case REQUEST_LOGIN:
                return service.login();
            break;
            case REQUEST_LOGOUT:
                return service.logout();
            break;
            default:
                return null;
            break;
        }
    }
}
