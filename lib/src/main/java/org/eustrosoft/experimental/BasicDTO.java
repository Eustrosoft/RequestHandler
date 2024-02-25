package org.eustrosoft.experimental;

import org.eustrosoft.core.json.interfaces.JsonData;
import org.eustrosoft.core.response.ResponseParams;
import org.eustrosoft.core.response.basic.BasicResponseBlock;

public class BasicDTO<T extends JsonData<T>> extends BasicResponseBlock<T> {
    private T data;

    public BasicDTO(ResponseParams params, T data) {
        super(params);
        this.data = data;
    }

    public BasicDTO(T data) {
        this.data = data;
    }

    @Override
    public T getData() {
        return this.data;
    }
}
