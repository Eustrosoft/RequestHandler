/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core;

import java.util.List;

public interface Response {

    List<ResponseBlock> getR();

    Long getT();

    String getJson();
}
