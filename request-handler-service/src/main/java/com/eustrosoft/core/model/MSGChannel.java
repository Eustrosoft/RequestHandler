/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.model;

import com.eustrosoft.core.model.ranges.MSGChannelStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.sql.ResultSet;

import static com.eustrosoft.core.constants.DBConstants.OBJ_ID;
import static com.eustrosoft.core.constants.DBConstants.STATUS;
import static com.eustrosoft.core.constants.DBConstants.SUBJECT;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MSGChannel extends DBObject {
    private String subject;
    private Long documentId;
    private MSGChannelStatus status;

    public MSGChannel(Long id, Long zver, Long zrid, String subject, Long docId, MSGChannelStatus msgStatus) {
        super(id, zver, zrid);
        this.subject = subject;
        this.documentId = docId;
        this.status = msgStatus;
    }

    @Override
    @SneakyThrows
    public void fillFromResultSet(ResultSet resultSet) {
        super.fillFromResultSet(resultSet);
        trySet(this::setSubject, resultSet, SUBJECT);
        trySet(this::setDocumentId, resultSet, OBJ_ID);
        trySet(this::setStatus, resultSet, STATUS);
    }

    // In database it is String - need to extract from string to Enum
    public void setStatus(String status) {
        if (status != null) {
            this.status = MSGChannelStatus.of(status);
        }
    }

    public void merge(MSGChannel otherChannel) {
        if (this.subject == null) {
            this.subject = otherChannel.getSubject();
        }
        if (this.documentId == null) {
            this.documentId = otherChannel.getDocumentId();
        }
        if (this.status == null) {
            this.status = otherChannel.getStatus();
        }
    }
}
