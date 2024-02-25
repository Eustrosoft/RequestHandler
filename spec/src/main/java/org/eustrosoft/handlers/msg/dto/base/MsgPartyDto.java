package org.eustrosoft.handlers.msg.dto.base;

import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.util.JsonUtil;

public class MsgPartyDto implements JsonConvertible {
    private Long userId;
    private MSGPartyRole role;
    private String lastRead;

    public MsgPartyDto(Long userId, MSGPartyRole role, String lastRead) {
        this.userId = userId;
        this.role = role;
        this.lastRead = lastRead;
    }

    @Override
    public String convertToString() throws JsonException {
        return JsonUtil.toJsonFormatted(
                JsonUtil.AsEntry.getNumberParams("userId", getUserId()),
                JsonUtil.AsEntry.getStringParams("role", getRole().getValue()),
                JsonUtil.AsEntry.getStringParams("lastRead", getLastRead())
        );
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public MSGPartyRole getRole() {
        return role;
    }

    public void setRole(MSGPartyRole role) {
        this.role = role;
    }

    public String getLastRead() {
        return lastRead;
    }

    public void setLastRead(String lastRead) {
        this.lastRead = lastRead;
    }
}
