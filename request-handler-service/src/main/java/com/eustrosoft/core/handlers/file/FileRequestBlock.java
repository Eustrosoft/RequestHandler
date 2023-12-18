/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.handlers.requests.BasicRequest;
import com.eustrosoft.core.tools.Json;
import com.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

import static com.eustrosoft.core.constants.Constants.*;

public class FileRequestBlock extends BasicRequest {
    private byte[] fileBytes;
    private String path;
    private String fileName;
    private String fileHash;
    private String fileString;
    private QJson parameters;
    private String description;
    private Integer securityLevel;

    public FileRequestBlock(HttpServletRequest request,
                            HttpServletResponse response,
                            QJson qJson) {
        super(request, response, qJson);
        parseQJson(qJson);
    }

    public FileRequestBlock(HttpServletResponse response,
                            HttpServletRequest request,
                            byte[] fileBytes) {
        super(request, response);
        this.fileBytes = fileBytes;
    }

    public FileRequestBlock(HttpServletRequest request,
                            HttpServletResponse response) {
        this(response, request, new byte[0]);
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
        return SUBSYSTEM_FILE;
    }

    @Override
    public String getR() {
        return REQUEST_FILE_UPLOAD;
    }

    private void setParameters(QJson qJson) {
        this.parameters = qJson;
    }

    protected void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        setParameters(qJson.getItemQJson(PARAMETERS));
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

    protected QJson getParameters() {
        return this.parameters;
    }
}
