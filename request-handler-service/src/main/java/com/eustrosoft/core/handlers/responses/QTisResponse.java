package com.eustrosoft.core.handlers.responses;

import com.eustrosoft.core.tools.QJson;

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
    public QJson getJson() {
        QJson qJson = new QJson();
        qJson.addItem("qtisver", String.valueOf(getQTisVer()));
        qJson.addItem("responses", getResponsesString());
        qJson.addItem("qtisend", String.valueOf(getQTisEnd()));
        return qJson;
    }

    private String getResponsesString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (ResponseBlock responseBlock : responseBlocks) {
            try {
                builder.append(responseBlock.toJson().toJSONString());
            } catch (Exception ex) {
                builder.append(
                        "{\"err_msg\":\"Exception while processing block\"}"
                );
            }
        }
        builder.append("]");
        return builder.toString();
    }

}
