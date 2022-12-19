package com.eustrosoft.core.handlers;

import java.util.List;

public interface Response {
    int getQTisVer();

    List<Object> getResponses();

    boolean getQTisEnd();
}
