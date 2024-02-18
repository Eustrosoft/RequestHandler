package org.eustrosoft.handlers.ping.dto;

import org.eustrosoft.spec.interfaces.JsonConvertible;
import org.eustrosoft.spec.response.BasicResponseBlock;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_PING;

public class PingResponseBlock<T extends JsonConvertible> extends BasicResponseBlock<T> {
    public PingResponseBlock(String request) {
        this.s = SUBSYSTEM_PING;
        this.r = request;
    }
}
