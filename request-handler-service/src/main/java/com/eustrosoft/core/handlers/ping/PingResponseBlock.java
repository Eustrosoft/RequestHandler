/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.ping;

import com.eustrosoft.core.handlers.responses.BasicResponse;
import lombok.Getter;
import lombok.Setter;

import static com.eustrosoft.core.constants.Constants.REQUEST_PING;
import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_PING;

@Getter
@Setter
public class PingResponseBlock extends BasicResponse {
    private String userId;
    private String username;
    private String fullName;
    private String dbUser;


    public PingResponseBlock() {
        super(SUBSYSTEM_PING, REQUEST_PING);
    }
}
