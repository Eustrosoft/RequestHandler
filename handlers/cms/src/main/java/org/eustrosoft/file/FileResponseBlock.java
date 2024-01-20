/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.file;

import org.eustrosoft.core.constants.Constants;
import org.eustrosoft.core.handlers.responses.BasicResponse;
import org.eustrosoft.core.handlers.responses.ResponseLang;

public class FileResponseBlock extends BasicResponse {
    private String errMsg;

    public FileResponseBlock(String answer) {
        this.errMsg = answer;
    }

    @Override
    public String getS() {
        return Constants.SUBSYSTEM_FILE;
    }

    @Override
    public String getR() {
        return Constants.REQUEST_FILE_UPLOAD;
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
        return ResponseLang.en_US;
    }


    public void setM(String errMsg) {
        this.errMsg = errMsg;
    }
}
