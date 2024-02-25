/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.msg.dto;

import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.core.response.basic.BasicResponseBlock;

import static org.eustrosoft.constants.Constants.SUBSYSTEM_SAM;

public final class MSGResponseBlock<T extends JsonConvertible> extends BasicResponseBlock<T> {
    private T data;

    public MSGResponseBlock(String requestType) {
        this.s = SUBSYSTEM_SAM;
        this.r = requestType;
    }

    @Override
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
