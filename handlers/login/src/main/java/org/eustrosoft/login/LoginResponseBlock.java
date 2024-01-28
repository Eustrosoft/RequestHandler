/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.login;

import org.eustrosoft.spec.ResponseLang;
import org.eustrosoft.spec.interfaces.JsonData;
import org.eustrosoft.spec.response.BasicResponseBlock;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_LOGIN;

public final class LoginResponseBlock<T extends JsonData> extends BasicResponseBlock<T> {

    public LoginResponseBlock() {
    }

    @Override
    public String getS() {
        return SUBSYSTEM_LOGIN;
    }

    @Override
    public String getL() {
        return ResponseLang.EN_US.getLang();
    }

}
