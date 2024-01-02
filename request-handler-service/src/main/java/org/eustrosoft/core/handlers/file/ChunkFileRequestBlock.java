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

public class ChunkFileRequestBlock extends FileRequestBlock {
    private Long chunkNumber;
    private Long chunksCount;

    public ChunkFileRequestBlock(HttpServletRequest request,
                                 HttpServletResponse response,
                                 QJson qJson) {
        super(request, response);
        parseQJson(qJson);
    }

    @Override
    protected void parseQJson(QJson qJson) throws NullPointerException {
        super.parseQJson(qJson);
        QJson fileData = getParameters();
        setChunkNumber(fileData.getItemLong("chunk"));
        setChunksCount(fileData.getItemLong("all_chunks"));
    }

    public void setChunksCount(Long chunkCount) {
        this.chunksCount = chunkCount;
    }

    public Long getChunkNumber() {
        return this.chunkNumber;
    }

    public void setChunkNumber(Long chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public Long getChunkCount() {
        return this.chunksCount;
    }

    @Override
    public String getR() {
        return Constants.REQUEST_CHUNKS_FILE_UPLOAD; // TODO make non static value
    }
}
