package com.eustrosoft.core.model;

import com.eustrosoft.core.model.ranges.MSGPartyRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MSGParty extends DBObject {
    private String userId;
    private MSGPartyRole role;
    private String lastRead;
}
