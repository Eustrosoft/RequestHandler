/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.context;

public interface EustrosoftUser {
    String getIp();

    String getUserName();

    String getPassword();
}
