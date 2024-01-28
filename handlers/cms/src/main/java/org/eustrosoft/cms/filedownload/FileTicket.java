/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms.filedownload;

public class FileTicket {
    private final String ticket;
    private final DownloadFileDetails downloadFileDetails;

    public FileTicket(String ticket, DownloadFileDetails downloadFileDetails) {
        this.ticket = ticket;
        this.downloadFileDetails = downloadFileDetails;
    }

    public String getTicket() {
        return ticket;
    }

    public DownloadFileDetails getDownloadFileDetails() {
        return downloadFileDetails;
    }
}
