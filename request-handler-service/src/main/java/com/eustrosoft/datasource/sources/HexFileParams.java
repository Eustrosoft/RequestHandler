/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.datasource.sources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HexFileParams {
    private String destination;
    private String recordId;
    private String recordVer;
    private String filePid;
    private String hex;
    private String crc32;
    private String description;
    private Integer securityLevel;
    private Long chunkNumber;
    private Long chunkCount;
}
