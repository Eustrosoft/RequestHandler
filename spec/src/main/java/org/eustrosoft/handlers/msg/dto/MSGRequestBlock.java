/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.msg.dto;

import org.eustrosoft.core.json.QJson;
import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonParsable;

public class MSGRequestBlock extends MsgParams implements JsonParsable<MSGRequestBlock> {
    private String id;

    public MSGRequestBlock() {
    }

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        try {
            setId(qJson.getItemString("id"));
        } catch (Exception ex) {
            // ignore
        }
        try {
            fromJson(qJson);
        } catch (Exception ex) {
            // ignore
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public MSGRequestBlock convertToObject(QJson qJson) throws JsonException {
        parseQJson(qJson);
        return this;
    }
}
