package com.eustrosoft.core.model;

import com.eustrosoft.core.dto.UserDTO;
import com.eustrosoft.core.model.ranges.MSGMessageType;
import lombok.*;

import java.sql.ResultSet;

import static com.eustrosoft.core.constants.DBConstants.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MSGMessage extends DBObject {
    private String content;
    private Long answerId;
    private MSGMessageType type;
    private UserDTO user;

    public MSGMessage(Long id, String content, Long answerId, MSGMessageType type) {
        setZoid(id);
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
        setContent(resultSet.getString(CONTENT));
        setAnswerId(resultSet.getLong(MSG_ID));
        setType(MSGMessageType.of(resultSet.getString(TYPE)));
    }
}
