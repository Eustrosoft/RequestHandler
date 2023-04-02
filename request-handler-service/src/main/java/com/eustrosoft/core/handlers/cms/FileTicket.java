package com.eustrosoft.core.handlers.cms;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class FileTicket {
    @Setter
    private final String ticket;
    private final DownloadFileDetails downloadFileDetails;
}
