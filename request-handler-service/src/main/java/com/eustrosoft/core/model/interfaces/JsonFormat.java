package com.eustrosoft.core.model.interfaces;

import com.eustrosoft.core.tools.Json;

public interface JsonFormat {
    default String toJson() {
        return Json.fromObject(this);
    }
}
