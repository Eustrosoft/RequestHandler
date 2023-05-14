package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eustrosoft.core.Constants.REQUEST_CHUNKS_BINARY_FILE_UPLOAD;

public final class BytesChunkFileRequestBlock extends ChunkFileRequestBlock {

    public BytesChunkFileRequestBlock(HttpServletRequest request,
                                      HttpServletResponse response,
                                      QJson qJson) {
        super(request, response, qJson);
    }

    @Override
    public String getR() {
        return REQUEST_CHUNKS_BINARY_FILE_UPLOAD;
    }
}
