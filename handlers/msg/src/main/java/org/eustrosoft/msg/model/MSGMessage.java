/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.msg.model;

import lombok.*;
import org.eustrosoft.core.constants.DBConstants;
import org.eustrosoft.core.dto.UserDTO;
import org.eustrosoft.core.model.ranges.MSGMessageType;
import org.eustrosoft.core.tools.DateTimeZone;

import java.sql.ResultSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MSGMessage extends DBObject {
    private String content;
    private Long answerId;
    private MSGMessageType type;
    private UserDTO user;

    public MSGMessage(Long id, Long zver, Long zrid, DateTimeZone created, String content, Long answerId, MSGMessageType type) {
        super(id, zver, zrid, created);
        this.content = content;
        this.answerId = answerId;
        this.type = type;
    }

    public MSGMessage(Long id, Long zver, Long zrid, String content, Long answerId, MSGMessageType type) {
        super(id, zver, zrid);
        this.content = content;
        this.answerId = answerId;
        this.type = type;
    }

    public MSGMessage(String content, Long answerId, MSGMessageType type) {
        this.content = content;
        this.answerId = answerId;
        this.type = type;
    }

    @Override
    @SneakyThrows
    public void fillFromResultSet(ResultSet resultSet) {
        super.fillFromResultSet(resultSet);
        setContent(resultSet.getString(DBConstants.CONTENT));
        setAnswerId(resultSet.getLong(DBConstants.MSG_ID));
        setType(MSGMessageType.of(resultSet.getString(DBConstants.TYPE)));
    }

    public void merge(MSGMessage otherMessage) {
        if (this.content == null) {
            this.content = otherMessage.getContent();
        }
        if (this.answerId == null) {
            this.answerId = otherMessage.getAnswerId();
        }
        if (this.type == null) {
            this.type = otherMessage.getType();
        }
    }
}
