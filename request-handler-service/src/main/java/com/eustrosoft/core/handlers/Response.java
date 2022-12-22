package com.eustrosoft.core.handlers;

import com.eustrosoft.core.handlers.responses.ResponseBlock;

import java.util.List;

public interface Response {
    int getQTisVer();

    List<ResponseBlock> getResponse();

    boolean getQTisEnd();
}
