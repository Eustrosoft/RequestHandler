package com.eustrosoft.core.handlers.responses;

import com.eustrosoft.core.tools.QJson;

import java.util.List;

public interface Response {
    long getQTisVer();

    List<ResponseBlock> getResponses();

    boolean getQTisEnd();

    QJson getJson();
}
