package org.eustrosoft.spec.response;

import org.eustrosoft.json.JsonUtil;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonConvertible;

import java.util.List;

public class ListStringResponseData implements JsonConvertible {
    private final String paramName;
    private final List<String> values;

    public ListStringResponseData(String paramName, List<String> values) {
        this.paramName = paramName;
        this.values = values;
    }

    @Override
    public String convertToString() throws JsonException {
        return JsonUtil.toJson(
                JsonUtil.getFormatString(1),
                JsonUtil.AsEntry.getStringCollection(paramName, values)
        );
    }
}
