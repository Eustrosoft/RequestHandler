/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.requests;

import java.util.List;

public class QTisRequestObject extends RequestObject {

    @Override
    public List<RequestBlock> getR() {
        return super.getRequestBlocks();
    }

    @Override
    public Long getT() {
        return super.getTimeout();
    }
}
