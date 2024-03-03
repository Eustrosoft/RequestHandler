/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.login;

import com.eustrosoft.core.handlers.responses.BasicResponse;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_LOGIN;

public final class LoginResponseBlock extends BasicResponse {

    public LoginResponseBlock(String r) {
        super(SUBSYSTEM_LOGIN, r);
    }
}
