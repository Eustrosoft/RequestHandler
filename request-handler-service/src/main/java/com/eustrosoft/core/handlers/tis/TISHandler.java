/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.tis;

import com.eustrosoft.core.db.dao.BasicDAO;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eustrosoft.core.constants.Constants.*;

public final class TISHandler implements Handler {
    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        HttpServletRequest request = requestBlock.getHttpRequest();
        HttpServletResponse httpResponse = requestBlock.getHttpResponse();
        SessionProvider sessionProvider = new SessionProvider(request, httpResponse);
        QDBPSession session = sessionProvider.getSession();

        TISRequestBlock req = (TISRequestBlock) requestBlock;
        TISResponseBlock resp = new TISResponseBlock(req.getR());
        try {
            BasicDAO dao = new BasicDAO(session.getConnection());
            switch (req.getR()) {
                case REQUEST_CHANGE_ZLVL:
                    String zType = dao.getZType(req.getZoid(), req.getZver());
                    if (zType == null) {
                        throw new Exception("Can not get object ztype.");
                    }
                    dao.setZLvl(zType, req.getZoid(), req.getZver(), req.getZlvl());
                    break;
                default:
                    throw new Exception("This functionality is not implemented.");
            }
            resp.setE(ERR_OK);
            resp.setErrMsg(MSG_OK);
        } catch (Exception ex) {
            resp.setErrMsg(ex.getMessage());
            resp.setE(ERR_UNEXPECTED);
        }
        return resp;
    }
}
