package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.tools.QJson;

import java.util.Base64;

public final class FileRequestBlock implements RequestBlock {
    private byte[] fileBytes;
    private String fileName;


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

    @Override
    public String getSubsystem() {
        return "file";
    }

    @Override
    public String getRequest() {
        return "upload"; // TODO make non static value
    }

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        QJson fileData = qJson.getItemQJson("data");
        setFileBytes(decodeString(fileData.getItemString("file")));
        setFileName(fileData.getItemString("name"));
    }

    private byte[] decodeString(String str) {
        return Base64.getDecoder().decode(str);
    }
}
