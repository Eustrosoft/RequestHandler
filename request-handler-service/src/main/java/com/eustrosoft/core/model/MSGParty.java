package com.eustrosoft.core.model;

import com.eustrosoft.core.model.ranges.MSGPartyRole;
import lombok.*;

import java.sql.ResultSet;

import static com.eustrosoft.core.constants.DBConstants.*;

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
