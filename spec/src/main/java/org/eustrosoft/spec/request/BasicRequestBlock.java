/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.spec.request;

import org.eustrosoft.json.QJson;
import org.eustrosoft.spec.interfaces.JsonData;
import org.eustrosoft.spec.interfaces.RequestBlock;

public abstract class BasicRequestBlock<T extends JsonData> implements RequestBlock<T> {
    protected final String s;
    protected final String r;
    protected QJson json;

    public BasicRequestBlock(String subsystem, String request, QJson json) {
        this.s = subsystem;
        this.r = request;
        this.json = json;
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
