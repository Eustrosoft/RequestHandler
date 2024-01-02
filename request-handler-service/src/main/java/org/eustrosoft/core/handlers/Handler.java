/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.handlers;

import org.eustrosoft.core.handlers.requests.RequestBlock;
import org.eustrosoft.core.handlers.responses.ResponseBlock;

public interface Handler {
    ResponseBlock processRequest(RequestBlock requestBlock) throws Exception;
}
