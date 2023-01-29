package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.tools.QJson;

import static com.eustrosoft.core.Constants.REQUEST_CHUNKS_FILE_UPLOAD;

public final class ChunkFileRequestBlock extends FileRequestBlock {
    private Long chunkNumber;
    private Long chunksCount;

    public ChunkFileRequestBlock(QJson qJson) {
        parseQJson(qJson);
    }

    @Override
    protected void parseQJson(QJson qJson) throws NullPointerException {
        super.parseQJson(qJson);

        QJson fileData = qJson.getItemQJson("data");
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
    public String getRequest() {
        return REQUEST_CHUNKS_FILE_UPLOAD; // TODO make non static value
    }
}
