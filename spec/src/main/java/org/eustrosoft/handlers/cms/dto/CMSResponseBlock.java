/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.cms.dto;

import org.eustrosoft.core.json.interfaces.JsonData;
import org.eustrosoft.core.response.basic.BasicResponseBlock;

import java.util.List;

public final class CMSResponseBlock<T extends JsonData> extends BasicResponseBlock<T> {
    private List<CMSObject> content;

    public CMSResponseBlock() {
        super();
    }

    public List<CMSObject> getContent() {
        return content;
    }

    public void setContent(List<CMSObject> content) {
        this.content = content;
    }

    @Override
    public String toJsonString() {
        return null;
    }
}
