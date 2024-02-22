package org.eustrosoft.dic;

import org.eustrosoft.core.Service;
import org.eustrosoft.dic.dao.DicDAO;
import org.eustrosoft.dic.model.DIC;
import org.eustrosoft.dic.transform.DicToDicDtoTransform;
import org.eustrosoft.handlers.dic.dto.DicRequestDto;
import org.eustrosoft.handlers.dic.dto.DicResponseBlock;
import org.eustrosoft.handlers.dic.dto.DicResponseDto;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.ResponseLang;
import org.eustrosoft.spec.request.BasicRequestBlock;
import org.eustrosoft.spec.response.ListStringResponseData;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.eustrosoft.spec.Constants.ERR_OK;
import static org.eustrosoft.spec.Constants.MSG_OK;
import static org.eustrosoft.spec.Constants.PARAM_NAMES;

public class DicService implements Service {

    public DicService() {
    }

    public DicResponseBlock<ListStringResponseData> getDicNames(BasicRequestBlock<?> dto) throws SQLException {
        DicResponseBlock<ListStringResponseData> dicResponse = new DicResponseBlock<>(dto.getR());
        DicDAO dao = new DicDAO(getSession().getConnection());
        List<String> dicNames = dao.getDicNames();
        dicResponse.setData(new ListStringResponseData(PARAM_NAMES, dicNames));
        dicResponse.setE(ERR_OK);
        dicResponse.setM(MSG_OK);
        dicResponse.setL(ResponseLang.EN_US.getLang());
        return dicResponse;
    }

    public DicResponseBlock<DicResponseDto> getDicValues(BasicRequestBlock<DicRequestDto> dto) throws SQLException, JsonException {
        DicResponseBlock<DicResponseDto> dicResponse = new DicResponseBlock<>(dto.getR());
        DicDAO dao = new DicDAO(getSession().getConnection());

        List<DIC> dictionaryValues = dao.getDictionaryValues(dto.getData().getName());

        dicResponse.setData(
                new DicResponseDto(dictionaryValues.stream()
                        .map(new DicToDicDtoTransform()).collect(Collectors.toList()))
        );
        dicResponse.setE(ERR_OK);
        dicResponse.setM(MSG_OK);
        dicResponse.setL(ResponseLang.EN_US.getLang());
        return dicResponse;
    }
}
