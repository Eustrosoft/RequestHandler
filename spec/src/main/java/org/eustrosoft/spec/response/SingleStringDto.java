package org.eustrosoft.spec.response;

import org.eustrosoft.json.JsonUtil;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonConvertible;

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
