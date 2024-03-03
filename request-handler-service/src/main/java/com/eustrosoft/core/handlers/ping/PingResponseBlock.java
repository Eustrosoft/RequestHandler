/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.ping;

import com.eustrosoft.core.handlers.responses.BasicResponse;

import static com.eustrosoft.core.constants.Constants.REQUEST_PING;
import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_PING;

public class PingResponseBlock extends BasicResponse {
    private String userId;
    private String username;
    private String fullName;
    private String dbUser;


    public PingResponseBlock() {
        super(SUBSYSTEM_PING, REQUEST_PING);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }
}
