package org.eustrosoft.sam;

import org.eustrosoft.core.Service;
import org.eustrosoft.handlers.sam.dto.RequestScopesDTO;
import org.eustrosoft.handlers.sam.dto.SAMResponseBlock;
import org.eustrosoft.handlers.sam.dto.ScopesDTO;
import org.eustrosoft.handlers.sam.dto.UserIdDTO;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.sam.dao.SamDAO;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.ResponseBlock;

import java.sql.SQLException;
import java.util.List;

import static org.eustrosoft.spec.Constants.REQUEST_USER_ID;

public class SAMService implements Service {
    private final RequestBlock requestBlock;
    private SamDAO samDAO;

    public SAMService(RequestBlock requestBlock) throws SQLException {
        this.requestBlock = requestBlock;

        QDBPSession session = new SessionProvider(getRequest(), getResponse())
                .getSession();
        QDBPConnection pool = session.getConnection();
        this.samDAO = new SamDAO(pool);
    }

    public ResponseBlock<UserIdDTO> getUserId() throws SQLException {
        Long userId = samDAO.getUserId();
        SAMResponseBlock<UserIdDTO> respBlock = new SAMResponseBlock<>(REQUEST_USER_ID);
        respBlock.setData(new UserIdDTO(userId.toString()));
        return respBlock;
    }

    public ResponseBlock<ScopesDTO> getAvailableZsid() throws SQLException, JsonException {
        RequestScopesDTO data = (RequestScopesDTO) requestBlock.getData();
        List<Number> zsids = samDAO.getZsids(data.getType());
        SAMResponseBlock<ScopesDTO> respBlock = new SAMResponseBlock<>(REQUEST_USER_ID);
        respBlock.setData(new ScopesDTO(zsids));
        return respBlock;
    }
}
