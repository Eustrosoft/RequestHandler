package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.eustrosoft.core.Constants.REQUEST_CHUNKS_HEX_FILE_UPLOAD;

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
