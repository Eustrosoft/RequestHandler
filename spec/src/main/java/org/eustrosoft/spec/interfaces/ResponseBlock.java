/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.spec.interfaces;

import org.eustrosoft.json.JsonUtil;
import org.eustrosoft.json.exception.JsonException;

import static org.eustrosoft.json.Constants.*;

public interface ResponseBlock<T extends JsonData> {
    String getS();

    String getR();

    Long getE();

    String getM();

    String getL();

    T getData();

    default String toJsonString() throws JsonException {
        T data = getData();
        return JsonUtil.toJson(
                JsonUtil.getFormatString(6),
                JsonUtil.AsEntry.getStringParams(PARAM_DISPATCHER_SUBSYSTEM, getS()),
                JsonUtil.AsEntry.getStringParams(PARAM_DISPATCHER_REQUEST, getR()),
                JsonUtil.AsEntry.getNumberParams(PARAM_DISPATCHER_ERROR, getE()),
                JsonUtil.AsEntry.getStringParams(PARAM_DISPATCHER_MESSAGE, getM()),
                JsonUtil.AsEntry.getStringParams(PARAM_DISPATCHER_LANG, getL()),
                JsonUtil.AsEntry.getRawParams(PARAM_DISPATCHER_DATA, data == null ? "null" : data.toJsonString())
        );
    }
}
