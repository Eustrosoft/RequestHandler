/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.dic;

import org.eustrosoft.core.annotations.Handler;
import org.eustrosoft.core.interfaces.BasicHandler;
import org.eustrosoft.core.request.BasicRequestBlock;
import org.eustrosoft.core.request.RequestBlock;
import org.eustrosoft.core.response.ResponseBlock;
import org.eustrosoft.handlers.dic.dto.DicRequestDto;

import static org.eustrosoft.constants.Constants.REQUEST_DICS;
import static org.eustrosoft.constants.Constants.REQUEST_VALUES;
import static org.eustrosoft.constants.Constants.SUBSYSTEM_DIC;

@Handler(SUBSYSTEM_DIC)
public final class DicHandler implements BasicHandler {

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        DicService service = new DicService();
        BasicRequestBlock rb = (BasicRequestBlock) requestBlock;
        switch (requestBlock.getR()) {
            case REQUEST_DICS:
                return service.getDicNames(rb);
            case REQUEST_VALUES:
                rb.setData(new DicRequestDto());
                return service.getDicValues(rb);
            default:
                return null;
        }
    }
}
