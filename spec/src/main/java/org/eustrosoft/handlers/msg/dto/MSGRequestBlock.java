/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.msg.dto;

import org.eustrosoft.core.json.QJson;
import org.eustrosoft.core.request.BasicRequestBlock;

import static org.eustrosoft.constants.Constants.SUBSYSTEM_MSG;

public class MSGRequestBlock extends BasicRequestBlock {
    private String id;
    private MsgParams params;

    public MSGRequestBlock(String request, QJson json) {
        super(SUBSYSTEM_MSG, request, json);
    }

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        try {
            setId(qJson.getItemString("id"));
        } catch (Exception ex) {
        }
        try {
            setParams(MsgParams.fromJson(qJson.getItemQJson("params")));
        } catch (Exception ex) {
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MsgParams getParams() {
        return params;
    }

    public void setParams(MsgParams params) {
        this.params = params;
    }
}
