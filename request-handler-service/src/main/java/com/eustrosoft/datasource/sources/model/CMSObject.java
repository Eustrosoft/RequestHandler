package com.eustrosoft.datasource.sources.model;

import java.util.Date;
import java.util.List;

/**
 * Interface that represents
 * methods that need to be implemented by
 * system objects, like file, directory and other.
 */
public interface CMSObject {

    String getFileName();

    String getFullPath();

    Date getCreated();

    Date getModified();

    long getSpace();

    CMSType getType();

    List<String> getLinks();
}
