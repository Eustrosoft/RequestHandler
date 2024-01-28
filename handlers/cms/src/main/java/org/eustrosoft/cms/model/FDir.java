/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms.model;

import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.core.model.DBObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FDir extends DBObject {
    private Long fileId;
    private String fileName;
    private String mimeType;
    private String description;

    public FDir(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    public FDir(Long zoid, Long zver, Long zrid, Long fileId,
                String fileName, String mimeType, String description) {
        super(zoid, zver, zrid);
        this.fileId = fileId;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.description = description;
    }

    @Override
    public void fillFromResultSet(ResultSet resultSet) throws SQLException {
        super.fillFromResultSet(resultSet);
        setFileId(resultSet.getLong(DBConstants.FILE_ID));
        setFileName(resultSet.getString(DBConstants.F_NAME));
        setMimeType(resultSet.getString(DBConstants.MIME_TYPE));
        setDescription(resultSet.getString(DBConstants.DESCRIPTION));
    }

    public String toUpdateString() {
        return String.format(
                "%s, %s, %s, %s, '%s','%s', '%s'",
                getZoid(),
                getZver(),
                getZrid(),
                fileId,
                fileName,
                mimeType,
                description
        );
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

