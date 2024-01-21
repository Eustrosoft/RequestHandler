package org.eustrosoft.handlers.cms.dto;

import java.util.ArrayList;
import java.util.List;

public class SingleTISRequest<T> implements Request {
    private Long time = 0L;

    private RequestBlock<T> requestBlock;

    public SingleTISRequest(RequestBlock<T> requestBlock) {
        this.requestBlock = requestBlock;
    }

    @Override
    public List<RequestBlock> getR() {
        List<RequestBlock> requestBlocks = new ArrayList<>();
        requestBlocks.add(requestBlock);
        return requestBlocks;
    }

    @Override
    public Long getT() {
        return this.time;
    }
}