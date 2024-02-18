/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.dic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.core.annotation.Handler;
import org.eustrosoft.dic.dao.DicDAO;
import org.eustrosoft.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.spec.Constants;
import org.eustrosoft.spec.request.BasicRequestBlock;
import org.eustrosoft.spec.response.BasicResponseBlock;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_DIC;

@Handler(SUBSYSTEM_DIC)
public final class DICHandler implements BasicHandler {
    private QDBPConnection poolConnection;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Override
    public BasicResponseBlock processRequest(BasicRequestBlock requestBlock) throws Exception {
        QDBPSession session = new SessionProvider(request, response).getSession();
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
