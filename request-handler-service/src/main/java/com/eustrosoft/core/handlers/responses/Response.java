package com.eustrosoft.core.handlers.responses;

import java.util.List;

public interface Response {
    long getQTisVer();

    List<ResponseBlock> getResponse();

    boolean getQTisEnd();
}
