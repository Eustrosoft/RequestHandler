/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.model;

import com.eustrosoft.core.model.ranges.MSGPartyRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.sql.ResultSet;

import static com.eustrosoft.core.constants.DBConstants.LAST_READ;
import static com.eustrosoft.core.constants.DBConstants.ROLE;
import static com.eustrosoft.core.constants.DBConstants.UID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MSGParty extends DBObject {
    private Long userId;
    private MSGPartyRole role;
    private String lastRead;

    @Override
    @SneakyThrows
    public void fillFromResultSet(ResultSet resultSet) {
        super.fillFromResultSet(resultSet);
        setUserId(resultSet.getLong(UID));
        setRole(MSGPartyRole.of(resultSet.getString(ROLE)));
        setLastRead(resultSet.getString(LAST_READ));
    }
}
