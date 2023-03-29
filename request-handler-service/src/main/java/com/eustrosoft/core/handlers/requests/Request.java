package com.eustrosoft.core.handlers.requests;

import java.util.List;

public interface Request {
    List<RequestBlock> getR();

    Long getT();
}
