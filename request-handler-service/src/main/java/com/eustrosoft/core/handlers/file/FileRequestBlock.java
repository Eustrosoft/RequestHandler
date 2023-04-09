package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.handlers.requests.BasicRequest;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.BasicResponse;
import com.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

import static com.eustrosoft.core.Constants.*;

public class FileRequestBlock extends BasicRequest {
    private byte[] fileBytes;
    private String fileName;
    private String fileString;
    private QJson parameters;

    public FileRequestBlock(HttpServletRequest request,
                            HttpServletResponse response,
                            QJson qJson) {
        super(request, response);
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
        setFileBytes(decodeString(fileData.getItemString("file")));
        setFileString(fileData.getItemString("file"));
        setFileName(fileData.getItemString("name"));
    }

    protected byte[] decodeString(String str) {
        return Base64.getDecoder().decode(str);
    }

    protected QJson getParameters() {
        return this.parameters;
    }
}
