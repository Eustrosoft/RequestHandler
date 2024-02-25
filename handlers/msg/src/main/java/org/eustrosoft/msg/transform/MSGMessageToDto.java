package org.eustrosoft.msg.transform;

import org.eustrosoft.handlers.msg.dto.base.MsgMessageDto;
import org.eustrosoft.msg.model.MSGMessage;

import java.util.function.Function;

public class MSGMessageToDto implements Function<MSGMessage, MsgMessageDto> {

    @Override
    public MsgMessageDto apply(MSGMessage msgMessage) {
        MsgMessageDto msgMessageDto = new MsgMessageDto(
                msgMessage.getContent(),
                msgMessage.getAnswerId(),
                msgMessage.getType(),
                msgMessage.getUser()
        );
        msgMessageDto.setZOID(msgMessage.getZOID());
        msgMessageDto.setZVER(msgMessage.getZVER());
        msgMessageDto.setZRID(msgMessage.getZRID());
        msgMessageDto.setZLVL(msgMessage.getZLVL());
        msgMessageDto.setZSID(msgMessage.getZSID());
        return msgMessageDto;
    }
}
