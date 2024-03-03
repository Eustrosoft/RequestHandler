/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.cms.dto;

import com.eustrosoft.cms.CMSType;

import java.util.List;

public class CMSDirectory extends CMSGeneralObject {
    private String fileName;
    private String fullPath;
    private List<String> links;
    private Long space;
    private String description;

    public CMSDirectory(String extension, String fileName, String fullPath, List<String> links, Long space, Integer securityLevel, String hash, CMSType type, String description) {
        super(extension, fileName, fullPath, links, space, securityLevel, hash, type, description);
    }

    public CMSType getType() {
        return CMSType.DIRECTORY;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFullPath() {
        return fullPath;
    }

    @Override
    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    @Override
    public List<String> getLinks() {
        return links;
    }

    @Override
    public void setLinks(List<String> links) {
        this.links = links;
    }

    @Override
    public Long getSpace() {
        return space;
    }

    @Override
    public void setSpace(Long space) {
        this.space = space;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
