/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.dic;

import com.eustrosoft.core.db.dao.DicDAO;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;

import static com.eustrosoft.core.constants.Constants.ERR_OK;
import static com.eustrosoft.core.constants.Constants.ERR_UNEXPECTED;
import static com.eustrosoft.core.constants.Constants.MSG_OK;
import static com.eustrosoft.core.constants.Constants.MSG_REQUEST_TYPE_NOT_SUPPORTED;
import static com.eustrosoft.core.constants.Constants.REQUEST_DICS;
import static com.eustrosoft.core.constants.Constants.REQUEST_VALUES;

public final class DICHandler implements Handler {
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
            case REQUEST_VALUES:
                String name = dicRequestBlock.getDic();
                if (name == null || name.isEmpty()) {
                    throw new Exception("Dic in values request was incorrect");
                }
                respBlock.setValues(dao.getDictionaryValues(name));
                break;
            case REQUEST_DICS:
                respBlock.setDics(dao.getDicNames());
                break;
            default:
                respBlock.setE(ERR_UNEXPECTED);
                respBlock.setM(MSG_REQUEST_TYPE_NOT_SUPPORTED);
                break;
        }

        respBlock.setE(ERR_OK);
        respBlock.setM(MSG_OK);
        respBlock.setR(requestType);
        return respBlock;
    }
}
