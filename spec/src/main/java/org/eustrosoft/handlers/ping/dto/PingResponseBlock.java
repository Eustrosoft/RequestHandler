/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.ping.dto;

import org.eustrosoft.core.response.ResponseLang;
import org.eustrosoft.core.response.basic.BasicResponseBlock;

import static org.eustrosoft.constants.Constants.REQUEST_PING;
import static org.eustrosoft.constants.Constants.SUBSYSTEM_PING;

public class PingResponseBlock extends BasicResponseBlock<PingDto> {

    public PingResponseBlock() {
        super();
    }

    @Override
    public String getS() {
        return SUBSYSTEM_PING;
    }

    @Override
    public String getR() {
        return REQUEST_PING;
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
