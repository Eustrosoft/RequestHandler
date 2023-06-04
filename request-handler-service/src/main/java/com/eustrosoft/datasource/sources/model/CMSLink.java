package com.eustrosoft.datasource.sources.model;

import java.util.Date;
import java.util.List;

public class CMSLink implements CMSObject {
    public String getId() {
        return null;
    }

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

    public Date getCreated() {
        return null;
    }

    public Date getModified() {
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
