/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.response;

import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.util.JsonUtil;

import static org.eustrosoft.core.json.Constants.JSON_NULL;
import static org.eustrosoft.core.json.Constants.PARAM_DISPATCHER_DATA;
import static org.eustrosoft.core.json.Constants.PARAM_DISPATCHER_ERROR;
import static org.eustrosoft.core.json.Constants.PARAM_DISPATCHER_LANG;
import static org.eustrosoft.core.json.Constants.PARAM_DISPATCHER_MESSAGE;
import static org.eustrosoft.core.json.Constants.PARAM_DISPATCHER_REQUEST;
import static org.eustrosoft.core.json.Constants.PARAM_DISPATCHER_SUBSYSTEM;


public interface ResponseBlock<T extends JsonConvertible> {
    String getS();

    String getR();

    Long getE();

    String getM();

    String getL();

    T getData();

    default String toJsonString() throws JsonException {
        T data = getData();
        return JsonUtil.toJsonFormatted(
                JsonUtil.AsEntry.getStringParams(PARAM_DISPATCHER_SUBSYSTEM, getS()),
                JsonUtil.AsEntry.getStringParams(PARAM_DISPATCHER_REQUEST, getR()),
                JsonUtil.AsEntry.getNumberParams(PARAM_DISPATCHER_ERROR, getE()),
                JsonUtil.AsEntry.getStringParams(PARAM_DISPATCHER_MESSAGE, getM()),
                JsonUtil.AsEntry.getStringParams(PARAM_DISPATCHER_LANG, getL()),
                JsonUtil.AsEntry.getRawParams(PARAM_DISPATCHER_DATA, data == null ? JSON_NULL : data.convertToString())
        );
    }
}
