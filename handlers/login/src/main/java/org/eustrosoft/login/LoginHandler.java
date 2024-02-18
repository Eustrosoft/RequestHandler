/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.login;

import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.core.annotation.Handler;
import org.eustrosoft.handlers.login.dto.LoginDTO;
import org.eustrosoft.spec.request.BasicRequestBlock;
import org.eustrosoft.spec.response.BasicResponseBlock;

import static org.eustrosoft.spec.Constants.REQUEST_LOGIN;
import static org.eustrosoft.spec.Constants.REQUEST_LOGOUT;
import static org.eustrosoft.spec.Constants.SUBSYSTEM_LOGIN;

@Handler(SUBSYSTEM_LOGIN)
public final class LoginHandler implements BasicHandler {

    @Override
    public BasicResponseBlock<?> processRequest(BasicRequestBlock<?> requestBlock) throws Exception {
        LoginService service = new LoginService();
        switch (requestBlock.getR()) {
            case REQUEST_LOGIN:
                ((BasicRequestBlock) requestBlock).setData(new LoginDTO());
                return service.login((BasicRequestBlock) requestBlock);
            case REQUEST_LOGOUT:
                return service.logout(requestBlock);
            default:
                return null;
        }
    }
}
