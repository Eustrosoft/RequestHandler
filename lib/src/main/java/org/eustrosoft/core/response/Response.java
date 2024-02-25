/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.response;

import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.util.JsonUtil;

import java.util.List;
import java.util.stream.Collectors;

import static org.eustrosoft.core.json.Constants.PARAM_DISPATCHER_RESPONSES;
import static org.eustrosoft.core.json.Constants.PARAM_DISPATCHER_TIMEOUT;

public interface Response extends JsonConvertible {

    List<ResponseBlock> getR();

    Long getT();

    default String convertToString() throws JsonException {
        return String.format(
                "{%s, %s:[%s]}",
                JsonUtil.AsEntry.getNumberParams(PARAM_DISPATCHER_TIMEOUT, getT()),             // Timeout param as %s:%s
                JsonUtil.ParamUtil.getString(PARAM_DISPATCHER_RESPONSES),                       // r - responses
                getR().stream().map(r -> {
                    String rb = "";
                    try {
                        rb = r.toJsonString();
                    } catch (JsonException e) {
                        e.printStackTrace();
                    }
                    return rb;
                }).collect(Collectors.joining(", "))   // response blocks
        );
    }
}
