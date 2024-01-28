/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms.filedownload;

import java.io.InputStream;

public class DownloadFileDetails {
    private InputStream inputStream;
    private String fileName;
    private Long fileLength;

    public DownloadFileDetails(InputStream inputStream, String fileName, Long fileLength) {
        this.inputStream = inputStream;
        this.fileName = fileName;
        this.fileLength = fileLength;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileLength() {
        return fileLength;
    }

    public void setFileLength(Long fileLength) {
        this.fileLength = fileLength;
    }
}
