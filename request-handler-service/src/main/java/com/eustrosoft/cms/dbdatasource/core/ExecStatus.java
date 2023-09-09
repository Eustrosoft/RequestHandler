/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.cms.dbdatasource.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
