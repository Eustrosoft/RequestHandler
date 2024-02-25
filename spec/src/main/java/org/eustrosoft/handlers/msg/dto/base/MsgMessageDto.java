package org.eustrosoft.handlers.msg.dto.base;

import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.handlers.sam.dto.UserDTO;
import org.eustrosoft.handlers.tis.dto.TISDto;
import org.eustrosoft.util.JsonUtil;

public class MsgMessageDto extends TISDto implements JsonConvertible {
    private String content;
    private Long answerId;
    private MSGMessageType type;
    private UserDTO user;

    public MsgMessageDto(String content, Long answerId, MSGMessageType type, UserDTO user) {
        this.content = content;
        this.answerId = answerId;
        this.type = type;
        this.user = user;
    }

    @Override
    public String convertToString() throws JsonException {
        return JsonUtil.toJsonFormatted(
                JsonUtil.AsEntry.getNumberParams(DBConstants.ZOID, getZOID()),
                JsonUtil.AsEntry.getNumberParams(DBConstants.ZRID, getZRID()),
                JsonUtil.AsEntry.getNumberParams(DBConstants.ZVER, getZVER()),
                JsonUtil.AsEntry.getNumberParams(DBConstants.ZSID, getZSID()),
                JsonUtil.AsEntry.getStringParams("content", getContent()),
                JsonUtil.AsEntry.getNumberParams("answerId", getAnswerId()),
                JsonUtil.AsEntry.getStringParams("type", getType().getValue()),
                JsonUtil.AsEntry.getRawParams("user", user.convertToString())
        );
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public MSGMessageType getType() {
        return type;
    }

    public void setType(MSGMessageType type) {
        this.type = type;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
