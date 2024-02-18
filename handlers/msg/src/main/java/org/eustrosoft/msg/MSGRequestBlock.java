/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.msg;

import org.eustrosoft.json.QJson;
import org.eustrosoft.spec.request.BasicRequestBlock;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_MSG;

public class MSGRequestBlock extends BasicRequestBlock {
    private String id;
    private MsgParams params;

    public MSGRequestBlock(String request, QJson qJson) {
        super(SUBSYSTEM_MSG, request, qJson);
        parseQJson(qJson);
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
