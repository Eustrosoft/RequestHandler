package org.eustrosoft.core.request;

import org.eustrosoft.constants.Constants;
import org.eustrosoft.core.json.QJson;
import org.eustrosoft.core.json.exception.JsonException;

import java.util.ArrayList;
import java.util.List;

import static org.eustrosoft.constants.Constants.REQUEST;
import static org.eustrosoft.constants.Constants.SUBSYSTEM;

public class TISRequest implements Request {
    private List<RequestBlock<?>> requestBlocks;
    private Long timeout;

    @Override
    public List<RequestBlock<?>> getR() {
        return this.requestBlocks;
    }

    @Override
    public Long getT() {
        return this.timeout;
    }

    @Override
    public void fromJson(QJson qJson) throws JsonException {
        try {
            setTimeout((Long) qJson.getItem(Constants.TIMEOUT));
            setRequestBlocks(getRequestBlocks(qJson.getItemQJson(Constants.REQUESTS)));
        } catch (Exception exception) {
            throw new JsonException("Error while parsing json");
        }
    }

    private List<RequestBlock<?>> getRequestBlocks(QJson requests) {
        List<RequestBlock<?>> requestBlocks = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            QJson qJson = requests.getItemQJson(i);
            String subsystem = qJson.getItemString(SUBSYSTEM);
            String requestType = qJson.getItemString(REQUEST);
            requestBlocks.add(new BasicRequestBlock<>(subsystem, requestType, qJson));
        }
        return requestBlocks;
    }

    public void setRequestBlocks(List<RequestBlock<?>> requestBlocks) {
        this.requestBlocks = requestBlocks;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
}
