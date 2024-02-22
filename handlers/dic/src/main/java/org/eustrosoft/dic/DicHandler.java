/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.dic;

import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.core.annotation.Handler;
import org.eustrosoft.handlers.dic.dto.DicRequestDto;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.ResponseBlock;
import org.eustrosoft.spec.request.BasicRequestBlock;

import static org.eustrosoft.spec.Constants.REQUEST_DICS;
import static org.eustrosoft.spec.Constants.REQUEST_VALUES;
import static org.eustrosoft.spec.Constants.SUBSYSTEM_DIC;

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
