/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.dto;

import com.eustrosoft.core.model.DBObject;
import com.eustrosoft.core.model.ranges.CMSType;
import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CMSGeneralObject extends DBObject implements CMSObject {
    private String extension;
    private String fileName;
    private String fullPath;
    private List<String> links;
    private Long space;
    private Integer securityLevel;
    private String hash;
    private CMSType type;
    private String description;
    private Date created;
    private Date modified;

    public CMSGeneralObject(String extension, String fileName, String fullPath, List<String> links,
                            Long space, Integer securityLevel, String hash, CMSType type, String description) {
        this.extension = extension;
        this.fileName = fileName;
        this.fullPath = fullPath;
        this.links = links;
        this.space = space;
        this.securityLevel = securityLevel;
        this.hash = hash;
        this.type = type;
        this.description = description;
    }

    @Override
    public String toJson() {
        return this.toString();
    }
}
