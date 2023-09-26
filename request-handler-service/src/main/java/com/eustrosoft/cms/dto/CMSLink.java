/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.cms.dto;

import com.eustrosoft.cms.CMSType;
import com.eustrosoft.core.model.DBObject;

import java.util.List;

public class CMSLink extends DBObject implements CMSObject {
    public String getExtension() {
        return null;
    }

    public String getDescription() {
        return null;
    }

    public String getFileName() {
        return null;
    }

    public String getFullPath() {
        return null;
    }

    public Long getSpace() {
        return 0L;
    }

    public Long getTotalSpace() {
        return 0L;
    }

    public CMSType getType() {
        return CMSType.LINK;
    }

    public List<String> getLinks() {
        return null;
    }
}
