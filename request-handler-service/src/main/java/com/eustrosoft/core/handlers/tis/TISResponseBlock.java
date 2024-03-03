/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.tis;

import com.eustrosoft.core.handlers.responses.BasicResponse;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_TIS;

public class TISResponseBlock extends BasicResponse {

    public TISResponseBlock(String responseType) {
        super(SUBSYSTEM_TIS, responseType);
    }
}
