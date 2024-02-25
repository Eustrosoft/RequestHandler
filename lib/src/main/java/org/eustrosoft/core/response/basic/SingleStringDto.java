package org.eustrosoft.core.response.basic;

import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.util.JsonUtil;

public class SingleStringDto implements JsonConvertible {
    private final String paramName;
    private final String data;

    public SingleStringDto(String paramName, String data) {
        this.paramName = paramName;
        this.data = data;
    }

    @Override
    public String convertToString() throws JsonException {
        return JsonUtil.toJson(
                JsonUtil.getFormatString(1),
                JsonUtil.AsEntry.getStringParams(paramName, data)
        );
    }
}
