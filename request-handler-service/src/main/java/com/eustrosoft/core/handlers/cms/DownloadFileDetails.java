package com.eustrosoft.core.handlers.cms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
@AllArgsConstructor
public class DownloadFileDetails {
    private InputStream inputStream;
    private String fileName;
    private Long fileLength;
}
