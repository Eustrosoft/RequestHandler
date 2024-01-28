package org.eustrosoft.ping;

import org.eustrosoft.handlers.ping.dto.PingData;
import org.eustrosoft.handlers.ping.dto.PingResponseBlock;
import org.eustrosoft.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;
import org.eustrosoft.sam.dao.SamDAO;
import org.eustrosoft.sam.model.User;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.ResponseBlock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

import static org.eustrosoft.spec.Constants.*;

public class PingService {
    private final RequestBlock requestBlock;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public PingService(
            HttpServletRequest request,
            HttpServletResponse response,
            RequestBlock requestBlock) {
        this.requestBlock = requestBlock;
        this.request = request;
        this.response = response;
    }

    public ResponseBlock<PingData> getPing() throws SQLException {
        PingResponseBlock<PingData> pingResponseBlock = new PingResponseBlock<>(requestBlock.getR());
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
                    new PingData(user.getUsername(), user.getFullName(), user.getDbUser(), user.getId().toString())
            );
        } else {
            pingResponseBlock.setE(ERR_UNAUTHORIZED);
            pingResponseBlock.setM(MSG_UNAUTHORIZED);
        }
        return pingResponseBlock;
    }
}
