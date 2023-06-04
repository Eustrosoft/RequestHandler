package com.eustrosoft.dbdatasource.core.model;

import com.eustrosoft.dbdatasource.ranges.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.sql.ResultSet;

import static com.eustrosoft.dbdatasource.constants.DBConstants.DESCRIPTION;
import static com.eustrosoft.dbdatasource.constants.DBConstants.NAME;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZOID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZRID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZVER;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FFile {
    private Long zoid;
    private Long zver;
    private Long zrid;
    private String fileName;
    private FileType fileType;
    private Character extStore;
    private String mimeType;
    private String description;
    private Long chcnt;
    private String algorithm;
    private String digest;
    private Long bSize;
    private Long tChcnt;
    private String tAlgorithm;
    private String tDigest;

    @SneakyThrows
    public static FFile fromResultSet(ResultSet resultSet) {
        if (resultSet == null) {
            throw new Exception("Result set is null while processing FFile from ResultSet.");
        }
        if (resultSet.next()) {
            return new FFile(
                    resultSet.getLong(ZOID),
                    resultSet.getLong(ZVER),
                    resultSet.getLong(ZRID),
                    resultSet.getString(NAME),
                    FileType.valueOf(resultSet.getString("type")),
                    resultSet.getString("extstore").charAt(0),
                    resultSet.getString("mimetype"),
                    resultSet.getString(DESCRIPTION),
                    resultSet.getLong("b_chcnt"),
                    resultSet.getString("b_algo"),
                    resultSet.getString("b_digest"),
                    resultSet.getLong("b_size"),
                    resultSet.getLong("t_chcnt"),
                    resultSet.getString("t_algo"),
                    resultSet.getString("t_digest")
            );
        }
        throw new Exception("Exception while processing ResultSet");
    }

    public String toUpdate() {
        return String.format(
                "%s, %s, %s, '%s', '%s','%s', %s, %s, %s, %s, %s, %s, %s, %s, %s",
                zoid,
                zver,
                zrid,
                fileName,
                fileType.getValue(),
                extStore,
                mimeType,
                description,
                chcnt,
                algorithm,
                digest,
                bSize,
                tChcnt,
                tAlgorithm,
                tDigest
        );
    }
}
