package org.eustrosoft.spec.request;

import org.eustrosoft.json.QJson;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonData;

public class SAMRequestBlock<T extends JsonData> extends TISRequestBlock<T> {
    private T data;

    public SAMRequestBlock(String subsystem, String request, QJson json) {
        super(subsystem, request, json);
    }

    @Override
    public T getDataFromJson(QJson qJson) throws JsonException {
        super.getDataFromJson(qJson);
        return null;
    }

    @Override
    public T getData() {
        return this.data;
    }
}
