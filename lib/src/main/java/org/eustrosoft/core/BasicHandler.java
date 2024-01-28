package org.eustrosoft.core;

import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.ResponseBlock;

public interface BasicHandler {
    ResponseBlock processRequest(RequestBlock requestBlock) throws Exception;
}
