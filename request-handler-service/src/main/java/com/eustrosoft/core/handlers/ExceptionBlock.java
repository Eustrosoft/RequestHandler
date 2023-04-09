package com.eustrosoft.core.handlers;

import com.eustrosoft.core.handlers.responses.BasicResponse;

public class ExceptionBlock extends BasicResponse {
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

    @Override
    public String getS() {
        return this.subsystem;
    }

    @Override
    public String getR() {
        return this.response;
    }

    @Override
    public Short getE() {
        return this.errorCode;
    }

    @Override
    public String getM() {
        return this.message;
    }

    @Override
    public String getL() {
        return this.lang;
    }
}
