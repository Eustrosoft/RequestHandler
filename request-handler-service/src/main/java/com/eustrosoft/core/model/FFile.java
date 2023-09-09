/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 * <p>
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 * <p>
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 * <p>
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.model;

import com.eustrosoft.cms.dbdatasource.ranges.FileType;
import com.eustrosoft.core.model.interfaces.Updatable;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.eustrosoft.core.constants.DBConstants.DESCRIPTION;
import static com.eustrosoft.core.constants.DBConstants.NAME;

@Getter
@Setter
public class FFile extends DBObject implements Updatable {
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

    public FFile(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    public FFile(Long zoid, Long zver, Long zrid, String fileName, FileType fileType, Character extStore,
                 String mimeType, String description, Long chcnt, String algorithm, String digest,
                 Long bSize, Long tChcnt, String tAlgorithm, String tDigest) {
        super(zoid, zver, zrid);
        this.fileName = fileName;
        this.fileType = fileType;
        this.extStore = extStore;
        this.mimeType = mimeType;
        this.description = description;
        this.chcnt = chcnt;
        this.algorithm = algorithm;
        this.digest = digest;
        this.bSize = bSize;
        this.tChcnt = tChcnt;
        this.tAlgorithm = tAlgorithm;
        this.tDigest = tDigest;
    }

    @Override
    @SneakyThrows
    public void fillFromResultSet(ResultSet resultSet) {
        super.fillFromResultSet(resultSet);
        // TODO: constants
        setFileName(resultSet.getString(NAME));
        setFileType(FileType.fromString(resultSet.getString("type")));
        setExtStore(resultSet.getString("extstore").charAt(0));
        setMimeType(resultSet.getString("mimetype"));
        setDescription(resultSet.getString(DESCRIPTION));
        setChcnt(resultSet.getLong("b_chcnt"));
        setAlgorithm(resultSet.getString("b_algo"));
        setDigest(resultSet.getString("b_digest"));
        setBSize(resultSet.getLong("b_size"));
        setTChcnt(resultSet.getLong("t_chcnt"));
        setTAlgorithm(resultSet.getString("t_algo"));
        setTDigest(resultSet.getString("t_digest"));
    }

    public String toUpdateString() {
        return String.format(
                "%s, %s, %s, '%s', '%s','%s', %s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
                getZoid(),
                getZver(),
                getZrid(),
                fileName,
                fileType.getValue(),
                extStore,
                mimeType,
                description,
                "null",
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


