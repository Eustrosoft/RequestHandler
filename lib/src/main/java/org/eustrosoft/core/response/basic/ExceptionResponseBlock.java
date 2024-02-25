package org.eustrosoft.core.response.basic;

import org.eustrosoft.core.json.interfaces.JsonData;
import org.eustrosoft.core.response.ResponseParams;

public class ExceptionResponseBlock extends BasicResponseBlock<JsonData> {
    public ExceptionResponseBlock(ResponseParams params) {
        super(params);
    }
}
