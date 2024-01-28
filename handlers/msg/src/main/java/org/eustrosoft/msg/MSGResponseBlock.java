/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.msg;

import org.eustrosoft.msg.model.MSGChannel;
import org.eustrosoft.msg.model.MSGMessage;

import java.util.List;

public final class MSGResponseBlock extends BasicResponse {
    private String errMsg = "";
    private Short errCode = 0;
    private String responseType;
    private List<MSGChannel> chats;
    private List<MSGMessage> messages;

    public void setE(int code) {
        errCode = (short) code;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
