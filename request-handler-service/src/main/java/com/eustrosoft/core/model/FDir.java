/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.model;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.eustrosoft.cms.dbdatasource.constants.DBConstants.*;

@Getter
@Setter
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

    @SneakyThrows
    public void fillFromResultSet(ResultSet resultSet) {
        if (resultSet == null) {
            throw new Exception("Result set is null while processing FDir from ResultSet.");
        }
        if (resultSet.next()) {
            setZoid(resultSet.getLong(ZOID));
            setZver(resultSet.getLong(ZVER));
            setZrid(resultSet.getLong(ZRID));
            setFileId(resultSet.getLong(FILE_ID));
            setFileName(resultSet.getString(F_NAME));
            setMimeType(resultSet.getString(MIME_TYPE));
            setDescription(resultSet.getString(DESCRIPTION));
        }
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
}

