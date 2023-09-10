package com.eustrosoft.core.model;

import com.eustrosoft.core.dto.UserDTO;
import com.eustrosoft.core.model.interfaces.Updatable;
import com.eustrosoft.core.model.ranges.MSGMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.sql.ResultSet;

import static com.eustrosoft.core.constants.DBConstants.CONTENT;
import static com.eustrosoft.core.constants.DBConstants.MSG_ID;
import static com.eustrosoft.core.constants.DBConstants.TYPE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MSGMessage extends DBObject implements Updatable {
    private String content;
    private Long answerId;
    private MSGMessageType type;
    private UserDTO user;

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
        setContent(resultSet.getString(CONTENT));
        setAnswerId(resultSet.getLong(MSG_ID));
        setType(MSGMessageType.of(resultSet.getString(TYPE)));
    }

    public String toUpdateString() {
        return String.format(
                "%s, %s, %s, '%s', %s, %s",
                getZoid(),
                getZver(),
                getZrid(),
                content,
                answerId,
                type == null ? "null" : String.format("'%s'", type.getValue())
        );
    }
}
