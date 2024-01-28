package org.eustrosoft.spec.response;

import org.eustrosoft.spec.ResponseParams;
import org.eustrosoft.spec.interfaces.JsonData;

public class ExceptionResponseBlock<T extends JsonData> extends BasicResponseBlock<T> {
    public ExceptionResponseBlock(ResponseParams params) {
        super(params);
    }
}
