package org.eustrosoft.ping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eustrosoft.core.db.model.User;
import org.eustrosoft.core.request.RequestBlock;
import org.eustrosoft.handlers.ping.dto.PingDto;
import org.eustrosoft.handlers.ping.dto.PingResponseBlock;
import org.eustrosoft.providers.RequestContextHolder;
import org.eustrosoft.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;
import org.eustrosoft.sam.dao.SamDAO;

import java.sql.SQLException;

import static org.eustrosoft.constants.Constants.ERR_OK;
import static org.eustrosoft.constants.Constants.ERR_UNAUTHORIZED;
import static org.eustrosoft.constants.Constants.MSG_OK;
import static org.eustrosoft.constants.Constants.MSG_UNAUTHORIZED;

public class PingService {
    private final RequestBlock requestBlock;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public PingService(RequestBlock requestBlock) {
        this.requestBlock = requestBlock;
        RequestContextHolder.ServletAttributes attributes = RequestContextHolder.getAttributes();
        this.request = attributes.getRequest();
        this.response = attributes.getResponse();
    }

    public PingResponseBlock getPing() throws SQLException {
        QTISSessionCookie qtisSessionCookie = new QTISSessionCookie(
                request,
                response
        );
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(request),
                DBPoolContext.getUrl(request),
                DBPoolContext.getDriverClass(request)
        );
        QDBPSession dbps = dbPool.logon(qtisSessionCookie.getCookieValue());
        PingResponseBlock pingResponseBlock = new PingResponseBlock();
        if (dbps == null) {
            pingResponseBlock.setE(ERR_UNAUTHORIZED);
            pingResponseBlock.setM(MSG_UNAUTHORIZED);
            return pingResponseBlock;
        }
        if (dbps.checkSecretCookie(dbps.getSecretCookie())) {
            pingResponseBlock.setE(ERR_OK);
            pingResponseBlock.setM(MSG_OK);
            SamDAO samDAO = new SamDAO(dbps.getConnection());
            User user = samDAO.getUserById(samDAO.getUserId());
            pingResponseBlock.setData(
                    new PingDto(user.getUsername(), user.getFullName(), user.getDbUser(), user.getId().toString())
            );
        } else {
            pingResponseBlock.setE(ERR_UNAUTHORIZED);
            pingResponseBlock.setM(MSG_UNAUTHORIZED);
        }
        return pingResponseBlock;
    }
}
