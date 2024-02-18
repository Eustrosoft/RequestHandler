package org.eustrosoft.handlers.login.dto;

import org.eustrosoft.spec.interfaces.JsonConvertible;
import org.eustrosoft.spec.response.BasicResponseBlock;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_LOGIN;

public class LoginResponseBock<T extends JsonConvertible> extends BasicResponseBlock<T> {

    public LoginResponseBock(String request) {
        this.s = SUBSYSTEM_LOGIN;
        this.r = request;
    }
}
