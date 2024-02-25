/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.tis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eustrosoft.constants.Constants;
import org.eustrosoft.core.annotations.Handler;
import org.eustrosoft.core.db.dao.BasicDAO;
import org.eustrosoft.core.interfaces.BasicHandler;
import org.eustrosoft.core.request.BasicRequestBlock;
import org.eustrosoft.core.request.RequestBlock;
import org.eustrosoft.core.response.ResponseBlock;
import org.eustrosoft.handlers.tis.dto.TISDto;
import org.eustrosoft.handlers.tis.dto.TISResponseBlock;
import org.eustrosoft.providers.RequestContextHolder;
import org.eustrosoft.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPSession;

import static org.eustrosoft.constants.Constants.SUBSYSTEM_TIS;

@Handler(SUBSYSTEM_TIS)
public final class TISHandler implements BasicHandler {
    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        RequestContextHolder.ServletAttributes attributes = RequestContextHolder.getAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse httpResponse = attributes.getResponse();
        SessionProvider sessionProvider = new SessionProvider(request, httpResponse);
        QDBPSession session = sessionProvider.getSession();

        BasicRequestBlock<TISDto> req = (BasicRequestBlock<TISDto>) requestBlock;
        TISResponseBlock resp = new TISResponseBlock(req.getR());
        try {
            BasicDAO dao = new BasicDAO(session.getConnection());
            switch (req.getR()) {
                case Constants.REQUEST_CHANGE_ZLVL:
                    TISDto data = req.getData();
                    String zType = dao.getZType(data.getZOID(), data.getZVER());
                    if (zType == null) {
                        throw new Exception("Can not get object ztype.");
                    }
                    dao.setZLvl(zType, data.getZOID(), data.getZVER(), data.getZLVL());
                    break;
                default:
                    throw new Exception("This functionality is not implemented.");
            }
            resp.setE(Constants.ERR_OK);
            resp.setM(Constants.MSG_OK);
        } catch (Exception ex) {
            resp.setM(ex.getMessage());
            resp.setE(Constants.ERR_UNEXPECTED);
        }
        return resp;
    }
}
