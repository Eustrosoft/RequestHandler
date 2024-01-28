/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms.parameters;

public class HexFileResult {
    private String recordId;
    private String recordVer;
    private String filePid;
    private String filePath;

    public HexFileResult(String recordId, String recordVer, String filePid, String filePath) {
        this.recordId = recordId;
        this.recordVer = recordVer;
        this.filePid = filePid;
        this.filePath = filePath;
    }

    public boolean isEmpty() {
        return recordId == null && recordVer == null && filePid == null && filePath == null;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getRecordVer() {
        return recordVer;
    }

    public void setRecordVer(String recordVer) {
        this.recordVer = recordVer;
    }

    public String getFilePid() {
        return filePid;
    }

    public void setFilePid(String filePid) {
        this.filePid = filePid;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
