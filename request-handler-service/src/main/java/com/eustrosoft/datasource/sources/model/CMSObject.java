/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.datasource.sources.model;

import com.eustrosoft.datasource.sources.ranges.CMSType;

import java.util.List;

/**
 * Interface that represents
 * methods that need to be implemented by
 * system objects, like file, directory and other.
 */
public interface CMSObject extends IDBObject {
    String getFileName();

    String getFullPath();

    Long getSpace();

    CMSType getType();

    List<String> getLinks();

    String getDescription();
}
