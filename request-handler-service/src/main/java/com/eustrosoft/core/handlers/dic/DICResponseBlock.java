/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.dic;

import com.eustrosoft.core.handlers.responses.BasicResponse;
import com.eustrosoft.core.model.DIC;

import java.util.List;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_DIC;

public final class DICResponseBlock extends BasicResponse {
    private List<DIC> values;
    private List<String> dics;

    public DICResponseBlock() {
        super(SUBSYSTEM_DIC);
    }

    public List<DIC> getValues() {
        return values;
    }

    public void setValues(List<DIC> values) {
        this.values = values;
    }

    public List<String> getDics() {
        return dics;
    }

    public void setDics(List<String> dics) {
        this.dics = dics;
    }
}
