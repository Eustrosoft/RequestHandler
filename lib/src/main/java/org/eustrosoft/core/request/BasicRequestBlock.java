/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.request;

import org.eustrosoft.core.json.QJson;
import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonParsable;

import static org.eustrosoft.core.json.Constants.PARAM_DISPATCHER_DATA;

public class BasicRequestBlock<T extends JsonParsable<T>> implements RequestBlock<T> {
    protected String s;
    protected String r;
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

    public QJson getJson() {
        return json;
    }

    @Override
    public T getData() throws JsonException {
        return this.data.convertToObject(json);
    }

    public void setData(T data) {
        this.data = data;
    }
}
