/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.handlers.file;

import org.eustrosoft.core.constants.Constants;
import org.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class BytesChunkFileRequestBlock extends ChunkFileRequestBlock {

    public BytesChunkFileRequestBlock(HttpServletRequest request,
                                      HttpServletResponse response,
                                      QJson qJson) {
        super(request, response, qJson);
    }

    @Override
    public String getR() {
        return Constants.REQUEST_CHUNKS_BINARY_FILE_UPLOAD;
    }
}
