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
}
