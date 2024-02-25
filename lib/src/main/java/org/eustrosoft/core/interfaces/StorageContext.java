/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.interfaces;

public interface StorageContext {
    void clearCurrentStorage();

    String getStoragePath();

    String createAndGetNewStoragePath();
}
