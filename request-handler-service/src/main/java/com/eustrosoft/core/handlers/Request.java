package com.eustrosoft.core.handlers;

import com.eustrosoft.core.handlers.requests.RequestBlock;

import java.util.List;

public interface Request {
    long getqTisVer();

    List<RequestBlock> getRequests();

    boolean getqTisEnd();
}
