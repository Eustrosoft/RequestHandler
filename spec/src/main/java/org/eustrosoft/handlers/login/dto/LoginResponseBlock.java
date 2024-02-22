package org.eustrosoft.handlers.login.dto;

import org.eustrosoft.spec.response.BasicResponseBlock;
import org.eustrosoft.spec.response.StubDto;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_LOGIN;

public class LoginResponseBlock extends BasicResponseBlock<StubDto> {

    public LoginResponseBlock(String request) {
        this.s = SUBSYSTEM_LOGIN;
        this.r = request;
    }
}
