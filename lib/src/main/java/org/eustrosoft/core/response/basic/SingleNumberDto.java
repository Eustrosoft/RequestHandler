package org.eustrosoft.core.response.basic;

import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.util.JsonUtil;

public class SingleNumberDto implements JsonConvertible {
    private final String paramName;
    private final Number data;

    public SingleNumberDto(String paramName, Number data) {
        this.paramName = paramName;
        this.data = data;
    }

    @Override
    public String convertToString() throws JsonException {
        return JsonUtil.toJson(
                JsonUtil.getFormatString(1),
                JsonUtil.AsEntry.getNumberParams(paramName, data)
        );
    }
}
