package com.eustrosoft.datasource.sources.model;

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
    private String role;
    private String lastRead;
}
