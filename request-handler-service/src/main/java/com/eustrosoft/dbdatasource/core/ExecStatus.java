package com.eustrosoft.dbdatasource.core;

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
    private final static String F_ZID = "ZID";
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
        setZoid(set.getLong(F_ZID));
        setZver(set.getLong(F_ZVER));
        setErrnum(set.getInt(F_ERRNUM));
        setErrcode(set.getString(F_ERRCODE));
        setErrdesc(set.getString(F_ERRDESC));
        setP1(set.getString(F_P1));
        setP2(set.getString(F_P2));
        setP3(set.getString(F_P3));
    }

    public boolean isOk() {
        return this.errnum != null && this.errnum == 0;
    }

    public String getCaption() {
        return String.format("Error code: %s: %s", this.errcode, this.errdesc);
    }
}
