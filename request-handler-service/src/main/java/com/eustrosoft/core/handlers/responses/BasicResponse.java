/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.responses;

public abstract class BasicResponse implements ResponseBlock {
    protected final String s;
    protected String m = "";
    protected Short e = 0;
    protected String r;

    public BasicResponse(String subsystem) {
        this.s = subsystem;
    }

    public BasicResponse(String s, String r) {
        this.s = s;
        this.r = r;
    }

    public BasicResponse(String s, String m, Short e, String r) {
        this.s = s;
        this.m = m;
        this.e = e;
        this.r = r;
    }

    @Override
    public String getR() {
        return this.r;
    }

    @Override
    public String getM() {
        return this.m;
    }

    @Override
    public String getL() {
        return ResponseLang.en_US;
    }

    @Override
    public Short getE() {
        return e;
    }

    @Override
    public String getS() {
        return this.s;
    }

    public void setE(int code) {
        this.e = (short) code;
    }

    public void setM(String m) {
        this.m = m;
    }

    public void setR(String r) {
        this.r = r;
    }
}
