/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.spec.interfaces;

public interface RequestBlock<T> extends JsonParsable<T> {
    String getS();

    String getR();

    T getData();
}
