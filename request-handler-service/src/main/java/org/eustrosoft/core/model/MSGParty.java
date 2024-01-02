/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.model;

import lombok.*;
import org.eustrosoft.core.constants.DBConstants;
import org.eustrosoft.core.model.ranges.MSGPartyRole;

import java.sql.ResultSet;

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
        setUserId(resultSet.getLong(DBConstants.UID));
        setRole(MSGPartyRole.of(resultSet.getString(DBConstants.ROLE)));
        setLastRead(resultSet.getString(DBConstants.LAST_READ));
    }
}
