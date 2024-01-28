package org.eustrosoft.handlers.sam.resource;

import org.eustrosoft.spec.experimental.Stub;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.Response;

public interface SamResource {
    Response getUserId(RequestBlock<Stub> request);
}
