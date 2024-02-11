/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.sam;

import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.core.annotation.Handler;
import org.eustrosoft.spec.Constants;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.ResponseBlock;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_SAM;

@Handler(SUBSYSTEM_SAM)
public final class SAMHandler implements BasicHandler {

    public SAMHandler() {
    }

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        SAMService samService = new SAMService(requestBlock);

        switch (requestBlock.getS()) {
            case Constants.REQUEST_USER_ID:
                return samService.getUserId();
//            case Constants.REQUEST_USER_LOGIN:
//                respBlock.setData(dao.getUserLogin());
//                break;
//            case Constants.REQUEST_USER_SLVL:
//                respBlock.setData(dao.getUserSLvl().toString());
//                break;
//            case Constants.REQUEST_USER_AVAILABLE_SLVL:
//                respBlock.setData(Arrays.toString(dao.getUserAvailableSlvl()));
//                break;
//            case Constants.REQUEST_USER_LANG:
//                respBlock.setData(dao.getUserLang());
//                break;
            case Constants.REQUEST_ZSID:
                return samService.getAvailableZsid();
            default:
//                respBlock.setE(Constants.ERR_UNEXPECTED);
//                respBlock.setErrMsg(Constants.MSG_REQUEST_TYPE_NOT_SUPPORTED);
                return null;
        }
    }
}
