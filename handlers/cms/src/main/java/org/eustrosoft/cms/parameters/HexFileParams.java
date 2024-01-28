/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms.parameters;

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

    public HexFileParams() {
    }

    public HexFileParams(String destination, String recordId, String recordVer, String filePid, String hex, String crc32, String description, Integer securityLevel, Long chunkNumber, Long chunkCount, Long chunkSize) {
        this.destination = destination;
        this.recordId = recordId;
        this.recordVer = recordVer;
        this.filePid = filePid;
        this.hex = hex;
        this.crc32 = crc32;
        this.description = description;
        this.securityLevel = securityLevel;
        this.chunkNumber = chunkNumber;
        this.chunkCount = chunkCount;
        this.chunkSize = chunkSize;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
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

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getCrc32() {
        return crc32;
    }

    public void setCrc32(String crc32) {
        this.crc32 = crc32;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    public Long getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(Long chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public Long getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(Long chunkCount) {
        this.chunkCount = chunkCount;
    }

    public Long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Long chunkSize) {
        this.chunkSize = chunkSize;
    }
}
