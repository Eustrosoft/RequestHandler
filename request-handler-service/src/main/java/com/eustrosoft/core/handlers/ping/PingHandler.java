package com.eustrosoft.core.handlers.ping;

import com.eustrosoft.core.context.User;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;

import javax.servlet.http.HttpServletRequest;

import static com.eustrosoft.core.Constants.*;

public class PingHandler implements Handler {
    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        PingResponseBlock pingResponseBlock = new PingResponseBlock();
        HttpServletRequest httpRequest = requestBlock.getHttpRequest();
        if (httpRequest.getSession(false) != null) {
            UsersContext usersContext = UsersContext.getInstance();
            User user = usersContext.getSQLUser(httpRequest.getSession(false).getId());
            pingResponseBlock.setErrCode(ERR_OK);
            pingResponseBlock.setMessage(MSG_OK);
            if (httpRequest.getUserPrincipal() != null) {
                pingResponseBlock.setFullName(httpRequest.getUserPrincipal().getName());
            }
            pingResponseBlock.setUsername(user.getUserName());
            pingResponseBlock.setUserId(httpRequest.getRequestedSessionId());
        } else {
            pingResponseBlock.setErrCode(ERR_UNAUTHORIZED);
            pingResponseBlock.setMessage(MSG_UNAUTHORIZED);
        }
        return pingResponseBlock;
    }
}
