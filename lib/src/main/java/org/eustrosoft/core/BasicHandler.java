package org.eustrosoft.core;

import org.eustrosoft.spec.request.BasicRequestBlock;
import org.eustrosoft.spec.response.BasicResponseBlock;

public interface BasicHandler {
    BasicResponseBlock<?> processRequest(BasicRequestBlock<?> requestBlock) throws Exception;
}
