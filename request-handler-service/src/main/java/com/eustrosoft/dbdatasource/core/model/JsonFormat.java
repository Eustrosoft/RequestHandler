package com.eustrosoft.dbdatasource.core.model;

import com.eustrosoft.core.tools.Json;

public interface JsonFormat {
    default String toJson() {
        return Json.fromObject(this);
    }
}
