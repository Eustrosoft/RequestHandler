/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.sam;

import org.eustrosoft.constants.Constants;
import org.eustrosoft.core.annotations.Handler;
import org.eustrosoft.core.interfaces.BasicHandler;
import org.eustrosoft.core.request.BasicRequestBlock;
import org.eustrosoft.core.request.RequestBlock;
import org.eustrosoft.core.response.ResponseBlock;
import org.eustrosoft.core.response.basic.BasicResponseBlock;
import org.eustrosoft.core.response.basic.StubDto;
import org.eustrosoft.handlers.sam.dto.RequestScopesDTO;
import org.eustrosoft.handlers.sam.dto.SAMResponseBlock;

import static org.eustrosoft.constants.Constants.SUBSYSTEM_SAM;

@Handler(SUBSYSTEM_SAM)
public final class SAMHandler implements BasicHandler {

    public SAMHandler() {
    }

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        SAMService samService = new SAMService(requestBlock);
        BasicRequestBlock rb = (BasicRequestBlock) requestBlock;
        switch (requestBlock.getR()) {
            case Constants.REQUEST_USER_ID:
                return samService.getUserId();
            case Constants.REQUEST_USER_LOGIN:
                return samService.getUserLogin();
            case Constants.REQUEST_USER_SLVL:
                return samService.getUserSlvl();
            case Constants.REQUEST_USER_AVAILABLE_SLVL:
                rb.setData(new RequestScopesDTO());
                return samService.getAvailableZsid(rb);
            case Constants.REQUEST_USER_LANG:
                return samService.getUserLang();
            case Constants.REQUEST_ZSID:
                return samService.getUserZsid();
            default:
                BasicResponseBlock<StubDto> resp = new SAMResponseBlock<>(requestBlock.getR());
                resp.setE(Constants.ERR_UNEXPECTED);
                resp.setM(Constants.MSG_REQUEST_TYPE_NOT_SUPPORTED);
                return null;
        }
    }
}
