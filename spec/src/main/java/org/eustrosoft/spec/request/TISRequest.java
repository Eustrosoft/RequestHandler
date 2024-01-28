package org.eustrosoft.spec.request;

import org.eustrosoft.json.QJson;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.Constants;
import org.eustrosoft.spec.interfaces.Request;
import org.eustrosoft.spec.interfaces.RequestBlock;

import java.util.ArrayList;
import java.util.List;

import static org.eustrosoft.spec.Constants.REQUEST;
import static org.eustrosoft.spec.Constants.SUBSYSTEM;

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
            String requestType = "";
            try {
                requestType = qJson.getItemString(REQUEST);
            } catch (Exception ex) {
                System.err.println("Can not get Request type. " + ex.getMessage());
            }
            RequestBlock<?> requestBlock = new TISRequestBlock(subsystem, requestType, qJson);
            if (requestBlock != null) {
                requestBlocks.add(requestBlock);
            }
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
