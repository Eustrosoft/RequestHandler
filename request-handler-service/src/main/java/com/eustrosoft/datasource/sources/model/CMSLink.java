package com.eustrosoft.datasource.sources.model;

import java.util.Date;
import java.util.List;

public class CMSLink implements CMSObject {

    public String getExtension() {
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

    public long getSpace() {
        return 0;
    }

    public long getTotalSpace() {
        return 0;
    }

    public CMSType getType() {
        return CMSType.LINK;
    }

    public List<String> getLinks() {
        return null;
    }
}
