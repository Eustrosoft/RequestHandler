/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.responses;

import java.util.List;

public interface Response {
    List<ResponseBlock> getR();

    Long getT();

    String getJson();
}
