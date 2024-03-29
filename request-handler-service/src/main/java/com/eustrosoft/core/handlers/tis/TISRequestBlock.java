/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.tis;

import com.eustrosoft.core.handlers.requests.BasicRequest;
import com.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_SQL;
import static com.eustrosoft.core.constants.DBConstants.*;

public final class TISRequestBlock extends BasicRequest {
    private Long zoid;
    private Long zver;
    private Integer zsid;
    private Short zlvl;

    public TISRequestBlock(HttpServletRequest request,
                           HttpServletResponse response,
                           QJson qJson) {
        super(request, response, qJson);
        parseQJson(qJson);
    }

    private static Long getLongOrNull(QJson json, String name) {
        try {
            return json.getItemLong(name);
        } catch (Exception ex) {
            return null;
        }
    }

    private static Integer getIntOrNull(QJson json, String name) {
        try {
            String item = json.getItemString(name);
            return Integer.parseInt(item);
        } catch (Exception ex) {
            return null;
        }
    }

    private static Short getShortOrNull(QJson json, String name) {
        try {
            String str = json.getItemString(name);
            return Short.parseShort(str);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String getS() {
        return SUBSYSTEM_SQL;
    }

    @Override
    public String getR() {
        return requestType;
    }

    public Integer getZsid() {
        return zsid;
    }

    public void setZsid(Integer zsid) {
        this.zsid = zsid;
    }

    public Long getZoid() {
        return zoid;
    }

    public void setZoid(Long zoid) {
        this.zoid = zoid;
    }

    public Long getZver() {
        return this.zver;
    }

    public void setZver(Long zver) {
        this.zver = zver;
    }

    public Short getZlvl() {
        return zlvl;
    }

    public void setZlvl(Short zlvl) {
        this.zlvl = zlvl;
    }

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        setZoid(getLongOrNull(qJson, ZOID));
        setZver(getLongOrNull(qJson, ZVER));
        setZsid(getIntOrNull(qJson, ZSID));
        setZlvl(getShortOrNull(qJson, ZLVL));
    }
}
