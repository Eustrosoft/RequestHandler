package org.eustrosoft.msg.transform;

import org.eustrosoft.handlers.msg.dto.base.MsgChannelDto;
import org.eustrosoft.msg.model.MSGChannel;

import java.util.function.Function;

public class MSGChannelToDto implements Function<MSGChannel, MsgChannelDto> {
    @Override
    public MsgChannelDto apply(MSGChannel msgChannel) {
        MsgChannelDto msgChannelDto = new MsgChannelDto(
                msgChannel.getSubject(),
                msgChannel.getDocumentId(),
                msgChannel.getStatus()
        );
        msgChannelDto.setZOID(msgChannel.getZOID());
        msgChannelDto.setZVER(msgChannel.getZVER());
        msgChannelDto.setZRID(msgChannel.getZRID());
        msgChannelDto.setZLVL(msgChannel.getZLVL());
        msgChannelDto.setZSID(msgChannel.getZSID());
        return msgChannelDto;
    }
}
