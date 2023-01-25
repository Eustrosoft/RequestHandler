package com.eustrosoft.core.handlers.responses;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class QTisResponse implements Response {
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

    @Override
    public long getQTisVer() {
        return 0;
    }

    @Override
    public List<ResponseBlock> getResponses() {
        return this.responseBlocks;
    }

    @Override
    public boolean getQTisEnd() {
        return true;
    }

    @Override
    public String getJson() {
        JsonObject object = new JsonObject();
        object.addProperty("qtisver", String.valueOf(getQTisVer()));
        object.add("responses", getResponsesString());
        object.addProperty("qtisend", String.valueOf(getQTisEnd()));
        return object.toString();
    }

    private JsonArray getResponsesString() {
        List<String> jsonResponses = new ArrayList<>();
        for (ResponseBlock responseBlock : responseBlocks) {
            try {
                jsonResponses.add(responseBlock.toJson());
            } catch (Exception ex) {
                jsonResponses.add(
                        "{\"err_msg\":\"Exception while processing block\"}"
                );
            }
        }
        return new Gson().fromJson(jsonResponses.toString(), JsonArray.class);
    }

}
