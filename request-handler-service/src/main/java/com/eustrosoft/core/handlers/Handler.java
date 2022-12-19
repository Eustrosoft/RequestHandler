package com.eustrosoft.core.handlers;

import com.eustrosoft.core.handlers.requests.RequestBlock;

public interface Handler {
    void processRequest(RequestBlock requestBlock);
}
