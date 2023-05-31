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
public class CMSGeneralObject implements CMSObject {
    private String id;
    private String extension;
    private String fileName;
    private String fullPath;
    private List<String> links;
    private Date created;
    private Date modified;
    private long space;
    private String hash;
    private CMSType type;
    private String description;
}