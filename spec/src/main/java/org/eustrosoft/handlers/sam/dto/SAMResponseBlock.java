/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.sam.dto;

import org.eustrosoft.spec.interfaces.JsonConvertible;
import org.eustrosoft.spec.response.BasicResponseBlock;

public final class SAMResponseBlock<T extends JsonConvertible> extends BasicResponseBlock<T> {
    private T data;

    public SAMResponseBlock(String requestType) {
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
