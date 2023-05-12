package com.eustrosoft.core.handlers.cms;

import com.eustrosoft.core.handlers.requests.BasicRequest;
import com.eustrosoft.core.tools.QJson;
import com.eustrosoft.datasource.sources.model.CMSType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eustrosoft.core.Constants.SUBSYSTEM_CMS;

public class CMSRequestBlock extends BasicRequest {
    private CMSType type;
    private String requestType;
    private String path;
    private String fileName;
    private String from;
    private String to;
    private String ticket;
    private String contentType;

    public CMSRequestBlock(HttpServletRequest request,
                           HttpServletResponse response,
                           QJson qJson) {
        super(request, response);
        parseQJson(qJson);
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
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

    public String getRequestType() {
        return requestType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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
        setTicket(qJson.getItemString("ticket"));
        setContentType(qJson.getItemString("contentType"));
    }
}
