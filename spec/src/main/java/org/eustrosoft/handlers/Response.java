/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers;

import java.util.List;

public interface Response {
    @Deprecated
    default long getQTisVer() {
        return 1L;
    }

    ;

    List<ResponseBlock> getR();

    Long getT();
}