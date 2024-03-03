/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.cms.dto;

import com.eustrosoft.cms.CMSType;
import com.eustrosoft.core.model.DBObject;
import com.eustrosoft.core.tools.json.JsonNotNull;

import java.util.Date;
import java.util.List;

@JsonNotNull
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
    private Long fileId;
    private Date modified;

    public CMSGeneralObject(String extension, String fileName, String fullPath, List<String> links,
                            Long space, Integer securityLevel, String hash, CMSType type, String description) {
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

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public void setLinks(List<String> links) {
        this.links = links;
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

    public void setType(CMSType type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getFullPath() {
        return fullPath;
    }

    @Override
    public Long getSpace() {
        return space;
    }

    @Override
    public CMSType getType() {
        return type;
    }

    @Override
    public List<String> getLinks() {
        return links;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public static CMSGeneralObjectBuilder builder() {
        return new CMSGeneralObjectBuilder();
    }

    public static final class CMSGeneralObjectBuilder {
        private String extension;
        private String fileName;
        private String fullPath;
        private List<String> links;
        private Long space;
        private Integer securityLevel;
        private String hash;
        private CMSType type;
        private String description;
        private Long fileId;
        private Date modified;
        private Long zoid;
        private Long zver;
        private Long zrid;
        private Long zsid;
        private Short zlvl;
        private String created;

        private CMSGeneralObjectBuilder() {
        }

        public CMSGeneralObjectBuilder extension(String extension) {
            this.extension = extension;
            return this;
        }

        public CMSGeneralObjectBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public CMSGeneralObjectBuilder fullPath(String fullPath) {
            this.fullPath = fullPath;
            return this;
        }

        public CMSGeneralObjectBuilder links(List<String> links) {
            this.links = links;
            return this;
        }

        public CMSGeneralObjectBuilder space(Long space) {
            this.space = space;
            return this;
        }

        public CMSGeneralObjectBuilder securityLevel(Integer securityLevel) {
            this.securityLevel = securityLevel;
            return this;
        }

        public CMSGeneralObjectBuilder hash(String hash) {
            this.hash = hash;
            return this;
        }

        public CMSGeneralObjectBuilder type(CMSType type) {
            this.type = type;
            return this;
        }

        public CMSGeneralObjectBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CMSGeneralObjectBuilder fileId(Long fileId) {
            this.fileId = fileId;
            return this;
        }

        public CMSGeneralObjectBuilder modified(Date modified) {
            this.modified = modified;
            return this;
        }

        public CMSGeneralObjectBuilder zoid(Long zoid) {
            this.zoid = zoid;
            return this;
        }

        public CMSGeneralObjectBuilder zver(Long zver) {
            this.zver = zver;
            return this;
        }

        public CMSGeneralObjectBuilder zrid(Long zrid) {
            this.zrid = zrid;
            return this;
        }

        public CMSGeneralObjectBuilder zsid(Long zsid) {
            this.zsid = zsid;
            return this;
        }

        public CMSGeneralObjectBuilder zlvl(Short zlvl) {
            this.zlvl = zlvl;
            return this;
        }

        public CMSGeneralObjectBuilder created(String created) {
            this.created = created;
            return this;
        }

        public CMSGeneralObject build() {
            CMSGeneralObject cMSGeneralObject = new CMSGeneralObject(extension, fileName, fullPath, links, space, securityLevel, hash, type, description);
            cMSGeneralObject.setFileId(fileId);
            cMSGeneralObject.setModified(modified);
            cMSGeneralObject.setZoid(zoid);
            cMSGeneralObject.setZver(zver);
            cMSGeneralObject.setZrid(zrid);
            cMSGeneralObject.setZsid(zsid);
            cMSGeneralObject.setZlvl(zlvl);
            cMSGeneralObject.setCreated(created);
            return cMSGeneralObject;
        }
    }
}
