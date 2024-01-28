/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.file;

import org.eustrosoft.json.QJson;

import static org.eustrosoft.spec.Constants.REQUEST_CHUNKS_HEX_FILE_UPLOAD;

public final class HexFileRequestBlock extends ChunkFileRequestBlock {
    private String hexString;

    public HexFileRequestBlock(String request, QJson qJson) {
        super(request, qJson);
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
