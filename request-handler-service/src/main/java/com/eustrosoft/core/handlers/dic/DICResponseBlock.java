/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.dic;

import com.eustrosoft.core.handlers.responses.BasicResponse;
import com.eustrosoft.core.model.DIC;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_DIC;

@Getter
@Setter
public final class DICResponseBlock extends BasicResponse {
    private List<DIC> values;
    private List<String> dics;

    public DICResponseBlock() {
        super(SUBSYSTEM_DIC);
    }
}
