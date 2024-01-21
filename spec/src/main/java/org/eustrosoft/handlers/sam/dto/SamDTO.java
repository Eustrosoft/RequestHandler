package org.eustrosoft.handlers.sam.dto;


import org.eustrosoft.handlers.cms.dto.BasicResponseBlock;
import org.eustrosoft.handlers.cms.dto.ResponseParams;

public class SamDTO<T> extends BasicResponseBlock<T> {
    private T t;

    public SamDTO(ResponseParams params) {
        super(params);
    }

    @Override
    public T getData() {
        return t;
    }
}
