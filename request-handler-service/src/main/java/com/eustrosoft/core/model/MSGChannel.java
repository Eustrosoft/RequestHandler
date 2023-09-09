package com.eustrosoft.core.model;

import com.eustrosoft.core.model.ranges.MSGChannelStatus;
import lombok.*;

import java.sql.ResultSet;

import static com.eustrosoft.core.constants.DBConstants.*;

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

    @Override
    @SneakyThrows
    public void fillFromResultSet(ResultSet resultSet) {
        super.fillFromResultSet(resultSet);
        setSubject(resultSet.getString(SUBJECT));
        setDocumentId(resultSet.getLong(OBJ_ID));
        setStatus(MSGChannelStatus.of(resultSet.getString(STATUS)));
    }
}
