/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.tis.dto;

import org.eustrosoft.core.response.basic.BasicResponseBlock;

import static org.eustrosoft.constants.Constants.SUBSYSTEM_TIS;

public class TISResponseBlock extends BasicResponseBlock {

    public TISResponseBlock(String request) {
        this.s = SUBSYSTEM_TIS;
        this.r = request;
    }
}
