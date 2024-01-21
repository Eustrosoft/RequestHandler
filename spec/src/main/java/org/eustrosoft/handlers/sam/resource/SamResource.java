package org.eustrosoft.handlers.sam.resource;

import org.eustrosoft.handlers.cms.dto.RequestBlock;
import org.eustrosoft.handlers.cms.dto.Response;
import org.eustrosoft.handlers.cms.dto.Stub;

public interface SamResource {
    Response getUserId(RequestBlock<Stub> request);
}
