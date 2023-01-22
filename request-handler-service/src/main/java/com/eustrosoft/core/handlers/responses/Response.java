package com.eustrosoft.core.handlers.responses;

import java.util.List;

public interface Response {
    long getQTisVer();

    List<ResponseBlock> getResponses();

    boolean getQTisEnd();

    String getJson();
}
