/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ExecStatus {
    private final static String F_ZOID = "ZOID";
    private final static String F_ZVER = "ZVER";
    private final static String F_ERRNUM = "errnum";
    private final static String F_ERRCODE = "errcode";
    private final static String F_ERRDESC = "errdesc";

    private final static String F_P1 = "p1";
    private final static String F_P2 = "p2";
    private final static String F_P3 = "p3";

    private Long zoid;
    private Long zver;
    private Integer errnum;
    private String errcode;
    private String errdesc;
    private String p1;
    private String p2;
    private String p3;

    public void fillFromResultSet(ResultSet set) throws SQLException {
        set.next();
        String answer = set.getString(1);
        String[] params = parseAnswer(answer);
        if (notEmpty(params[0])) {
            setZoid(Long.parseLong(params[0]));
        }
        if (notEmpty(params[1])) {
            setZver(Long.parseLong(params[1]));
        }
        if (notEmpty(params[2])) {
            setErrnum(Integer.parseInt(params[2]));
        }
        setErrcode(params[3]);
        setErrdesc(params[4]);
        try {
            setP1(params[5]);
            setP2(params[6]);
            setP3(params[7]);
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println("No more properties found for query.");
        }
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }

    public void setP3(String p3) {
        this.p3 = p3;
    }

    public Long getZoid() {
        return zoid;
    }

    public void setZoid(Long zoid) {
        this.zoid = zoid;
    }

    public Long getZver() {
        return zver;
    }

    public void setZver(Long zver) {
        this.zver = zver;
    }

    public Integer getErrnum() {
        return errnum;
    }

    public void setErrnum(Integer errnum) {
        this.errnum = errnum;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrdesc() {
        return errdesc;
    }

    public void setErrdesc(String errdesc) {
        this.errdesc = errdesc;
    }

    public boolean isOk() {
        return this.errnum != null && this.errnum == 0;
    }

    public String getCaption() {
        return String.format("Error code: %s: %s", this.errcode, this.errdesc);
    }

    private String[] parseAnswer(String answer) {
        return answer.substring(1, answer.length() - 1).split(",");
    }

    private boolean notEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
