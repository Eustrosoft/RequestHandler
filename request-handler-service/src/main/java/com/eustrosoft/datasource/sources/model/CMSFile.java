package com.eustrosoft.datasource.sources.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CMSFile extends CMSGeneralObject {
    private String id;
    private String extension;
    private String fileName;
    private String fullPath;
    private List<String> links;
    private Date created;
    private Date modified;
    private Long space;
    private String hash;
    private String description;

    public CMSType getType() {
        return CMSType.FILE;
    }
}
