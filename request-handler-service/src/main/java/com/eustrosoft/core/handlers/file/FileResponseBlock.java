/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.handlers.responses.BasicResponse;

import static com.eustrosoft.core.constants.Constants.REQUEST_FILE_UPLOAD;
import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_FILE;

public class FileResponseBlock extends BasicResponse {

    public FileResponseBlock(String answer) {
        super(SUBSYSTEM_FILE, answer, (short) 0,REQUEST_FILE_UPLOAD);
    }
}
