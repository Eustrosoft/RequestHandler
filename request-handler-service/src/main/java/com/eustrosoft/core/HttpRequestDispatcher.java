package com.eustrosoft.core;

import com.eustrosoft.core.handlers.ExceptionBlock;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.cms.CMSHandler;
import com.eustrosoft.core.handlers.cms.CMSRequestBlock;
import com.eustrosoft.core.handlers.cms.CMSResponseBlock;
import com.eustrosoft.core.handlers.login.LoginHandler;
import com.eustrosoft.core.handlers.login.LoginRequestBlock;
import com.eustrosoft.core.handlers.ping.PingHandler;
import com.eustrosoft.core.handlers.ping.PingRequestBlock;
import com.eustrosoft.core.handlers.requests.QTisRequestObject;
import com.eustrosoft.core.handlers.sql.SQLHandler;
import com.eustrosoft.core.handlers.sql.SQLRequestBlock;
import com.eustrosoft.core.handlers.file.BytesChunkFileHandler;
import com.eustrosoft.core.handlers.file.BytesChunkFileRequestBlock;
import com.eustrosoft.core.handlers.file.ChunkFileHandler;
import com.eustrosoft.core.handlers.file.ChunkFileRequestBlock;
import com.eustrosoft.core.handlers.file.FileHandler;
import com.eustrosoft.core.handlers.file.FileRequestBlock;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.requests.RequestObject;
import com.eustrosoft.core.handlers.responses.QTisResponse;
import com.eustrosoft.core.handlers.responses.Response;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.tools.QJson;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;

import javax.servlet.ServletException;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.eustrosoft.core.Constants.*;
import static com.eustrosoft.core.handlers.responses.ResponseLang.en_US;

