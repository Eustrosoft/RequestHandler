package com.eustrosoft.core.handlers;

import java.util.List;

public interface Request {
    int getqTisVer();

    List<RequestObject> getRequests();

    boolean getqTisEnd();
}
