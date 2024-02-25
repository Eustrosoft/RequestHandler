/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.tis.dto;

import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.core.json.QJson;
import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonParsable;

import java.util.Objects;

public final class TISDto implements JsonParsable<TISDto> {
    private Long ZOID;
    private Long ZVER;
    private Long ZSID;
    private Short ZLVL;

    public Long getZOID() {
        return ZOID;
    }

    public void setZOID(Long ZOID) {
        this.ZOID = ZOID;
    }

    public Long getZVER() {
        return ZVER;
    }

    public void setZVER(Long ZVER) {
        this.ZVER = ZVER;
    }

    public Long getZSID() {
        return ZSID;
    }

    public void setZSID(Long ZSID) {
        this.ZSID = ZSID;
    }

    public Short getZLVL() {
        return ZLVL;
    }

    public void setZLVL(Short ZLVL) {
        this.ZLVL = ZLVL;
    }

    @Override
    public TISDto convertToObject(QJson qJson) throws JsonException {
        setZOID(qJson.getItemLong(DBConstants.ZOID));
        setZVER(qJson.getItemLong(DBConstants.ZVER));
        setZSID(qJson.getItemLong(DBConstants.ZSID));
        setZLVL(qJson.getItemLong(DBConstants.ZLVL) == null ?
                null : Objects.requireNonNull(qJson.getItemLong(DBConstants.ZLVL)).shortValue()
        );
        return null;
    }
}
