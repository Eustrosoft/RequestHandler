package com.eustrosoft.core.handlers.cms;

import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.tools.QJson;
import com.eustrosoft.datasource.sources.model.CMSType;

import javax.servlet.http.HttpServletRequest;

import static com.eustrosoft.core.Constants.*;

public class CMSRequestBlock implements RequestBlock {
    private final HttpServletRequest request;
    private CMSType type;
    private String requestType;
    private String path;
    private String fileName;
    private String from;
    private String to;

    public CMSRequestBlock(HttpServletRequest request, QJson qJson) {
        this.request = request;
        parseQJson(qJson);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public CMSType getType() {
        return type;
    }

    public void setType(CMSType type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String getS() {
        return SUBSYSTEM_CMS;
    }

    @Override
    public String getR() {
        return this.requestType;
    }

    @Override
    public HttpServletRequest getHttpRequest() {
        return this.request;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        setFileName(qJson.getItemString("fileName"));
        if (CMSType.FILE.isInRange(qJson.getItemString("type"))) {
            setType(CMSType.valueOf(qJson.getItemString("type")));
        }
        setFrom(qJson.getItemString("from"));
        setTo(qJson.getItemString("to"));
        setPath(qJson.getItemString("path"));
    }
}
