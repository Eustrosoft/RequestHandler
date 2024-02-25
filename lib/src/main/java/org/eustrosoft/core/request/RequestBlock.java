/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.request;

import org.eustrosoft.core.json.QJson;
import org.eustrosoft.core.json.exception.JsonException;

public interface RequestBlock<T> {
    String getS();

    String getR();

    QJson getJson();

    T getData() throws JsonException;
}
