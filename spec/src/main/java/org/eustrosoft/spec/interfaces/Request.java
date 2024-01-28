/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.spec.interfaces;

import org.eustrosoft.json.QJson;
import org.eustrosoft.json.exception.JsonException;

import java.util.List;

public interface Request {

    List<RequestBlock<?>> getR();

    Long getT();

    void fromJson(QJson qJson) throws JsonException;
}
