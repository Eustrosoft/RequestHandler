package org.eustrosoft.spec.experimental;

import org.eustrosoft.json.QJson;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonParsable;
import org.eustrosoft.spec.interfaces.Request;
import org.eustrosoft.spec.request.BasicRequestBlock;

import java.util.ArrayList;
import java.util.List;

public class SingleRequest<T extends JsonParsable<T>> implements Request {
    private Long time = 0L;

    private BasicRequestBlock<T> requestBlock;

    public SingleRequest(BasicRequestBlock<T> requestBlock) {
        this.requestBlock = requestBlock;
    }

    @Override
    public List<BasicRequestBlock<?>> getR() {
        List<BasicRequestBlock<?>> requestBlocks = new ArrayList<>();
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