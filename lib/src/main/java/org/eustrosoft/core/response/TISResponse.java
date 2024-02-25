package org.eustrosoft.core.response;

import java.util.List;

public class TISResponse implements Response {
    private List<ResponseBlock> responseBlocks;
    private Long timeout;

    @Override
    public List<ResponseBlock> getR() {
        return this.responseBlocks;
    }

    @Override
    public Long getT() {
        return this.timeout;
    }

    public void setResponseBlocks(List<ResponseBlock> responseBlocks) {
        this.responseBlocks = responseBlocks;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
}
