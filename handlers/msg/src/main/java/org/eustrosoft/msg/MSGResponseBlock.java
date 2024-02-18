/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.msg;

import org.eustrosoft.msg.model.MSGChannel;
import org.eustrosoft.msg.model.MSGMessage;
import org.eustrosoft.spec.response.BasicResponseBlock;

import java.util.List;

public final class MSGResponseBlock extends BasicResponseBlock {
    private List<MSGChannel> chats;
    private List<MSGMessage> messages;

    public List<MSGChannel> getChats() {
        return chats;
    }

    public void setChats(List<MSGChannel> chats) {
        this.chats = chats;
    }

    public List<MSGMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<MSGMessage> messages) {
        this.messages = messages;
    }
}
