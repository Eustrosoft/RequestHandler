package org.eustrosoft.handlers.msg.dto;

import org.eustrosoft.spec.interfaces.JsonConvertible;
import org.eustrosoft.spec.response.BasicResponseBlock;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_MSG;

public class MsgResponseBock<T extends JsonConvertible> extends BasicResponseBlock<T> {

    public MsgResponseBock(String request) {
        this.s = SUBSYSTEM_MSG;
        this.r = request;
    }
}
