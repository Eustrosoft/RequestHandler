/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.response;

public enum ResponseLang {
    EN_US("en-US");

    private String lang;

    ResponseLang(String lang) {
        this.lang = lang;
    }

    public String getLang() {
        return this.lang;
    }
}
