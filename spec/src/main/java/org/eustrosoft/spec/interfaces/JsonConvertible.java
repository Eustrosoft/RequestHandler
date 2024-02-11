/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.spec.interfaces;

import org.eustrosoft.json.exception.JsonException;

public interface JsonConvertible {
    String convertToString() throws JsonException;
}
