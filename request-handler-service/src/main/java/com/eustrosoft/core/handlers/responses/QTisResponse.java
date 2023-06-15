/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.responses;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class QTisResponse implements Response {
    private Long timeout;
    private List<ResponseBlock> responseBlocks;

    public QTisResponse(List<ResponseBlock> responseBlocks) {
        this.responseBlocks = responseBlocks;
    }

    public QTisResponse() {

    }

    public void setResponseBlocks(List<ResponseBlock> responseBlocks) {
        this.responseBlocks = responseBlocks;
    }

    public void addResponseBlock(ResponseBlock responseBlock) {
        this.responseBlocks.add(responseBlock);
    }
//
//    @Override
//    public long getQTisVer() {
//        return 0;
//    }

    @Override
    public List<ResponseBlock> getR() {
        return this.responseBlocks;
    }

    @Override
    public Long getT() {
        return this.timeout;
    }

    public void setT(Long timeout) {
        this.timeout = timeout;
    }

    @Override
    public String getJson() {
        JsonObject object = new JsonObject();
        object.add("r", getResponsesString());
        object.addProperty("t", String.valueOf(getT()));
        return object.toString();
    }

    private JsonArray getResponsesString() {
        List<String> jsonResponses = new ArrayList<>();
        for (ResponseBlock responseBlock : responseBlocks) {
            try {
                jsonResponses.add(
                        new Gson().toJson(responseBlock.toJsonObject())
                );
            } catch (Exception ex) {
                jsonResponses.add(
                        "{\"m\":\"Exception while processing block\"}"
                );
            }
        }
        return new Gson().fromJson(jsonResponses.toString(), JsonArray.class);
    }

}
