/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.cms.dto;

import org.eustrosoft.handlers.JsonParser;

import java.util.List;

public interface Request {
    List<RequestBlock> getR();

    Long getT();

    default String toJsonString() throws Exception {
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parseObject(this);
    }
}
