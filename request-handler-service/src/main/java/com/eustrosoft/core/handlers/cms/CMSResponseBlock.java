/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.cms;

import com.eustrosoft.cms.dto.CMSObject;
import com.eustrosoft.core.handlers.responses.BasicResponse;

import java.util.List;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_CMS;

public final class CMSResponseBlock extends BasicResponse {

    private List<CMSObject> content;

    public CMSResponseBlock() {
        super(SUBSYSTEM_CMS);
    }

    public List<CMSObject> getContent() {
        return content;
    }

    public void setContent(List<CMSObject> content) {
        this.content = content;
    }
}
