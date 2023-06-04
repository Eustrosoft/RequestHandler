package com.eustrosoft.dbdatasource.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.sql.ResultSet;

import static com.eustrosoft.dbdatasource.constants.DBConstants.DESCRIPTION;
import static com.eustrosoft.dbdatasource.constants.DBConstants.FILE_ID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.F_NAME;
import static com.eustrosoft.dbdatasource.constants.DBConstants.MIME_TYPE;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZOID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZRID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZVER;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FDir {
    private Long zoid;
    private Long zver;
    private Long zrid;
    private Long fileId;
    private String fileName;
    private String mimeType;
    private String description;

    @SneakyThrows
    public static FDir fromResultSet(ResultSet resultSet) {
        if (resultSet == null) {
            throw new Exception("Result set is null while processing FDir from ResultSet.");
        }
        if (resultSet.next()) {
            return new FDir(
                    resultSet.getLong(ZOID),
                    resultSet.getLong(ZVER),
                    resultSet.getLong(ZRID),
                    resultSet.getLong(FILE_ID),
                    resultSet.getString(F_NAME),
                    resultSet.getString(MIME_TYPE),
                    resultSet.getString(DESCRIPTION)
            );
        }
        throw new Exception("Exception while processing ResultSet");
    }

    public String toUpdate() {
        return String.format(
                "%s, %s, %s, %s, '%s','%s', '%s'",
                zoid,
                zver,
                zrid,
                fileId,
                fileName,
                mimeType,
                description
        );
    }
}

