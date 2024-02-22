package org.eustrosoft.sam;

import org.eustrosoft.core.Service;
import org.eustrosoft.handlers.sam.dto.RequestScopesDTO;
import org.eustrosoft.handlers.sam.dto.SAMResponseBlock;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.sam.dao.SamDAO;
import org.eustrosoft.spec.ResponseLang;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.ResponseBlock;
import org.eustrosoft.spec.request.BasicRequestBlock;
import org.eustrosoft.spec.response.ListNumberResponseData;
import org.eustrosoft.spec.response.SingleNumberDto;
import org.eustrosoft.spec.response.SingleStringDto;

import java.sql.SQLException;
import java.util.List;

import static org.eustrosoft.spec.Constants.*;

public class SAMService implements Service {
    private final RequestBlock requestBlock;
    private SamDAO samDAO;

    public SAMService(RequestBlock requestBlock) throws SQLException {
        this.requestBlock = requestBlock;
        this.samDAO = new SamDAO(getSession().getConnection());
    }

    public ResponseBlock<SingleStringDto> getUserId() throws SQLException {
        Long userId = samDAO.getUserId();
        SAMResponseBlock<SingleStringDto> respBlock = new SAMResponseBlock<>(REQUEST_USER_ID);
        respBlock.setData(new SingleStringDto(PARAM_ID, userId.toString()));
        respBlock.setE(ERR_OK);
        respBlock.setM(MSG_OK);
        respBlock.setL(ResponseLang.EN_US.getLang());
        return respBlock;
    }

    public ResponseBlock<SingleStringDto> getUserLogin() throws SQLException {
        String userLogin = samDAO.getUserLogin();
        SAMResponseBlock<SingleStringDto> respBlock = new SAMResponseBlock<>(REQUEST_USER_LOGIN);
        respBlock.setData(new SingleStringDto(PARAM_LOGIN, userLogin));
        respBlock.setE(ERR_OK);
        respBlock.setM(MSG_OK);
        respBlock.setL(ResponseLang.EN_US.getLang());
        return respBlock;
    }

    public ResponseBlock<SingleStringDto> getUserLang() throws SQLException {
        String userLang = samDAO.getUserLang();
        SAMResponseBlock<SingleStringDto> respBlock = new SAMResponseBlock<>(REQUEST_USER_LANG);
        respBlock.setData(new SingleStringDto(PARAM_LANG, userLang));
        respBlock.setE(ERR_OK);
        respBlock.setM(MSG_OK);
        respBlock.setL(ResponseLang.EN_US.getLang());
        return respBlock;
    }

    public ResponseBlock<SingleNumberDto> getUserSlvl() throws SQLException {
        Integer userSlvl = samDAO.getUserSLvl();
        SAMResponseBlock<SingleNumberDto> respBlock = new SAMResponseBlock<>(REQUEST_USER_SLVL);
        respBlock.setData(new SingleNumberDto(PARAM_SLVL, userSlvl));
        respBlock.setE(ERR_OK);
        respBlock.setM(MSG_OK);
        respBlock.setL(ResponseLang.EN_US.getLang());
        return respBlock;
    }

    public ResponseBlock<SingleNumberDto> getUserZsid() throws SQLException {
        Long userZsid = samDAO.getUserSid();
        SAMResponseBlock<SingleNumberDto> respBlock = new SAMResponseBlock<>(REQUEST_USER_ZSID);
        respBlock.setData(new SingleNumberDto(PARAM_ZSID, userZsid));
        respBlock.setE(ERR_OK);
        respBlock.setM(MSG_OK);
        respBlock.setL(ResponseLang.EN_US.getLang());
        return respBlock;
    }

    public ResponseBlock<ListNumberResponseData> getAvailableZsid(BasicRequestBlock<RequestScopesDTO> dto) throws SQLException, JsonException {
        List<Long> zsids = samDAO.getZsids(dto.getData().getType());
        SAMResponseBlock<ListNumberResponseData> respBlock = new SAMResponseBlock<>(REQUEST_ZSID);
        respBlock.setData(new ListNumberResponseData(PARAM_VALUES, zsids));
        respBlock.setE(ERR_OK);
        respBlock.setM(MSG_OK);
        respBlock.setL(ResponseLang.EN_US.getLang());
        return respBlock;
    }
}
