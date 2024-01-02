/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.handlers.file;

import org.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.eustrosoft.core.constants.Constants.REQUEST_CHUNKS_HEX_FILE_UPLOAD;

public final class HexFileRequestBlock extends ChunkFileRequestBlock {
    private String hexString;

    public HexFileRequestBlock(HttpServletRequest request,
                               HttpServletResponse response,
                               QJson qJson) {
        super(request, response, qJson);
        parseQJson(qJson);
    }

    @Override
    protected void parseQJson(QJson qJson) throws NullPointerException {
        super.parseQJson(qJson);
        QJson parameters = getParameters();
        setHexString(parameters.getItemString("hexString"));
    }

    public String getHexString() {
        return hexString;
    }

    public void setHexString(String hexString) {
        this.hexString = hexString;
    }

    @Override
    public String getR() {
        return REQUEST_CHUNKS_HEX_FILE_UPLOAD;
    }
}
