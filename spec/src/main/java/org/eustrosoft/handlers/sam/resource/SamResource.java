package org.eustrosoft.handlers.sam.resource;

import org.eustrosoft.handlers.sam.dto.UserIdDTO;
import org.eustrosoft.spec.request.BasicRequestBlock;
import org.eustrosoft.spec.response.BasicResponseBlock;

public interface SamResource {
    BasicResponseBlock<UserIdDTO> getUserId(BasicRequestBlock<?> request) throws Exception;
}
