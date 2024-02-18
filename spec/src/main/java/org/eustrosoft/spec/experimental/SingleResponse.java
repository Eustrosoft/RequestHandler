package org.eustrosoft.spec.experimental;

import org.eustrosoft.spec.interfaces.Response;
import org.eustrosoft.spec.interfaces.ResponseBlock;

import java.util.ArrayList;
import java.util.List;

public class SingleResponse implements Response {
    private Long timing;
    private List<ResponseBlock<?>> responseBlocks;

    public SingleResponse(String json) {
        JsonParser parser = new JsonParser();
        this.responseBlocks = new ArrayList<>();
    }

    @Override
    public List<ResponseBlock<?>> getR() {
        return this.responseBlocks;
    }

    @Override
    public Long getT() {
        return this.timing;
    }
}
