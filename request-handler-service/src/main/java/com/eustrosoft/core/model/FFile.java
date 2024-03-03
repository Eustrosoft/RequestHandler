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

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.eustrosoft.core.constants.DBConstants.DESCRIPTION;
import static com.eustrosoft.core.constants.DBConstants.NAME;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public Character getExtStore() {
        return extStore;
    }

    public void setExtStore(Character extStore) {
        this.extStore = extStore;
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

    public Long getChcnt() {
        return chcnt;
    }

    public void setChcnt(Long chcnt) {
        this.chcnt = chcnt;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Long getBSize() {
        return bSize;
    }

    public void setBSize(Long bSize) {
        this.bSize = bSize;
    }

    public Long getTChcnt() {
        return tChcnt;
    }

    public void setTChcnt(Long tChcnt) {
        this.tChcnt = tChcnt;
    }

    public String getTAlgorithm() {
        return tAlgorithm;
    }

    public void setTAlgorithm(String tAlgorithm) {
        this.tAlgorithm = tAlgorithm;
    }

    public String getTDigest() {
        return tDigest;
    }

    public void setTDigest(String tDigest) {
        this.tDigest = tDigest;
    }

    @Override
    public void fillFromResultSet(ResultSet resultSet) throws SQLException {
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


