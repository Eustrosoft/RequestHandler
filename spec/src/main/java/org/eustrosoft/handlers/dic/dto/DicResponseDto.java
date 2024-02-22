package org.eustrosoft.handlers.dic.dto;

import org.eustrosoft.json.JsonUtil;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonConvertible;

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
        } catch (JsonException ex) {
            return null;
        }
    }
}
