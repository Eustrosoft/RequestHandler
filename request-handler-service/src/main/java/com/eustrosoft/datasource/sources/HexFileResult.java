/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.datasource.sources;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HexFileResult {
    private String recordId;
    private String recordVer;
    private String filePid;
    private String filePath;

    public boolean isEmpty() {
        return recordId == null && recordVer == null && filePid == null && filePath == null;
    }
}
