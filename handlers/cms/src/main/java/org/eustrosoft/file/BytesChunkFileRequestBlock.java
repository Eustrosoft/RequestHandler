/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.file;

import org.eustrosoft.json.QJson;
import org.eustrosoft.spec.Constants;

public final class BytesChunkFileRequestBlock extends ChunkFileRequestBlock {

    public BytesChunkFileRequestBlock(String request,
                                      QJson qJson) {
        super(request, qJson);
    }

    @Override
    public String getR() {
        return Constants.REQUEST_CHUNKS_BINARY_FILE_UPLOAD;
    }
}
