/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms.dto;

import org.eustrosoft.cms.CMSType;

import java.util.List;

public class CMSFile extends CMSGeneralObject {
    private String extension;
    private String fileName;
    private String fullPath;
    private List<String> links;
    private Long space;
    private String hash;
    private String description;

    public CMSFile(String extension, String fileName, String fullPath, List<String> links, Long space,
                   Integer securityLevel, String hash, CMSType type, String description) {
        super(extension, fileName, fullPath, links, space, securityLevel, hash, type, description);
    }

    public CMSType getType() {
        return CMSType.FILE;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getExtension() {
        return extension;
    }

    @Override
    public void setExtension(String extension) {
        this.extension = extension;
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
    public String getHash() {
        return hash;
    }

    @Override
    public void setHash(String hash) {
        this.hash = hash;
    }
}
