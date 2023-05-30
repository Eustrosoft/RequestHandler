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
    private Long chunkNumber;
    private Long chunkCount;
}
