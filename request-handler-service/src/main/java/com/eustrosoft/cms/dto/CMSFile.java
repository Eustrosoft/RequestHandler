/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.cms.dto;

import com.eustrosoft.cms.CMSType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CMSFile extends CMSGeneralObject {
    private String extension;
    private String fileName;
    private String fullPath;
    private List<String> links;
    private Long space;
    private String hash;
    private String description;

    public CMSType getType() {
        return CMSType.FILE;
    }
}
