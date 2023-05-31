package com.eustrosoft.datasource.sources;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class FileDetails {
    private String mimeType;
    private String fileName;
    private Long fileLength;
    private String encoding;
}
