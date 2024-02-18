package org.eustrosoft.ping;

import org.eustrosoft.core.BasicService;
import org.eustrosoft.handlers.ping.dto.PingDTO;
import org.eustrosoft.handlers.ping.dto.PingResponseBlock;
import org.eustrosoft.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;
import org.eustrosoft.sam.dao.SamDAO;
import org.eustrosoft.sam.model.User;
import org.eustrosoft.spec.request.BasicRequestBlock;
import org.eustrosoft.spec.response.BasicResponseBlock;

import java.sql.SQLException;

import static org.eustrosoft.spec.Constants.ERR_OK;
import static org.eustrosoft.spec.Constants.ERR_UNAUTHORIZED;
import static org.eustrosoft.spec.Constants.MSG_OK;
import static org.eustrosoft.spec.Constants.MSG_UNAUTHORIZED;

public class PingService extends BasicService {
    private final BasicRequestBlock requestBlock;

    public PingService(BasicRequestBlock requestBlock) {
        this.requestBlock = requestBlock;
    }

    public BasicResponseBlock<PingDTO> getPing() throws SQLException {
        long l = System.currentTimeMillis();
        QTISSessionCookie qtisSessionCookie = new QTISSessionCookie(getRequest(), getResponse());
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(getRequest()),
                DBPoolContext.getUrl(getRequest()),
                DBPoolContext.getDriverClass(getRequest())
        );
        QDBPSession dbps = dbPool.logon(qtisSessionCookie.getCookieValue());
        BasicResponseBlock<PingDTO> pingResponseBlock = new PingResponseBlock<>(requestBlock.getR());
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
                    new PingDTO(user.getUsername(), user.getFullName(), user.getDbUser(), user.getId().toString())
            );
        } else {
            pingResponseBlock.setE(ERR_UNAUTHORIZED);
            pingResponseBlock.setM(MSG_UNAUTHORIZED);
        }
        System.out.println(System.currentTimeMillis() - l);
        return pingResponseBlock;
    }
}
