/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.sam;

import com.eustrosoft.core.dto.ScopeCreationDTO;
import com.eustrosoft.core.handlers.responses.BasicResponse;

import java.util.List;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_SAM;

public final class SAMResponseBlock extends BasicResponse {
    private String data;
    private List<ScopeCreationDTO> scopes;

    public SAMResponseBlock() {
        super(SUBSYSTEM_SAM);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<ScopeCreationDTO> getScopes() {
        return scopes;
    }

    public void setScopes(List<ScopeCreationDTO> scopes) {
        this.scopes = scopes;
    }
}
