/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.handlers.cms;

import org.eustrosoft.cms.CMSType;
import org.eustrosoft.core.constants.Constants;
import org.eustrosoft.core.handlers.requests.BasicRequest;
import org.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CMSRequestBlock extends BasicRequest {
    private CMSType type;
    private String path;
    private String fileName;
    private String from;
    private String to;
    private String ticket;
    private String contentType;
    private String description;
    private Integer securityLevel;

    public CMSRequestBlock(HttpServletRequest request,
                           HttpServletResponse response,
                           QJson qJson) {
        super(request, response, qJson);
        parseQJson(qJson);
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getS() {
        return Constants.SUBSYSTEM_CMS;
    }

    @Override
    public String getR() {
        return this.requestType;
    }

    @Override
    public HttpServletRequest getHttpRequest() {
        return this.request;
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
        setDescription(qJson.getItemString("description"));
        try {
            setSecurityLevel(qJson.getItemLong("securityLevel").intValue());
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
    }
}
