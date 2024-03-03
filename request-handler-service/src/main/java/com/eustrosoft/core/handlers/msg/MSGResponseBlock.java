/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.msg;

import com.eustrosoft.core.handlers.responses.BasicResponse;
import com.eustrosoft.core.model.MSGChannel;
import com.eustrosoft.core.model.MSGMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_MSG;

@Getter
@Setter
public final class MSGResponseBlock extends BasicResponse {
    private List<MSGChannel> chats;
    private List<MSGMessage> messages;


    public MSGResponseBlock() {
        super(SUBSYSTEM_MSG);
    }
}
