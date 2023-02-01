package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.tools.QJson;

import java.util.Base64;

import static com.eustrosoft.core.Constants.REQUEST_FILE_UPLOAD;
import static com.eustrosoft.core.Constants.SUBSYSTEM_FILE;

public class FileRequestBlock implements RequestBlock {
    private byte[] fileBytes;
    private String fileName;
    private String fileString;


    public FileRequestBlock(QJson qJson) {
        parseQJson(qJson);
    }

    public FileRequestBlock(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public FileRequestBlock() {
        this(new byte[0]);
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

    @Override
    public String getSubsystem() {
        return SUBSYSTEM_FILE;
    }

    @Override
    public String getRequest() {
        return REQUEST_FILE_UPLOAD;
    }

    protected void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        QJson fileData = qJson.getItemQJson("data");
        setFileBytes(decodeString(fileData.getItemString("file")));
        setFileString(fileData.getItemString("file"));
        setFileName(fileData.getItemString("name"));
    }

    protected byte[] decodeString(String str) {
        return Base64.getDecoder().decode(str);
    }
}
