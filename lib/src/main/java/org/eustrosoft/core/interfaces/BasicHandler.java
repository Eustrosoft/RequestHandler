package org.eustrosoft.core.interfaces;

import org.eustrosoft.core.request.RequestBlock;
import org.eustrosoft.core.response.ResponseBlock;

public interface BasicHandler {
    ResponseBlock processRequest(RequestBlock requestBlock) throws Exception;
}
