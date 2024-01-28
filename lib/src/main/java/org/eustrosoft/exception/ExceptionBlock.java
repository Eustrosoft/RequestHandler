/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.exception;

public class ExceptionBlock {
    private String message;
    private Short errorCode;
    private String lang;

    private String subsystem;
    private String response;

    public ExceptionBlock(String message, Short errorCode, String lang,
                          String subsystem, String response) {
        this.message = message;
        this.errorCode = errorCode;
        this.lang = lang;
        this.subsystem = subsystem;
        this.response = response;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setErrorCode(Short errorCode) {
        this.errorCode = errorCode;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getS() {
        return this.subsystem;
    }

    public String getR() {
        return this.response;
    }

    public Short getE() {
        return this.errorCode;
    }

    public String getM() {
        return this.message;
    }

    public String getL() {
        return this.lang;
    }
}
