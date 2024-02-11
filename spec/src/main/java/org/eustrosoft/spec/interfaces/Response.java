/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.spec.interfaces;

import org.eustrosoft.json.JsonUtil;
import org.eustrosoft.json.exception.JsonException;

import java.util.List;
import java.util.stream.Collectors;

import static org.eustrosoft.json.Constants.PARAM_DISPATCHER_RESPONSES;
import static org.eustrosoft.json.Constants.PARAM_DISPATCHER_TIMEOUT;

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
                    } catch (JsonException ex) {
                    }
                    return rb;
                }).collect(Collectors.joining(", "))   // response blocks
        );
    }
}
