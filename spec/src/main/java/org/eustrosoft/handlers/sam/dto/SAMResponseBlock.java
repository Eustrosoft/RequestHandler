package org.eustrosoft.handlers.sam.dto;

import org.eustrosoft.spec.interfaces.JsonConvertible;
import org.eustrosoft.spec.response.BasicResponseBlock;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_SAM;

public class SAMResponseBlock<T extends JsonConvertible> extends BasicResponseBlock<T> {
    public SAMResponseBlock(String request) {
        super();
        this.s = SUBSYSTEM_SAM;
        this.r = request;
    }
}
