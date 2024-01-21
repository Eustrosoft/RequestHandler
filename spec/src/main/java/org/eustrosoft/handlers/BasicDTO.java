package org.eustrosoft.handlers;

import org.eustrosoft.handlers.cms.dto.BasicResponseBlock;
import org.eustrosoft.handlers.cms.dto.ResponseParams;

public class BasicDTO<T> extends BasicResponseBlock<T> {
    private T data;

    public BasicDTO(T data) {
        this.data = data;
    }

    public BasicDTO(ResponseParams params, T data) {
        super(params);
        this.data = data;
    }

    @Override
    public T getData() {
        return this.data;
    }
}
