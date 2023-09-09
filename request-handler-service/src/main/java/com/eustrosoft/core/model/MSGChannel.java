package com.eustrosoft.core.model;

import com.eustrosoft.core.model.ranges.MSGChannelStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MSGChannel extends DBObject {
    private String subject;
    private Long documentId;
    private MSGChannelStatus status;

    public MSGChannel(Long id, String subject, Long docId, String msgStatus) {
        setZoid(id);
        this.subject = subject;
        this.documentId = docId;
        try {
            this.status = MSGChannelStatus.of(msgStatus);
        } catch (Exception ex) {
        }
    }
}
