package com.eustrosoft.datasource.sources.model;

import com.eustrosoft.core.context.UserDTO;
import com.eustrosoft.datasource.sources.ranges.MSGMessageType;
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

    public MSGMessage(String id, String content, String answerId, MSGMessageType type) {
        setId(id);
        this.content = content;
        this.answerId = answerId;
        this.type = type;
    }
}
