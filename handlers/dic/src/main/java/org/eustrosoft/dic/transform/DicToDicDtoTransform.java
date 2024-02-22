package org.eustrosoft.dic.transform;

import org.eustrosoft.dic.model.DIC;
import org.eustrosoft.handlers.dic.dto.DicDto;

import java.util.function.Function;

public class DicToDicDtoTransform implements Function<DIC, DicDto> {
    @Override
    public DicDto apply(DIC dic) {
        return new DicDto(dic.getDic(), dic.getValue(), dic.getCode(), dic.getDescr());
    }
}
