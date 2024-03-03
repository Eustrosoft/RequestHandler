/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.sam;

import com.eustrosoft.core.handlers.responses.BasicResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_SAM;

@Getter
@Setter
public final class SAMResponseBlock extends BasicResponse {
    private String data;
    private List<Long> zsid;

    public SAMResponseBlock() {
        super(SUBSYSTEM_SAM);
    }
}
