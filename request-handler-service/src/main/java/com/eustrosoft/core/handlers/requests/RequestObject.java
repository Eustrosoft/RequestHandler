/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.requests;

import java.util.List;

public abstract class RequestObject implements Request {
    private long qTisVer = 0;
    private long timeout = 0;
    private boolean qTisEnd = false;
    private List<RequestBlock> requestBlocks;

    @Deprecated
    public boolean getqTisEnd() {
        return this.qTisEnd;
    }

    @Deprecated
    public void setqTisEnd(boolean qTisEnd) {
        this.qTisEnd = qTisEnd;
    }

    @Deprecated
    public long getqTisVer() {
        return qTisVer;
    }

    @Deprecated
    public void setqTisVer(long qTisVer) {
        this.qTisVer = qTisVer;
    }

    public List<RequestBlock> getRequestBlocks() {
        return this.requestBlocks;
    }

    public void setRequestBlocks(List<RequestBlock> requestBlocks) {
        this.requestBlocks = requestBlocks;
    }

    public Long getTimeout() {
        return this.timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
}
