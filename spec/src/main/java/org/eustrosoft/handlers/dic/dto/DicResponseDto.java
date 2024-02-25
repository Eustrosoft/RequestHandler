package org.eustrosoft.handlers.dic.dto;

import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.util.JsonUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DicResponseDto implements JsonConvertible {
    private final List<DicDto> values;

    public DicResponseDto(List<DicDto> values) {
        this.values = values;
    }

    @Override
    public String convertToString() throws JsonException {
        return JsonUtil.toJson(
                JsonUtil.getFormatString(1),
                JsonUtil.AsEntry.getRawCollection(
                        "values",
                        values.stream().map(this::getValue).filter(Objects::nonNull).collect(Collectors.toList())
                )
        );
    }

    private String getValue(DicDto dto) {
        try {
            return dto.convertToString();
        } catch (Exception ex) {
            return null;
        }
    }
}
