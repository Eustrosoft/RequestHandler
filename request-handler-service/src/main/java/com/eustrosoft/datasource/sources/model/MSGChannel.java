package com.eustrosoft.datasource.sources.model;

import com.eustrosoft.datasource.sources.ranges.MSGChannelStatus;
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
    private String documentId;
    private MSGChannelStatus status;

    public MSGChannel(String id, String subject, String docId, String msgStatus) {
        setId(id);
        this.subject = subject;
        this.documentId = docId;
        try {
            this.status = MSGChannelStatus.of(msgStatus);
        } catch (Exception ex) {
        }
    }
}
