package com.eustrosoft.core.handlers;

import com.eustrosoft.core.handlers.requests.RequestBlock;

import java.util.List;

public class QTisRequestObject extends RequestObject {
    @Override
    public List<RequestBlock> getRequests() {
        return super.getRequestBlocks();
    }
}
