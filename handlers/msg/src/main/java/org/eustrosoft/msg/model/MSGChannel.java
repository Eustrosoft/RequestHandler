/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.msg.model;

import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.core.model.DBObject;
import org.eustrosoft.msg.ranges.MSGChannelStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MSGChannel extends DBObject {
    private String subject;
    private Long documentId;
    private MSGChannelStatus status;

    public MSGChannel() {
    }

    public MSGChannel(Long id, Long zver, Long zrid, String subject, Long docId, MSGChannelStatus msgStatus) {
        super(id, zver, zrid);
        this.subject = subject;
        this.documentId = docId;
        this.status = msgStatus;
    }

    @Override
    public void fillFromResultSet(ResultSet resultSet) throws SQLException {
        super.fillFromResultSet(resultSet);
        trySet(this::setSubject, resultSet, DBConstants.SUBJECT);
        trySet(this::setDocumentId, resultSet, DBConstants.OBJ_ID);
        trySet(this::setStatus, resultSet, DBConstants.STATUS);
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
}
