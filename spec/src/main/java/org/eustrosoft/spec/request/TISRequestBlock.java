package org.eustrosoft.spec.request;

import org.eustrosoft.json.QJson;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonData;

public class TISRequestBlock<T extends JsonData> extends BasicRequestBlock<T> {
    public TISRequestBlock(String subsystem, String request, QJson json) {
        super(subsystem, request, json);
    }

    @Override
    public T getDataFromJson(QJson qJson) throws JsonException {
        throw new JsonException("Override this method to parse json");
    }

    @Override
    public T getData() {
        return null;
    }
}
