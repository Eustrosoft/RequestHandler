/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.dto;

import com.eustrosoft.core.model.ranges.CMSType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CMSDirectory extends CMSGeneralObject {
    private String fileName;
    private String fullPath;
    private List<String> links;
    private Long space;
    private String description;

    public CMSType getType() {
        return CMSType.DIRECTORY;
    }
}
