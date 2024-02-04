package org.eustrosoft.handlers.login.dto;

import org.eustrosoft.spec.response.BasicResponseBlock;
import org.eustrosoft.spec.response.StringResponseData;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_LOGIN;

public class LoginResponseBlock extends BasicResponseBlock<StringResponseData> {

    public LoginResponseBlock(String request) {
        this.r = request;
    }

    @Override
    public String getS() {
        return SUBSYSTEM_LOGIN;
    }
}
