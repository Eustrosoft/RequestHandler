/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms.parameters;

import lombok.*;

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
    private Long chunkSize;
}
