/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.model;

import com.eustrosoft.core.model.interfaces.Updatable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.eustrosoft.core.constants.DBConstants.DESCRIPTION;
import static com.eustrosoft.core.constants.DBConstants.FILE_ID;
import static com.eustrosoft.core.constants.DBConstants.F_NAME;
import static com.eustrosoft.core.constants.DBConstants.MIME_TYPE;
import static com.eustrosoft.core.db.util.DBUtils.setLongOrNull;
import static com.eustrosoft.core.db.util.DBUtils.setStringOrNull;

public class FDir extends DBObject implements Updatable {
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
        setFileId(resultSet.getLong(FILE_ID));
        setFileName(resultSet.getString(F_NAME));
        setMimeType(resultSet.getString(MIME_TYPE));
        setDescription(resultSet.getString(DESCRIPTION));
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

    public PreparedStatement toUpdatePrepareStatement(Connection connection) throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT FS.update_FDir(?, ?, ?, ?, ?, ?, ?)"
        );
        setLongOrNull(statement, 1, getZoid());
        setLongOrNull(statement, 2, getZver());
        setLongOrNull(statement, 3, getZrid());
        setLongOrNull(statement, 4, getFileId());
        setStringOrNull(statement, 5, getFileName());
        setStringOrNull(statement, 6, getMimeType());
        setStringOrNull(statement, 7, getDescription());
        return statement;
    }
}

