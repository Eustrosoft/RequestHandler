/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.response.basic;

import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.core.response.ResponseBlock;
import org.eustrosoft.core.response.ResponseParams;

public abstract class BasicResponseBlock<T extends JsonConvertible> implements ResponseBlock<T> {
    protected String s;
    protected String r;
    protected String m;
    protected String l;
    protected Long e;
    protected T data;

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

    public void setR(String r) {
        this.r = r;
    }

    public void setM(String m) {
        this.m = m;
    }

    public void setL(String l) {
        this.l = l;
    }

    public void setE(Long e) {
        this.e = e;
    }


    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
