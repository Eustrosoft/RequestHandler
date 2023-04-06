package com.eustrosoft.datasource.sources.model;

import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CMSDirectory implements CMSObject {
    private String fileName;
    private String fullPath;
    private List<String> links;
    private long space;
    private Date modified;
    private Date created;
    private String type;

    public CMSType getType() {
        return CMSType.DIRECTORY;
    }
}
