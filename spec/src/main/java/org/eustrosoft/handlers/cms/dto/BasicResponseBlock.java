/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.cms.dto;

public abstract class BasicResponseBlock<T> implements ResponseBlock<T> {
    protected String s;
    protected String r;
    protected String m;
    protected String l;
    protected Short e;

    public BasicResponseBlock(ResponseParams params) {
        this.s = params.getSubsystem();
        this.r = params.getRequest();
        this.m = params.getMessage();
        this.e = params.getError();
        this.l = params.getLang();
    }

    @Override
    public String getS() {
        return this.s;
    }

    @Override
    public String getR() {
        return this.r;
    }

    @Override
    public Short getE() {
        return this.e;
    }

    @Override
    public String getM() {
        return this.m;
    }

    @Override
    public String getL() {
        return this.l;
    }
}
