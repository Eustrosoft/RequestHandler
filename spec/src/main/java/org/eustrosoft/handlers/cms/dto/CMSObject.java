/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.cms.dto;

import org.eustrosoft.core.db.model.IDBObject;
import org.eustrosoft.core.json.interfaces.JsonConvertible;

import java.util.List;

/**
 * Interface that represents
 * methods that need to be implemented by
 * system objects, like file, directory and other.
 */
public interface CMSObject extends IDBObject, JsonConvertible {
    String getFileName();

    String getFullPath();

    Long getSpace();

    CMSType getType();

    List<String> getLinks();

    String getDescription();
}
