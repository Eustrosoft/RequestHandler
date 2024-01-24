package org.eustrosoft.handlers.sam.dto;

import org.eustrosoft.spec.BasicRequestBlock;

public class SamRequestBlockDTO<T> extends BasicRequestBlock<T> {
    public SamRequestBlockDTO(String request, T data) {
        super("sam", request, data);
    }

    @Override
    public T getData() {
        return data;
    }
}
