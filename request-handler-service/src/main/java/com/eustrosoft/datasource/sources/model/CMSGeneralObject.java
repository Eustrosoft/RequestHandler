/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

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
    private Long space;
    private Integer securityLevel;
    private String hash;
    private CMSType type;
    private String description;
}
