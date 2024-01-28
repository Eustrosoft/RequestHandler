package org.eustrosoft.spec.response;

import org.eustrosoft.spec.interfaces.Response;
import org.eustrosoft.spec.interfaces.ResponseBlock;

import java.util.List;

public class TISResponse implements Response {
    private List<ResponseBlock> requestBlocks;
    private Long timeout;

    @Override
    public List<ResponseBlock> getR() {
        return this.requestBlocks;
    }

    @Override
    public Long getT() {
        return this.timeout;
    }

    public void setRequestBlocks(List<ResponseBlock> requestBlocks) {
        this.requestBlocks = requestBlocks;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
}
