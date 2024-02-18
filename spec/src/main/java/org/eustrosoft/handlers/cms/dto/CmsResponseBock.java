package org.eustrosoft.handlers.cms.dto;

import org.eustrosoft.spec.interfaces.JsonConvertible;
import org.eustrosoft.spec.response.BasicResponseBlock;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_CMS;

public class CmsResponseBock<T extends JsonConvertible> extends BasicResponseBlock<T> {

    public CmsResponseBock(String request) {
        this.s = SUBSYSTEM_CMS;
        this.r = request;
    }
}
