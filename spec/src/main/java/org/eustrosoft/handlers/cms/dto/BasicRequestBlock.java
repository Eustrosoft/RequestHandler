/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.cms.dto;

public abstract class BasicRequestBlock<T> implements RequestBlock<T> {
    protected final T data;
    protected final String s;
    protected final String r;

    public BasicRequestBlock(String subsystem, String reqeust, T data) {
        this.s = subsystem;
        this.r = reqeust;
        this.data = data;
    }

    @Override
    public String getS() {
        return s;
    }

    @Override
    public String getR() {
        return r;
    }

}
