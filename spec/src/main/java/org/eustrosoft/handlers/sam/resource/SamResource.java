package org.eustrosoft.handlers.sam.resource;

import org.eustrosoft.spec.RequestBlock;
import org.eustrosoft.spec.Response;
import org.eustrosoft.spec.Stub;

public interface SamResource {
    Response getUserId(RequestBlock<Stub> request);
}
