package org.eustrosoft.dispatcher;

import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.dispatcher.context.HandlersContext;
import org.eustrosoft.json.QJson;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.ResponseLang;
import org.eustrosoft.spec.ResponseParams;
import org.eustrosoft.spec.interfaces.Request;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.Response;
import org.eustrosoft.spec.interfaces.ResponseBlock;
import org.eustrosoft.spec.request.TISRequest;
import org.eustrosoft.spec.response.ExceptionData;
import org.eustrosoft.spec.response.ExceptionResponseBlock;
import org.eustrosoft.spec.response.TISResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.eustrosoft.spec.Constants.ERR_UNSUPPORTED;

public final class PostRequestProcessor {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HandlersContext handlersContext;

    public PostRequestProcessor(
            HttpServletRequest request,
            HttpServletResponse response,
            HandlersContext context
    ) {
        this.request = request;
        this.response = response;
        this.handlersContext = context;
    }

    public Response processRequest() throws IOException {
        Part jsonPart = null;
        long millis = System.currentTimeMillis();
        try {
            jsonPart = request.getPart("json");
        } catch (Exception ex) {
            System.err.println("Failed parsing request with parts");
        }

        QJson qJson = new QJson();
        if (jsonPart != null) {
            qJson.parseJSONReader(new InputStreamReader(jsonPart.getInputStream()));
        } else {
            qJson.parseJSONReader(request.getReader());
        }

        Request requestObject = new TISRequest();
        try {
            requestObject.fromJson(qJson);
        } catch (JsonException ex) {
        }

        // Processing request blocks
        TISResponse response = new TISResponse();
        List<ResponseBlock> responses = processRequestBlocks(requestObject);
        response.setRequestBlocks(responses);
        response.setTimeout(System.currentTimeMillis() - millis);
        return response;
    }

    private List<ResponseBlock> processRequestBlocks(Request requestObject) {
        Map<String, Class<?>> handlersMap = handlersContext.getHandlersMap();
        List<ResponseBlock> responseBlocks = new ArrayList<>();
        for (RequestBlock<?> block : requestObject.getR()) {
            String subsystem = block.getS();
            Class<?> aClass = handlersMap.get(subsystem);
            try {
                BasicHandler handler = (BasicHandler) aClass.newInstance();
                responseBlocks.add(handler.processRequest(block));
            } catch (Exception ex) {
                responseBlocks.add(new ExceptionResponseBlock<ExceptionData>(
                                new ResponseParams(
                                        block.getS(), block.getR(), "Handler is not supported",
                                        ERR_UNSUPPORTED, ResponseLang.EN_US.getLang())
                        )
                );
            }
        }
        return responseBlocks;
    }

}
