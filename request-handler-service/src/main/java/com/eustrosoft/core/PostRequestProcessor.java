package com.eustrosoft.core;

import com.eustrosoft.core.handlers.ExceptionBlock;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.cms.CMSHandler;
import com.eustrosoft.core.handlers.cms.CMSRequestBlock;
import com.eustrosoft.core.handlers.dic.DICHandler;
import com.eustrosoft.core.handlers.dic.DICRequestBlock;
import com.eustrosoft.core.handlers.file.*;
import com.eustrosoft.core.handlers.login.LoginHandler;
import com.eustrosoft.core.handlers.login.LoginRequestBlock;
import com.eustrosoft.core.handlers.msg.MSGHandler;
import com.eustrosoft.core.handlers.msg.MSGRequestBlock;
import com.eustrosoft.core.handlers.ping.PingHandler;
import com.eustrosoft.core.handlers.ping.PingRequestBlock;
import com.eustrosoft.core.handlers.requests.QTisRequestObject;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.requests.RequestObject;
import com.eustrosoft.core.handlers.responses.QTisResponse;
import com.eustrosoft.core.handlers.responses.Response;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.handlers.sam.SAMHandler;
import com.eustrosoft.core.handlers.sam.SAMRequestBlock;
import com.eustrosoft.core.tools.QJson;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.eustrosoft.core.constants.Constants.*;
import static com.eustrosoft.core.handlers.responses.ResponseLang.en_US;
import static com.eustrosoft.core.tools.LoginChecker.checkLogin;

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
            // ignored
        }

        QJson qJson = new QJson();
        if (jsonPart != null) {
            qJson.parseJSONReader(new InputStreamReader(jsonPart.getInputStream()));
        } else {
            qJson.parseJSONReader(request.getReader());
        }

        RequestObject requestObject = new QTisRequestObject();
        requestObject.setTimeout((Long) qJson.getItem(TIMEOUT));
        QJson requestsArray = qJson.getItemQJson(REQUESTS);

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
                case SUBSYSTEM_LOGIN:
                    handler = new LoginHandler();
                    break;
//                case SUBSYSTEM_SQL:
//                    handler = new SQLHandler(); disabled sql handler
//                    break;
                case SUBSYSTEM_FILE:
                    handler = getFileHandler(requestType);
                    break;
                case SUBSYSTEM_PING:
                    handler = new PingHandler();
                    break;
                case SUBSYSTEM_CMS:
                    handler = new CMSHandler();
                    break;
                case SUBSYSTEM_MSG:
                    handler = new MSGHandler();
                    break;
                case SUBSYSTEM_SAM:
                    handler = new SAMHandler();
                    break;
                case SUBSYSTEM_DIC:
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
                            en_US,
                            requestSubsystem,
                            requestType
                    ));
                }
            }
        }
        return responses;
    }

    @SneakyThrows
    private List<RequestBlock> getRequestBlocks(QJson requestsArray) {
        List<RequestBlock> requestBlocks = new ArrayList<>();
        for (int i = 0; i < requestsArray.size(); i++) {
            QJson qJson = requestsArray.getItemQJson(i);
            String subSystem = qJson.getItemString(SUBSYSTEM);
            String requestType = "";
            try {
                requestType = qJson.getItemString(REQUEST);
            } catch (Exception ex) {
                // ignored
            }
            checkLogin(request, response, subSystem); // main filter for logging user
            RequestBlock requestBlock = null;
            switch (subSystem) {
                case SUBSYSTEM_LOGIN:
                    requestBlock = new LoginRequestBlock(request, response, qJson);
                    break;
                case SUBSYSTEM_PING:
                    requestBlock = new PingRequestBlock(request, response);
                    break;
//                case SUBSYSTEM_SQL:
//                    requestBlock = new SQLRequestBlock(request, response, qJson);
//                    break; disable sql subsystem temporary
                case SUBSYSTEM_FILE:
                    if (requestType.equals(REQUEST_FILE_UPLOAD)) {
                        requestBlock = new FileRequestBlock(request, response, qJson);
                    }
                    if (requestType.equals(REQUEST_CHUNKS_FILE_UPLOAD)) {
                        requestBlock = new ChunkFileRequestBlock(request, response, qJson);
                    }
                    if (requestType.equals(REQUEST_CHUNKS_BINARY_FILE_UPLOAD)) {
                        requestBlock = new BytesChunkFileRequestBlock(request, response, qJson);
                    }
                    if (requestType.equals(REQUEST_CHUNKS_HEX_FILE_UPLOAD)) {
                        requestBlock = new HexFileRequestBlock(request, response, qJson);
                    }
                    break;
                case SUBSYSTEM_CMS:
                    requestBlock = new CMSRequestBlock(request, response, qJson);
                    break;
                case SUBSYSTEM_MSG:
                    requestBlock = new MSGRequestBlock(request, response, qJson);
                    break;
                case SUBSYSTEM_SAM:
                    requestBlock = new SAMRequestBlock(request, response, qJson);
                    break;
                case SUBSYSTEM_DIC:
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
            case REQUEST_FILE_UPLOAD:
                return new FileHandler();
            case REQUEST_CHUNKS_FILE_UPLOAD:
                return new ChunkFileHandler();
            case REQUEST_CHUNKS_BINARY_FILE_UPLOAD:
                return new BytesChunkFileHandler();
            case REQUEST_CHUNKS_HEX_FILE_UPLOAD:
                return new HexFileHandler();
            default:
                return null;
        }
    }
}
