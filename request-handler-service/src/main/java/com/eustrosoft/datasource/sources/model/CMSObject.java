/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.datasource.sources.model;

import java.util.Date;
import java.util.List;

/**
 * Interface that represents
 * methods that need to be implemented by
 * system objects, like file, directory and other.
 */
public interface CMSObject {
    String getId();

    String getFileName();

    String getFullPath();

    Date getCreated();

    Date getModified();

    Long getSpace();

    CMSType getType();

    List<String> getLinks();

    String getDescription();
}
