/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.ping;

import org.eustrosoft.core.annotations.Handler;
import org.eustrosoft.core.interfaces.BasicHandler;
import org.eustrosoft.core.request.RequestBlock;
import org.eustrosoft.core.response.ResponseBlock;

import static org.eustrosoft.constants.Constants.REQUEST_PING;
import static org.eustrosoft.constants.Constants.SUBSYSTEM_PING;

@Handler(SUBSYSTEM_PING)
public class PingHandler implements BasicHandler {

    public PingHandler() {

    }

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        PingService pingService = new PingService(requestBlock);

        switch (requestBlock.getR()) {
            case REQUEST_PING:
                return pingService.getPing();
            default:
                return null;
        }
    }
}
