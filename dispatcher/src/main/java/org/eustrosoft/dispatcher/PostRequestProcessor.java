package org.eustrosoft.dispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.core.RequestContextHolder;
import org.eustrosoft.dispatcher.context.HandlersContext;
import org.eustrosoft.json.QJson;
import org.eustrosoft.spec.ResponseLang;
import org.eustrosoft.spec.ResponseParams;
import org.eustrosoft.spec.interfaces.Request;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.Response;
import org.eustrosoft.spec.interfaces.ResponseBlock;
import org.eustrosoft.spec.request.TISRequest;
import org.eustrosoft.spec.response.ExceptionResponseBlock;
import org.eustrosoft.spec.response.TISResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.eustrosoft.spec.Constants.ERR_SERVER;
import static org.eustrosoft.spec.Constants.ERR_UNEXPECTED;
import static org.eustrosoft.spec.Constants.ERR_UNSUPPORTED;
import static org.eustrosoft.spec.Constants.MSG_UNEXPECTED;
import static org.eustrosoft.spec.Constants.MSG_UNKNOWN;

public final class PostRequestProcessor {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HandlersContext handlersContext;

    public PostRequestProcessor(HandlersContext context) {
        this.handlersContext = context;
        RequestContextHolder.ServletAttributes attributes = RequestContextHolder.getAttributes();
        this.request = attributes.getRequest();
        this.response = attributes.getResponse();
    }

    public Response processRequest() throws IOException {
        long millis = System.currentTimeMillis();

        // Req and resp
        TISResponse response = new TISResponse();
        Request requestObject = new TISRequest();

        try {
            Part jsonPart = null;
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

            requestObject.fromJson(qJson);

            List<ResponseBlock> responses = processRequestBlocks(requestObject);
            response.setResponseBlocks(responses);
        } catch (Exception exception) {
            List<ResponseBlock> blocks = response.getR();
            if (blocks == null) {
                response.setResponseBlocks(new ArrayList<>());
            }
            response.getR().add(
                    new ExceptionResponseBlock(new ResponseParams(MSG_UNKNOWN, MSG_UNKNOWN, MSG_UNEXPECTED, ERR_UNEXPECTED, ResponseLang.EN_US.getLang()))
            );
        }
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
            } catch (ClassNotFoundException e) {
                responseBlocks.add(new ExceptionResponseBlock(
                                new ResponseParams(
                                        block.getS(), block.getR(), "Handler is not supported",
                                        ERR_UNSUPPORTED, ResponseLang.EN_US.getLang())
                        )
                );
            } catch (Exception ex) {
                responseBlocks.add(new ExceptionResponseBlock(
                                new ResponseParams(
                                        block.getS(), block.getR(), ex.getMessage(),
                                        ERR_SERVER, ResponseLang.EN_US.getLang()
                                )
                        )
                );
            }
        }
        return responseBlocks;
    }

}
