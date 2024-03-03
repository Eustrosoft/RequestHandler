/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.responses;

import com.eustrosoft.core.tools.json.JsonParser;

import java.util.List;

public class QTisResponse implements Response {
    private Long t;
    private List<ResponseBlock> r;

    public QTisResponse(List<ResponseBlock> responseBlocks) {
        this.r = responseBlocks;
    }

    public QTisResponse() {

    }

    public void setR(List<ResponseBlock> r) {
        this.r = r;
    }

    public void addResponseBlock(ResponseBlock r) {
        this.r.add(r);
    }

    @Override
    public List<ResponseBlock> getR() {
        return this.r;
    }

    @Override
    public Long getT() {
        return this.t;
    }

    public void setT(Long timeout) {
        this.t = timeout;
    }

    @Override
    public String getJson() {
        try {
            return new JsonParser().parseObject(this);
//            JsonObject object = new JsonObject();
//            object.add("r", getResponsesString());
//            object.addProperty("t", String.valueOf(getT()));
//            return object.toString();
        } catch (Exception exception) {
            return null;
        }
    }

//    private String getResponsesString() throws Exception {
////        return new JsonParser().parseCollection(responseBlocks);
////        List<String> jsonResponses = new ArrayList<>();
////        for (ResponseBlock responseBlock : r) {
////            try {
////                jsonResponses.add(
////                        new Gson().toJson(responseBlock.toJsonObject())
////                );
////            } catch (Exception ex) {
////                jsonResponses.add(
////                        "{\"m\":\"Exception while processing block\"}"
////                );
////            }
////        }
////        return new Gson().fromJson(jsonResponses.toString(), JsonArray.class);
//    }

}
