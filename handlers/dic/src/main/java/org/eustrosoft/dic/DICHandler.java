/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.dic;

import org.eustrosoft.core.constants.Constants;
import org.eustrosoft.core.db.dao.DicDAO;
import org.eustrosoft.core.handlers.BasicHandler;
import org.eustrosoft.core.handlers.requests.RequestBlock;
import org.eustrosoft.core.handlers.responses.ResponseBlock;
import org.eustrosoft.core.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;

public final class DICHandler implements BasicHandler {
    private QDBPConnection poolConnection;

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        QDBPSession session = new SessionProvider(requestBlock.getHttpRequest(), requestBlock.getHttpResponse())
                .getSession();
        this.poolConnection = session.getConnection();
        DICRequestBlock dicRequestBlock = (DICRequestBlock) requestBlock;
        DICResponseBlock respBlock = new DICResponseBlock();
        DicDAO dao = new DicDAO(poolConnection);

        String requestType = dicRequestBlock.getR();
        switch (requestType) {
            case Constants.REQUEST_VALUES:
                String name = dicRequestBlock.getDic();
                if (name == null || name.isEmpty()) {
                    throw new Exception("Dic in values request was incorrect");
                }
                respBlock.setValues(dao.getDictionaryValues(name));
                break;
            case Constants.REQUEST_DICS:
                respBlock.setDics(dao.getDicNames());
                break;
            default:
                respBlock.setE(Constants.ERR_UNEXPECTED);
                respBlock.setErrMsg(Constants.MSG_REQUEST_TYPE_NOT_SUPPORTED);
                break;
        }

        respBlock.setE(Constants.ERR_OK);
        respBlock.setErrMsg(Constants.MSG_OK);
        respBlock.setResponseType(requestType);
        return respBlock;
    }
}
