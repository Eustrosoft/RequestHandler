package com.eustrosoft.core.handlers;

import com.eustrosoft.core.handlers.requests.RequestBlock;

import java.util.List;

public abstract class RequestObject implements Request {
    private int qTisVer = 0;
    private boolean qTisEnd = false;
    private List<RequestBlock> requestBlocks;

    public boolean getqTisEnd() {
        return this.qTisEnd;
    }

    public void setqTisEnd(boolean qTisEnd) {
        this.qTisEnd = qTisEnd;
    }

    public int getqTisVer() {
        return qTisVer;
    }

    public void setqTisVer(int qTisVer) {
        this.qTisVer = qTisVer;
    }

    public void setRequestBlocks(List<RequestBlock> requestBlocks) {
        this.requestBlocks = requestBlocks;
    }
}
