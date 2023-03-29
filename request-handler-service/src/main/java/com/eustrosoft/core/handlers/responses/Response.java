package com.eustrosoft.core.handlers.responses;

import java.util.List;

public interface Response {
    //long getQTisVer();

    List<ResponseBlock> getR();

    Long getT();

    String getJson();
}
