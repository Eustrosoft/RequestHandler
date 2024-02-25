package org.eustrosoft.experimental;

import org.eustrosoft.core.json.QJson;
import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonData;
import org.eustrosoft.core.request.Request;
import org.eustrosoft.core.request.RequestBlock;

import java.util.ArrayList;
import java.util.List;

public class SingleTISRequest<T extends JsonData> implements Request {
    private Long time = 0L;

    private RequestBlock<T> requestBlock;

    public SingleTISRequest(RequestBlock<T> requestBlock) {
        this.requestBlock = requestBlock;
    }

    @Override
    public List<RequestBlock<?>> getR() {
        List<RequestBlock<?>> requestBlocks = new ArrayList<>();
        requestBlocks.add(requestBlock);
        return requestBlocks;
    }

    @Override
    public Long getT() {
        return this.time;
    }

    @Override
    public void fromJson(QJson qJson) throws JsonException {

    }
}