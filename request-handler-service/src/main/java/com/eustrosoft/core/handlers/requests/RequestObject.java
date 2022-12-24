package com.eustrosoft.core.handlers.requests;

import java.util.List;

public abstract class RequestObject implements Request {
    private long qTisVer = 0;
    private boolean qTisEnd = false;
    private List<RequestBlock> requestBlocks;

    public boolean getqTisEnd() {
        return this.qTisEnd;
    }

    public void setqTisEnd(boolean qTisEnd) {
        this.qTisEnd = qTisEnd;
    }

    public long getqTisVer() {
        return qTisVer;
    }

    public void setqTisVer(long qTisVer) {
        this.qTisVer = qTisVer;
    }

    public List<RequestBlock> getRequestBlocks() {
        return this.requestBlocks;
    }

    public void setRequestBlocks(List<RequestBlock> requestBlocks) {
        this.requestBlocks = requestBlocks;
    }
}
