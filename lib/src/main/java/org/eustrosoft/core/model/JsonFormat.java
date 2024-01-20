package org.eustrosoft.core.model;

import org.eustrosoft.core.tools.Json;

public interface JsonFormat {
    default String toJson() {
        return Json.fromObject(this);
    }
}
