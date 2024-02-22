package org.eustrosoft.spec.response;

import org.eustrosoft.json.JsonUtil;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonConvertible;

import java.util.List;

public class ListNumberResponseData implements JsonConvertible {
    private final String paramName;
    private final List<? extends Number> values;

    public ListNumberResponseData(String paramName, List<? extends Number> values) {
        this.paramName = paramName;
        this.values = values;
    }

    @Override
    public String convertToString() throws JsonException {
        return JsonUtil.toJson(
                JsonUtil.getFormatString(1),
                JsonUtil.AsEntry.getNumberCollection(paramName, values)
        );
    }
}
