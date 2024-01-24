/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.spec;

public abstract class BasicResponseBlock<T> implements ResponseBlock<T> {
    protected String s;
    protected String r;
    protected String m;
    protected String l;
    protected Long e;

    public BasicResponseBlock() {

    }

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
    public Long getE() {
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
