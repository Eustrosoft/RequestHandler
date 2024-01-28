/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms.dto;

import org.eustrosoft.cms.CMSType;
import org.eustrosoft.core.model.DBObject;

import java.util.Date;
import java.util.List;

public class CMSGeneralObject extends DBObject implements CMSObject {
    private String extension;
    private String fileName;
    private String fullPath;
    private List<String> links;
    private Long space;
    private Integer securityLevel;
    private String hash;
    private CMSType type;
    private String description;
    private Date modified;

    public CMSGeneralObject(String extension, String fileName, String fullPath, List<String> links,
                            Long space, Integer securityLevel, String hash, CMSType type, String description) {
        super();
        this.extension = extension;
        this.fileName = fileName;
        this.fullPath = fullPath;
        this.links = links;
        this.space = space;
        this.securityLevel = securityLevel;
        this.hash = hash;
        this.type = type;
        this.description = description;
    }

    public CMSGeneralObject() {
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    @Override
    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    @Override
    public Long getSpace() {
        return space;
    }

    public void setSpace(Long space) {
        this.space = space;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public CMSType getType() {
        return type;
    }

    public void setType(CMSType type) {
        this.type = type;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String toJson() {
        return this.toString();
    }
}
