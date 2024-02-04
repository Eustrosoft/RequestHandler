package org.eustrosoft.spec.experimental;

import org.eustrosoft.spec.ResponseParams;
import org.eustrosoft.spec.interfaces.JsonData;
import org.eustrosoft.spec.response.BasicResponseBlock;

public class BasicDTO<T extends JsonData> extends BasicResponseBlock<T> {
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
