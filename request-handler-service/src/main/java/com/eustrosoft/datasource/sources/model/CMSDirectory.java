package com.eustrosoft.datasource.sources.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public CMSType getType() {
        return CMSType.DIRECTORY;
    }
}
