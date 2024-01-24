package org.eustrosoft.spec;

import org.eustrosoft.handlers.BasicDTO;
import org.eustrosoft.handlers.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingleResponse<T extends ResponseBlock> implements Response {
    private Long timing;
    private T respType;
    private List<ResponseBlock> responseBlocks;

    public SingleResponse(String json, Class<T> respType) throws IllegalAccessException, InstantiationException, IOException {
        JsonParser parser = new JsonParser();
        BasicDTO<T> t = parser.toObject(json, respType);
        this.responseBlocks = new ArrayList<>();
        this.responseBlocks.add(t);
    }

    @Override
    public List<ResponseBlock> getR() {
        return this.responseBlocks;
    }

    @Override
    public Long getT() {
        return this.timing;
    }
}
