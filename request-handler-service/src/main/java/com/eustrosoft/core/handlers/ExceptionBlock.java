/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers;

import com.eustrosoft.core.handlers.responses.BasicResponse;

public class ExceptionBlock extends BasicResponse {

    public ExceptionBlock(String message, Short errorCode,
                          String subsystem, String response) {
        super(subsystem, message, errorCode, response);
    }
}