@WebServlet(
        name = "EustrosoftRequestDispatcher",
        description = "Dispatches request depending on it's body dispatch value",
        urlPatterns = {"/api/dispatch"}
)
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 1024,
        maxRequestSize = 1024 * 1024 * 1024
)
public class HttpRequestDispatcher extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Response resp = processRequest(request, response);
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "nocache");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(resp.getJson());
        response.setStatus(200);
        writer.flush();
        writer.close();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("text/html");
        writer.println(
                "<form action=\"dispatch\" method=\"post\">\n" +
                        "<input type=\"submit\" value=\"Logout\" >\n" +
                        "</form>");
        writer.flush();
        writer.close();
    }

    @SneakyThrows
    private Response processRequest(HttpServletRequest request, HttpServletResponse response) {
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
        requestObject.setTimeout((Long) qJson.getItem(TIMEOUT));
        QJson requestsArray = qJson.getItemQJson(REQUESTS);

        List<RequestBlock> requestBlocks = getRequestBlocks(request, response, requestsArray);

        // Processing request blocks
        QTisResponse qTisResponse = new QTisResponse();
        List<ResponseBlock> responses = processRequestBlocks(requestBlocks);
        qTisResponse.setResponseBlocks(responses);
        qTisResponse.setT(System.currentTimeMillis() - millis);
        return qTisResponse;
    }

    @SneakyThrows
    private List<ResponseBlock> processRequestBlocks(
            List<RequestBlock> requestBlocks
    ) {
        List<ResponseBlock> responses = new ArrayList<>();
        for (RequestBlock block : requestBlocks) {
            Handler handler;
            String requestSubsystem = block.getS();
            String requestType = block.getR();
            switch (requestSubsystem) {
                case SUBSYSTEM_LOGIN:
                    handler = new LoginHandler(requestType);
                    break;
                case SUBSYSTEM_SQL:
                    handler = getSQLHandler(requestType);
                    break;
                case SUBSYSTEM_FILE:
                    handler = getFileHandler(requestType);
                    break;
                case SUBSYSTEM_PING:
                    handler = new PingHandler();
                    break;
                case SUBSYSTEM_CMS:
                    handler = new CMSHandler(requestType);
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
                System.out.println(
                        String.format("%d exceptions occurred\n%s errors msgs",
                                exCount, exceptionsBuilder)
                );
            }
        }
        return responses;
    }

    @SneakyThrows
    private List<RequestBlock> getRequestBlocks(
            HttpServletRequest request,
            HttpServletResponse response,
            QJson requestsArray
    ) {
        List<RequestBlock> requestBlocks = new ArrayList<>();
        for (int i = 0; i < requestsArray.size(); i++) {
            QJson qJson = requestsArray.getItemQJson(i);
            String subSystem = qJson.getItemString(SUBSYSTEM);
            String requestType = "";
            try {
                requestType = qJson.getItemString(REQUEST);
            } catch (Exception ex) {
                System.err.println("Can not get Request type. " + ex.getMessage());
            }
            checkLogin(request, response, subSystem); // main filter for logging user
            RequestBlock requestBlock = null;
            switch (subSystem) {
                case SUBSYSTEM_LOGIN:
                    requestBlock = new LoginRequestBlock(request, response, qJson);
                    ((LoginRequestBlock) requestBlock).setRequestType(requestType);
                    break;
                case SUBSYSTEM_PING:
                    requestBlock = new PingRequestBlock(request, response);
                    break;
                case SUBSYSTEM_SQL:
                    requestBlock = new SQLRequestBlock(request, response, qJson);
                    break;
                case SUBSYSTEM_FILE:
                    if (requestType.equals(REQUEST_FILE_UPLOAD)) {
                        requestBlock = new FileRequestBlock(request, response, qJson);
                    }
                    if (requestType.equals(REQUEST_CHUNKS_FILE_UPLOAD) ||
                            requestType.equals(REQUEST_CHUNKS_BINARY_FILE_UPLOAD)) {
                        requestBlock = new ChunkFileRequestBlock(request, response, qJson);
                    }
                    if (requestType.equals(REQUEST_CHUNKS_BINARY_FILE_UPLOAD)) {
                        requestBlock = new BytesChunkFileRequestBlock(request, response, qJson);
                    }
                    break;
                case SUBSYSTEM_CMS:
                    requestBlock = new CMSRequestBlock(request, response, qJson);
                    ((CMSRequestBlock) requestBlock).setRequestType(requestType);
                default:
                    break;
            }
            if (requestBlock != null) {
                requestBlocks.add(requestBlock);
            }
        }
        return requestBlocks;
    }

    // TODO: move to another class
    private Handler getSQLHandler(String requestType) {
        if (REQUEST_SQL.equals(requestType)) {
            return new SQLHandler();
        }
        return null;
    }

    private Handler getFileHandler(String requestType) {
        switch (requestType) {
            case REQUEST_FILE_UPLOAD:
                return new FileHandler();
            case REQUEST_CHUNKS_FILE_UPLOAD:
                return new ChunkFileHandler();
            case REQUEST_CHUNKS_BINARY_FILE_UPLOAD:
                return new BytesChunkFileHandler();
            default:
                return null;
        }
    }

    @SneakyThrows
    private void checkLogin(HttpServletRequest request,
                            HttpServletResponse response,
                            String subsystem) {
        if (hasSession(request) || isLoginSubsystem(subsystem)) {
            return;
        } else {
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.println(new Gson().toJson(
                    getUnauthorizedResponse()
            ));
            writer.flush();
            writer.close();
        }
    }

    private boolean hasSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null;
    }

    private boolean isLoginSubsystem(String subsystem) {
        return SUBSYSTEM_LOGIN.equalsIgnoreCase(subsystem);
    }

    private JsonObject getUnauthorizedResponse() {
        JsonObject object = new JsonObject();
        object.addProperty("l", en_US);
        JsonObject response = new JsonObject();
        response.addProperty("m", "Unauthorized");
        response.addProperty("e", 401);
        response.addProperty("s", "login");
        response.addProperty("r", "login");
        object.add("r", response);
        return object;
    }
}
