package org.eustrosoft.core;

import lombok.SneakyThrows;
import org.eustrosoft.core.constants.Constants;
import org.eustrosoft.core.handlers.ExceptionBlock;
import org.eustrosoft.core.handlers.Handler;
import org.eustrosoft.core.handlers.cms.CMSHandler;
import org.eustrosoft.core.handlers.cms.CMSRequestBlock;
import org.eustrosoft.core.handlers.dic.DICHandler;
import org.eustrosoft.core.handlers.dic.DICRequestBlock;
import org.eustrosoft.core.handlers.file.*;
import org.eustrosoft.core.handlers.login.LoginHandler;
import org.eustrosoft.core.handlers.login.LoginRequestBlock;
import org.eustrosoft.core.handlers.msg.MSGHandler;
import org.eustrosoft.core.handlers.msg.MSGRequestBlock;
import org.eustrosoft.core.handlers.ping.PingHandler;
import org.eustrosoft.core.handlers.ping.PingRequestBlock;
import org.eustrosoft.core.handlers.requests.QTisRequestObject;
import org.eustrosoft.core.handlers.requests.RequestBlock;
import org.eustrosoft.core.handlers.requests.RequestObject;
import org.eustrosoft.core.handlers.responses.QTisResponse;
import org.eustrosoft.core.handlers.responses.Response;
import org.eustrosoft.core.handlers.responses.ResponseBlock;
import org.eustrosoft.core.handlers.responses.ResponseLang;
import org.eustrosoft.core.handlers.sam.SAMHandler;
import org.eustrosoft.core.handlers.sam.SAMRequestBlock;
import org.eustrosoft.core.handlers.sql.SQLHandler;
import org.eustrosoft.core.handlers.sql.SQLRequestBlock;
import org.eustrosoft.core.tools.LoginChecker;
import org.eustrosoft.core.tools.QJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class PostRequestProcessor {
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public PostRequestProcessor(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        this.request = request;
        this.response = response;
    }

    @SneakyThrows
    public Response processRequest() {
        // Parsing query and getting request blocks
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

        RequestObject requestObject = new QTisRequestObject();
        requestObject.setTimeout((Long) qJson.getItem(Constants.TIMEOUT));
        QJson requestsArray = qJson.getItemQJson(Constants.REQUESTS);

        List<RequestBlock> requestBlocks = getRequestBlocks(requestsArray);

        // Processing request blocks
        QTisResponse qTisResponse = new QTisResponse();
        List<ResponseBlock> responses = processRequestBlocks(requestBlocks);
        qTisResponse.setResponseBlocks(responses);
        qTisResponse.setT(System.currentTimeMillis() - millis);
        return qTisResponse;
    }

    @SneakyThrows
    private List<ResponseBlock> processRequestBlocks(List<RequestBlock> requestBlocks) {
        List<ResponseBlock> responses = new ArrayList<>();
        for (RequestBlock block : requestBlocks) {
            Handler handler;
            String requestSubsystem = block.getS();
            String requestType = block.getR();
            switch (requestSubsystem) {
                case Constants.SUBSYSTEM_LOGIN:
                    handler = new LoginHandler();
                    break;
                case Constants.SUBSYSTEM_SQL:
                    handler = new SQLHandler();
                    break;
                case Constants.SUBSYSTEM_FILE:
                    handler = getFileHandler(requestType);
                    break;
                case Constants.SUBSYSTEM_PING:
                    handler = new PingHandler();
                    break;
                case Constants.SUBSYSTEM_CMS:
                    handler = new CMSHandler();
                    break;
                case Constants.SUBSYSTEM_MSG:
                    handler = new MSGHandler();
                    break;
                case Constants.SUBSYSTEM_SAM:
                    handler = new SAMHandler();
                    break;
                case Constants.SUBSYSTEM_DIC:
                    handler = new DICHandler();
                    break;
                default:
                    handler = null;
                    break;
            }
            if (handler != null) {
                int exCount = 0;
                StringBuilder exceptionsBuilder = new StringBuilder();
                try {
                    ResponseBlock respBlock = handler.processRequest(block);
                    responses.add(respBlock);
                } catch (Exception ex) {
                    exCount += 1;
                    exceptionsBuilder.append(ex.getMessage()).append("\n");
                    responses.add(new ExceptionBlock(
                            ex.getMessage(),
                            (short) 500,
                            ResponseLang.en_US,
                            requestSubsystem,
                            requestType
                    ));
                }
                System.out.println(
                        String.format("%d exceptions occurred\n%s errors msgs",
                                exCount, exceptionsBuilder)
                );
            }
        }
        return responses;
    }

    @SneakyThrows
    private List<RequestBlock> getRequestBlocks(QJson requestsArray) {
        List<RequestBlock> requestBlocks = new ArrayList<>();
        for (int i = 0; i < requestsArray.size(); i++) {
            QJson qJson = requestsArray.getItemQJson(i);
            String subSystem = qJson.getItemString(Constants.SUBSYSTEM);
            String requestType = "";
            try {
                requestType = qJson.getItemString(Constants.REQUEST);
            } catch (Exception ex) {
                System.err.println("Can not get Request type. " + ex.getMessage());
            }
            LoginChecker.checkLogin(request, response, subSystem); // main filter for logging user
            RequestBlock requestBlock = null;
            switch (subSystem) {
                case Constants.SUBSYSTEM_LOGIN:
                    requestBlock = new LoginRequestBlock(request, response, qJson);
                    break;
                case Constants.SUBSYSTEM_PING:
                    requestBlock = new PingRequestBlock(request, response);
                    break;
                case Constants.SUBSYSTEM_SQL:
                    requestBlock = new SQLRequestBlock(request, response, qJson);
                    break;
                case Constants.SUBSYSTEM_FILE:
                    if (requestType.equals(Constants.REQUEST_FILE_UPLOAD)) {
                        requestBlock = new FileRequestBlock(request, response, qJson);
                    }
                    if (requestType.equals(Constants.REQUEST_CHUNKS_FILE_UPLOAD)) {
                        requestBlock = new ChunkFileRequestBlock(request, response, qJson);
                    }
                    if (requestType.equals(Constants.REQUEST_CHUNKS_BINARY_FILE_UPLOAD)) {
                        requestBlock = new BytesChunkFileRequestBlock(request, response, qJson);
                    }
                    if (requestType.equals(Constants.REQUEST_CHUNKS_HEX_FILE_UPLOAD)) {
                        requestBlock = new HexFileRequestBlock(request, response, qJson);
                    }
                    break;
                case Constants.SUBSYSTEM_CMS:
                    requestBlock = new CMSRequestBlock(request, response, qJson);
                    break;
                case Constants.SUBSYSTEM_MSG:
                    requestBlock = new MSGRequestBlock(request, response, qJson);
                    break;
                case Constants.SUBSYSTEM_SAM:
                    requestBlock = new SAMRequestBlock(request, response, qJson);
                    break;
                case Constants.SUBSYSTEM_DIC:
                    requestBlock = new DICRequestBlock(request, response, qJson);
                    break;
                default:
                    break;
            }
            if (requestBlock != null) {
                requestBlocks.add(requestBlock);
            }
        }
        return requestBlocks;
    }

    private Handler getFileHandler(String requestType) {
        switch (requestType) {
            case Constants.REQUEST_FILE_UPLOAD:
                return new FileHandler();
            case Constants.REQUEST_CHUNKS_FILE_UPLOAD:
                return new ChunkFileHandler();
            case Constants.REQUEST_CHUNKS_BINARY_FILE_UPLOAD:
                return new BytesChunkFileHandler();
            case Constants.REQUEST_CHUNKS_HEX_FILE_UPLOAD:
                return new HexFileHandler();
            default:
                return null;
        }
    }
}
