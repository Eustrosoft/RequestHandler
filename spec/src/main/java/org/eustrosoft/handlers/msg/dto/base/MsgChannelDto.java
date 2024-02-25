package org.eustrosoft.handlers.msg.dto.base;

import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.handlers.tis.dto.TISDto;
import org.eustrosoft.util.JsonUtil;

public class MsgChannelDto extends TISDto implements JsonConvertible {
    private String subject;
    private Long documentId;
    private MSGChannelStatus status;

    public MsgChannelDto(String subject, Long documentId, MSGChannelStatus status) {
        this.subject = subject;
        this.documentId = documentId;
        this.status = status;
    }

    @Override
    public String convertToString() throws JsonException {
        return JsonUtil.toJsonFormatted(
                JsonUtil.AsEntry.getNumberParams(DBConstants.ZOID, getZOID()),
                JsonUtil.AsEntry.getNumberParams(DBConstants.ZRID, getZRID()),
                JsonUtil.AsEntry.getNumberParams(DBConstants.ZVER, getZVER()),
                JsonUtil.AsEntry.getNumberParams(DBConstants.ZSID, getZSID()),
                JsonUtil.AsEntry.getStringParams("subject", getSubject()),
                JsonUtil.AsEntry.getNumberParams("documentId", getDocumentId()),
                JsonUtil.AsEntry.getStringParams("status", getStatus().getValue())
        );
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public MSGChannelStatus getStatus() {
        return status;
    }

    public void setStatus(MSGChannelStatus status) {
        this.status = status;
    }
}
