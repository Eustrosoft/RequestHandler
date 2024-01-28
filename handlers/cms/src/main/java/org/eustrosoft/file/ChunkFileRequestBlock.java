/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.file;

import org.eustrosoft.json.QJson;
import org.eustrosoft.spec.Constants;

public class ChunkFileRequestBlock extends FileRequestBlock {
    private Long chunkNumber;
    private Long chunksCount;

    public ChunkFileRequestBlock(String request, QJson qJson) {
        super(request, qJson);
    }

    public ChunkFileRequestBlock(String request, QJson qJson, byte[] fileBytes) {
        super(request, qJson, fileBytes);
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
