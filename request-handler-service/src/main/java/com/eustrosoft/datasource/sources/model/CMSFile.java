package com.eustrosoft.datasource.sources.model;

import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CMSFile implements CMSObject {
    // todo: create abstract class and move all same there
    private String extension;
    private String fileName;
    private String fullPath;
    private List<String> links;
    private Date created;
    private Date modified;
    private long space;
    private String hash;

    public CMSType getType() {
        return CMSType.FILE;
    }
}
