/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.cms.dto;

import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.core.response.basic.BasicResponseBlock;

import static org.eustrosoft.constants.Constants.SUBSYSTEM_CMS;

public final class CMSResponseBlock<T extends JsonConvertible> extends BasicResponseBlock<T> {

    public CMSResponseBlock(String response) {
        this.s = SUBSYSTEM_CMS;
        this.r = response;
    }

}
