/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.ping.dto;

import org.eustrosoft.spec.ResponseLang;
import org.eustrosoft.spec.interfaces.JsonData;
import org.eustrosoft.spec.response.BasicResponseBlock;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_PING;

public class PingResponseBlock<T extends JsonData> extends BasicResponseBlock<T> {

    public PingResponseBlock(String request) {
        super();
        this.r = request;
    }

    @Override
    public String getS() {
        return SUBSYSTEM_PING;
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
        return ResponseLang.EN_US.getLang();
    }
}
