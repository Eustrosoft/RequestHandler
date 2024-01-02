package org.eustrosoft.core.model.interfaces;

import org.eustrosoft.core.tools.Json;

public interface JsonFormat {
    default String toJson() {
        return Json.fromObject(this);
    }
}
