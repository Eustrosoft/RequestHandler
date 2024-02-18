package org.eustrosoft.spec.response;

import org.eustrosoft.spec.ResponseParams;
import org.eustrosoft.spec.interfaces.JsonConvertible;

public class ExceptionResponseBlock extends BasicResponseBlock<JsonConvertible> {
    public ExceptionResponseBlock(ResponseParams params) {
        super(params);
    }
}
