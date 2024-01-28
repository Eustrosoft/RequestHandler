package org.eustrosoft.sam;

import org.eustrosoft.handlers.sam.dto.RequestScopesDTO;
import org.eustrosoft.handlers.sam.dto.SAMResponseBlock;
import org.eustrosoft.handlers.sam.dto.ScopesDTO;
import org.eustrosoft.handlers.sam.dto.UserIdDTO;
import org.eustrosoft.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.sam.dao.SamDAO;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.ResponseBlock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

import static org.eustrosoft.spec.Constants.REQUEST_USER_ID;

public class SAMService {
    private final RequestBlock requestBlock;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private SamDAO samDAO;

    public SAMService(
            HttpServletRequest request,
            HttpServletResponse response,
            RequestBlock requestBlock) throws SQLException {
        this.requestBlock = requestBlock;
        this.request = request;
        this.response = response;

        QDBPSession session = new SessionProvider(request, response)
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

    public ResponseBlock<ScopesDTO> getAvailableZsid() throws SQLException {
        RequestScopesDTO data = (RequestScopesDTO) requestBlock.getData();
        List<Number> zsids = samDAO.getZsids(data.getType());
        SAMResponseBlock<ScopesDTO> respBlock = new SAMResponseBlock<>(REQUEST_USER_ID);
        respBlock.setData(new ScopesDTO(zsids));
        return respBlock;
    }
}
