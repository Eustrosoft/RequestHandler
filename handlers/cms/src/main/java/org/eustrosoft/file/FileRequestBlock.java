/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.file;

import org.eustrosoft.constants.Constants;
import org.eustrosoft.core.json.Json;
import org.eustrosoft.core.json.QJson;
import org.eustrosoft.core.json.interfaces.JsonParsable;
import org.eustrosoft.core.request.BasicRequestBlock;

import java.util.Base64;

import static org.eustrosoft.constants.Constants.SUBSYSTEM_FILE;

public class FileRequestBlock<T extends JsonParsable<T>> extends BasicRequestBlock<T> {
    private byte[] fileBytes;
    private String path;
    private String fileName;
    private String fileHash;
    private String fileString;
    private String description;
    private Integer securityLevel;

    public FileRequestBlock(String request, QJson qJson) {
        super(SUBSYSTEM_FILE, request, qJson);
        parseQJson(qJson);
    }

    public FileRequestBlock(String request, QJson qJson, byte[] fileBytes) {
        super(SUBSYSTEM_FILE, request, qJson);
        this.fileBytes = fileBytes;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileBytes() {
        return this.fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public String getFileString() {
        return this.fileString;
    }

    public void setFileString(String fileString) {
        this.fileString = fileString;
    }

    public String getFileHash() {
        return this.fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
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

    @Override
    public String getS() {
        return Constants.SUBSYSTEM_FILE;
    }

    @Override
    public String getR() {
        return Constants.REQUEST_FILE_UPLOAD;
    }

    protected QJson getParameters() {
        return this.json;
    }

    protected void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        setParameters(qJson.getItemQJson(Constants.PARAMETERS));
        QJson fileData = getParameters();
        try {
            setFileBytes(decodeString(fileData.getItemString("file")));
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        setPath(Json.tryGetQJsonParam(fileData, "path"));
        setFileString(fileData.getItemString("file"));
        setFileName(fileData.getItemString("name"));
        setFileHash(fileData.getItemString("hash"));
        setDescription(fileData.getItemString("description"));
        try {
            setSecurityLevel(fileData.getItemLong("securityLevel").intValue());
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
    }

    protected byte[] decodeString(String str) {
        return Base64.getDecoder().decode(str);
    }

    private void setParameters(QJson qJson) {
        this.json = qJson;
    }
}
