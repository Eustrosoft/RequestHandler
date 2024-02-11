/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.spec.request;

import org.eustrosoft.json.QJson;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonParsable;
import org.eustrosoft.spec.interfaces.RequestBlock;

import static org.eustrosoft.json.Constants.PARAM_DISPATCHER_DATA;

public class BasicRequestBlock<T extends JsonParsable<T>> implements RequestBlock<T> {
    protected final String s;
    protected final String r;
    protected QJson json;
    protected T data;

    public BasicRequestBlock(String subsystem, String request, QJson json) {
        this.s = subsystem;
        this.r = request;
        this.json = json.getItemQJson(PARAM_DISPATCHER_DATA);
    }

    @Override
    public String getS() {
        return s;
    }

    @Override
    public String getR() {
        return r;
    }

    @Override
    public T getData() throws JsonException {
        return this.data.convertToObject(json);
    }
}
