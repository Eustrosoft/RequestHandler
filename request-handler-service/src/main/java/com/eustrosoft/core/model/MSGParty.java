/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.model;

import com.eustrosoft.core.model.ranges.MSGPartyRole;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.eustrosoft.core.constants.DBConstants.LAST_READ;
import static com.eustrosoft.core.constants.DBConstants.ROLE;
import static com.eustrosoft.core.constants.DBConstants.UID;

public class MSGParty extends DBObject {
    private Long userId;
    private MSGPartyRole role;
    private String lastRead;

    public MSGParty(Long userId, MSGPartyRole role, String lastRead) {
        this.userId = userId;
        this.role = role;
        this.lastRead = lastRead;
    }

    @Override
    public void fillFromResultSet(ResultSet resultSet) throws SQLException {
        super.fillFromResultSet(resultSet);
        setUserId(resultSet.getLong(UID));
        setRole(MSGPartyRole.of(resultSet.getString(ROLE)));
        setLastRead(resultSet.getString(LAST_READ));
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public MSGPartyRole getRole() {
        return role;
    }

    public void setRole(MSGPartyRole role) {
        this.role = role;
    }

    public String getLastRead() {
        return lastRead;
    }

    public void setLastRead(String lastRead) {
        this.lastRead = lastRead;
    }


}
