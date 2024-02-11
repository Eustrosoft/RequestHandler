/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers.login.dto;

import org.eustrosoft.json.QJson;
import org.eustrosoft.spec.interfaces.JsonParsable;
import org.eustrosoft.spec.request.BasicRequestBlock;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_LOGIN;

public class LoginRequestBlock<T extends JsonParsable<T>> extends BasicRequestBlock<T> {
    public LoginRequestBlock(String request, QJson qJson) {
        super(SUBSYSTEM_LOGIN, request, qJson);
    }
}
