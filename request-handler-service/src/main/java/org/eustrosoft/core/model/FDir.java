/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.model;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.eustrosoft.core.constants.DBConstants;
import org.eustrosoft.core.model.interfaces.Updatable;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    @Override
    @SneakyThrows
    public void fillFromResultSet(ResultSet resultSet) {
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
}

