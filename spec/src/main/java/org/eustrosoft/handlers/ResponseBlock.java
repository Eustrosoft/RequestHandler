/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.handlers;

public interface ResponseBlock {
    String getS();

    String getR();

    Short getE();

    String getM();

    String getL();

    Object getData();
}
