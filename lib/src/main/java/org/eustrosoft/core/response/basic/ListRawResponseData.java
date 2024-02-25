package org.eustrosoft.core.response.basic;

import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.util.JsonUtil;

import java.util.List;

public class ListRawResponseData implements JsonConvertible {
    private final String paramName;
    private final List<String> values;

    public ListRawResponseData(String paramName, List<String> values) {
        this.paramName = paramName;
        this.values = values;
    }

    @Override
    public String convertToString() throws JsonException {
        return JsonUtil.toJson(
                JsonUtil.getFormatString(1),
                JsonUtil.AsEntry.getRawCollection(paramName, values)
        );
    }
}
