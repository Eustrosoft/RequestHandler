/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handler;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.List;

public interface Request {
    List<RequestBlock> getR();

    Long getT();

    ServletRequest getHttpRequest();

    ServletResponse getHttpResponse();
}
