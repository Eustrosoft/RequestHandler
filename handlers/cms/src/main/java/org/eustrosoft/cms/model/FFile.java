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

package org.eustrosoft.cms.model;

import org.eustrosoft.cms.dbdatasource.ranges.FileType;
import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.core.model.DBObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FFile extends DBObject {
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
    public void fillFromResultSet(ResultSet resultSet) throws SQLException {
        super.fillFromResultSet(resultSet);
        // TODO: constants
        setFileName(resultSet.getString(DBConstants.NAME));
        setFileType(FileType.fromString(resultSet.getString("type")));
        setExtStore(resultSet.getString("extstore").charAt(0));
        setMimeType(resultSet.getString("mimetype"));
        setDescription(resultSet.getString(DBConstants.DESCRIPTION));
        setChcnt(resultSet.getLong("b_chcnt"));
        setAlgorithm(resultSet.getString("b_algo"));
        setDigest(resultSet.getString("b_digest"));
        setbSize(resultSet.getLong("b_size"));
        settChcnt(resultSet.getLong("t_chcnt"));
        settAlgorithm(resultSet.getString("t_algo"));
        settDigest(resultSet.getString("t_digest"));
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

    public Long getbSize() {
        return bSize;
    }

    public void setbSize(Long bSize) {
        this.bSize = bSize;
    }

    public Long gettChcnt() {
        return tChcnt;
    }

    public void settChcnt(Long tChcnt) {
        this.tChcnt = tChcnt;
    }

    public String gettAlgorithm() {
        return tAlgorithm;
    }

    public void settAlgorithm(String tAlgorithm) {
        this.tAlgorithm = tAlgorithm;
    }

    public String gettDigest() {
        return tDigest;
    }

    public void settDigest(String tDigest) {
        this.tDigest = tDigest;
    }
}


