package org.eustrosoft.sam;

import org.eustrosoft.core.BasicService;
import org.eustrosoft.handlers.sam.dto.RequestScopesDTO;
import org.eustrosoft.handlers.sam.dto.SAMResponseBlock;
import org.eustrosoft.handlers.sam.dto.ScopesDTO;
import org.eustrosoft.handlers.sam.dto.UserIdDTO;
import org.eustrosoft.handlers.sam.resource.SamResource;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.sam.dao.SamDAO;
import org.eustrosoft.spec.request.BasicRequestBlock;
import org.eustrosoft.spec.response.BasicResponseBlock;

import java.sql.SQLException;
import java.util.List;

import static org.eustrosoft.spec.Constants.ERR_OK;
import static org.eustrosoft.spec.Constants.MSG_OK;

public class SAMService extends BasicService implements SamResource {
    private SamDAO samDAO;

    public SAMService() throws SQLException {
        QDBPSession session = new SessionProvider(getRequest(), getResponse())
                .getSession();
        QDBPConnection pool = session.getConnection();
        this.samDAO = new SamDAO(pool);
    }

    @Override
    public BasicResponseBlock<UserIdDTO> getUserId(BasicRequestBlock<?> requestBlock) throws SQLException {
        Long userId = samDAO.getUserId();
        BasicResponseBlock<UserIdDTO> respBlock = new SAMResponseBlock<>(requestBlock.getR());
        respBlock.setE(ERR_OK);
        respBlock.setM(MSG_OK);
        respBlock.setData(new UserIdDTO(userId.toString()));
        return respBlock;
    }

    public BasicResponseBlock<ScopesDTO> getAvailableZsid(BasicRequestBlock<?> requestBlock) throws SQLException, JsonException {
        RequestScopesDTO data = (RequestScopesDTO) requestBlock.getData();
        List<Number> zsids = samDAO.getZsids(data.getType());
        BasicResponseBlock<ScopesDTO> respBlock = new SAMResponseBlock<>(requestBlock.getR());
        respBlock.setE(ERR_OK);
        respBlock.setM(MSG_OK);
        respBlock.setData(new ScopesDTO(zsids));
        return respBlock;
    }
}
