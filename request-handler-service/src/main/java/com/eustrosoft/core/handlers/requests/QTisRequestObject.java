package com.eustrosoft.core.handlers.requests;

import java.util.List;

public class QTisRequestObject extends RequestObject {

    @Override
    public List<RequestBlock> getR() {
        return super.getRequestBlocks();
    }

    @Override
    public Long getT() {
        return super.getTimeout();
    }
}
