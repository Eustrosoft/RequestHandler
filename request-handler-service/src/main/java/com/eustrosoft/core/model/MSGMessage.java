package com.eustrosoft.core.model;

import com.eustrosoft.core.context.UserDTO;
import com.eustrosoft.core.model.ranges.MSGMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MSGMessage extends DBObject {
    private String content;
    private String answerId;
    private MSGMessageType type;
    private UserDTO user;

    public MSGMessage(Long id, String content, String answerId, MSGMessageType type) {
        setZoid(id);
        this.content = content;
        this.answerId = answerId;
        this.type = type;
    }

    public MSGMessage(String content, String answerId, MSGMessageType type) {
        this.content = content;
        this.answerId = answerId;
        this.type = type;
    }
}
