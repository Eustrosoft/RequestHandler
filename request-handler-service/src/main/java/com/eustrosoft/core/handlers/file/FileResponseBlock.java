/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.handlers.responses.BasicResponse;

import static com.eustrosoft.core.Constants.REQUEST_FILE_UPLOAD;
import static com.eustrosoft.core.Constants.SUBSYSTEM_FILE;
import static com.eustrosoft.core.handlers.responses.ResponseLang.en_US;

public class FileResponseBlock extends BasicResponse {
    private String errMsg;

    public FileResponseBlock(String answer) {
        this.errMsg = answer;
    }

    @Override
    public String getS() {
        return SUBSYSTEM_FILE;
    }

    @Override
    public String getR() {
        return REQUEST_FILE_UPLOAD;
    }

    @Override
    public Short getE() {
        return 0;
    }

    @Override
    public String getM() {
        return this.errMsg;
    }

    @Override
    public String getL() {
        return en_US;
    }


    public void setM(String errMsg) {
        this.errMsg = errMsg;
    }
}
