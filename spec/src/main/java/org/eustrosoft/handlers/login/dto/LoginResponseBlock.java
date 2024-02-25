package org.eustrosoft.handlers.login.dto;

import org.eustrosoft.core.response.basic.BasicResponseBlock;
import org.eustrosoft.core.response.basic.StubDto;

import static org.eustrosoft.constants.Constants.SUBSYSTEM_LOGIN;

public class LoginResponseBlock extends BasicResponseBlock<StubDto> {

    public LoginResponseBlock(String request) {
        this.s = SUBSYSTEM_LOGIN;
        this.r = request;
    }
}
