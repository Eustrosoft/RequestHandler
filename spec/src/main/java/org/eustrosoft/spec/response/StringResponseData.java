package org.eustrosoft.spec.response;

import org.eustrosoft.json.JsonUtil;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonData;

public class StringResponseData implements JsonData {
    private final String answer;

    public StringResponseData(String answer) {
        this.answer = answer;
    }

    @Override
    public String toJsonString() throws JsonException {
        return JsonUtil.toJson(
                JsonUtil.getFormatString(1),
                JsonUtil.AsEntry.getStringParams("answer", answer)
        );
    }
}
