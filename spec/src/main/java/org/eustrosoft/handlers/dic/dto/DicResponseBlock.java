package org.eustrosoft.handlers.dic.dto;

import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.core.response.basic.BasicResponseBlock;

import static org.eustrosoft.constants.Constants.SUBSYSTEM_DIC;

public class DicResponseBlock<T extends JsonConvertible> extends BasicResponseBlock<T> {
    private T data;

    public DicResponseBlock(String request) {
        this.s = SUBSYSTEM_DIC;
        this.r = request;
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public void setData(T data) {
        this.data = data;
    }
}
