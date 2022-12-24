package com.eustrosoft.core.handlers.requests;

import java.util.List;

public interface Request {
    long getqTisVer();

    List<RequestBlock> getRequests();

    boolean getqTisEnd();
}
