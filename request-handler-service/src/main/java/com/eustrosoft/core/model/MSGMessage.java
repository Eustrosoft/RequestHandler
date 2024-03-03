/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.model;

import com.eustrosoft.core.dto.UserDTO;
import com.eustrosoft.core.model.ranges.MSGMessageType;
import com.eustrosoft.core.tools.DateTimeZone;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.eustrosoft.core.constants.DBConstants.CONTENT;
import static com.eustrosoft.core.constants.DBConstants.MSG_ID;
import static com.eustrosoft.core.constants.DBConstants.TYPE;

public class MSGMessage extends DBObject {
    private String content;
    private Long answerId;
    private MSGMessageType type;
    private UserDTO user;

    public MSGMessage() {
    }

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
    public void fillFromResultSet(ResultSet resultSet) throws SQLException {
        super.fillFromResultSet(resultSet);
        setContent(resultSet.getString(CONTENT));
        setAnswerId(resultSet.getLong(MSG_ID));
        setType(MSGMessageType.of(resultSet.getString(TYPE)));
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
