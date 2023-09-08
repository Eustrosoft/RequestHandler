/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core;

import com.eustrosoft.core.context.DBPoolContext;
import com.eustrosoft.core.handlers.ExceptionBlock;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.cms.CMSHandler;
import com.eustrosoft.core.handlers.cms.CMSRequestBlock;
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
import com.eustrosoft.core.handlers.sql.SQLHandler;
import com.eustrosoft.core.handlers.sql.SQLRequestBlock;
import com.eustrosoft.core.providers.DataSourceProvider;
import com.eustrosoft.core.providers.SessionProvider;
import com.eustrosoft.core.tools.Json;
import com.eustrosoft.core.tools.QJson;
import com.eustrosoft.datasource.sources.CMSDataSource;
import com.eustrosoft.datasource.sources.FileDetails;
import com.eustrosoft.dbdatasource.core.DBDataSource;
import com.eustrosoft.filedatasource.FileCMSDataSource;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.eustrosoft.core.Constants.*;
import static com.eustrosoft.core.handlers.responses.ResponseLang.en_US;
import static org.apache.commons.io.IOUtils.DEFAULT_BUFFER_SIZE;

@WebServlet(
        name = "EustrosoftRequestDispatcher",
        description = "Dispatches request depending on it's body dispatch value",
        urlPatterns = {"/api/dispatch", "/api/download"}
)
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 10
)
public class HttpRequestDispatcher extends HttpServlet {
    // todo: move out from here.
    private static final String[] AVAILABLE_CONTENT_TYPES = new String[]{
            "application/octet-stream", "application/json",
            "application/pdf", "application/xml", "plain/text",
            "image/gif", "image/jpeg", "image/jpg", "image/png",
            "image/svg", "video/mp4", "video/mpeg"
    };

    @Override
    @SneakyThrows
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        req.setCharacterEncoding("UTF-8");
        try {
            checkLogin(req, resp, SUBSYSTEM_CMS);
        } catch (Exception ex) {
            System.err.println("Unauthorized access.");
            printError(resp, getUnauthorizedResponse());
            return;
        }

        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String path = req.getParameter("path");
        String ticket = req.getParameter("ticket");
        String contentType = req.getParameter("contentType");
        QDBPSession session = new SessionProvider(req, resp).getSession();
        CMSDataSource dataSource = DataSourceProvider.getInstance(session.getConnection())
                .getDataSource();

        try {
            if (dataSource instanceof DBDataSource) {
                if ((path == null || path.isEmpty())
                        && (id == null || id.isEmpty())) {
                    throw new Exception("You did not provide path. For database data " +
                            "source is not possible to get file from other sources.");
                }
                path = (path == null || path.isEmpty()) ? String.format("/%s", id) : path;
                InputStream fileStream = dataSource.getFileStream(path);
                FileDetails fileDetails = dataSource.getFileDetails(path);
                if (contentType != null && !contentType.isEmpty()) {
                    fileDetails.setMimeType(contentType);
                }
                setHeadersForFileDownload(resp, fileDetails);
                dataSource.uploadToStream(fileStream, resp.getOutputStream());
                return;
            }
            if (dataSource instanceof FileCMSDataSource) {
                if (ticket != null && !ticket.isEmpty()) {
                    downloadFile(req, resp, ticket, contentType);
                }
                if (path != null || !path.isEmpty()) {
                    Json json = new Json().builder()
                            .addKeyValue("path", path)
                            .addKeyValue("r", REQUEST_TICKET)
                            .build();
                    CMSRequestBlock cmsRequestBlock = new CMSRequestBlock(req, resp, new QJson(json.toString()));
                    ResponseBlock responseBlock = new CMSHandler(REQUEST_TICKET)
                            .processRequest(cmsRequestBlock);
                    String newTicket = responseBlock.getM();
                    downloadFile(req, resp, newTicket, contentType);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            printError(resp,
                    getExceptionResponse(
                            ex.getMessage(),
                            SUBSYSTEM_CMS,
                            REQUEST_DOWNLOAD,
                            ERR_SERVER)
            );
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        Response resp = processRequest(request, response);
        response.setContentType("application/json"); // todo think about it
        response.setHeader("Cache-Control", "nocache");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(resp.getJson());
        response.setStatus(200);
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
                case SUBSYSTEM_MSG:
                    handler = new MSGHandler(requestType);
                    break;
                case SUBSYSTEM_SAM:
                    handler = new SAMHandler();
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
                    ((CMSRequestBlock) requestBlock).setRequestType(requestType);
                    break;
                case SUBSYSTEM_MSG:
                    requestBlock = new MSGRequestBlock(request, response, qJson);
                    ((MSGRequestBlock) requestBlock).setRequestType(requestType);
                    break;
                case SUBSYSTEM_SAM:
                    requestBlock = new SAMRequestBlock(request, response, qJson);
                    ((SAMRequestBlock) requestBlock).setRequestType(requestType);
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
            case REQUEST_CHUNKS_HEX_FILE_UPLOAD:
                return new HexFileHandler();
            default:
                return null;
        }
    }

    @SneakyThrows
    private void checkLogin(HttpServletRequest request,
                            HttpServletResponse response,
                            String subsystem) {
        QTISSessionCookie qTisCookie = new QTISSessionCookie(request, response);
        String cookieValue = qTisCookie.getCookieValue();
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(request),
                DBPoolContext.getUrl(request),
                DBPoolContext.getDriverClass(request)
        );
        QDBPSession session = dbPool.logon(cookieValue);

        if (session == null && !isLoginSubsystem(subsystem)) {
            printError(response, getUnauthorizedResponse());
            throw new Exception("Unauthorized access");
        }
    }

    private void downloadFile(HttpServletRequest req, HttpServletResponse resp, String ticket, String contentType)
            throws IOException {
        Json.JsonBuilder builder = new Json().builder();
        builder.addKeyValue("s", SUBSYSTEM_CMS);
        if (ticket != null && !ticket.isEmpty()) {
            Json.JsonBuilder jsonBuilder = builder.addKeyValue("ticket", ticket)
                    .addKeyValue("r", REQUEST_DOWNLOAD);
            if (contentType != null && !contentType.isEmpty()) {
                jsonBuilder.addKeyValue("contentType", contentType);
            }
            Json json = jsonBuilder.build();
            CMSRequestBlock cmsRequestBlock = new CMSRequestBlock(req, resp, new QJson(json.toString()));
            try {
                new CMSHandler(REQUEST_DOWNLOAD)
                        .processRequest(cmsRequestBlock);
            } catch (Exception e) {
                printError(
                        resp,
                        getExceptionResponse(
                                "Error while downloading file occurred.",
                                SUBSYSTEM_CMS, REQUEST_DOWNLOAD, ERR_SERVER
                        )
                );
            }
        }
    }

    private boolean isLoginSubsystem(String subsystem) {
        return SUBSYSTEM_LOGIN.equalsIgnoreCase(subsystem);
    }

    private void printError(HttpServletResponse resp, JsonObject exceptionBlock) throws IOException {
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.println(new Gson().toJson(exceptionBlock));
        writer.flush();
        writer.close();
    }

    private JsonObject getUnauthorizedResponse() {
        return getExceptionResponse("Unauthorized", "login", "login", ERR_UNAUTHORIZED);
    }

    private JsonObject getExceptionResponse(String message, String subsystem, String request, Short errType) {
        JsonObject object = new JsonObject();
        object.addProperty("l", en_US);
        JsonObject response = new JsonObject();
        response.addProperty("m", message);
        response.addProperty("e", errType);
        response.addProperty("s", subsystem);
        response.addProperty("r", request);
        object.add("r", response);
        return object;
    }

    // FILE DOWNLOAD PART TODO: refactor

    @SneakyThrows
    private void setHeadersForFileDownload(HttpServletResponse response, FileDetails fileDetails) {
        response.reset();
        setContentType(response, fileDetails.getMimeType());
        response.setCharacterEncoding(fileDetails.getEncoding());
        response.setHeader(
                "Content-Disposition",
                String.format(
                        "attachment; filename=\"%s\";;filename*=utf-8''%s",
                        fileDetails.getFileName(),
                        URLEncoder.encode(fileDetails.getFileName(), "UTF-8")
                )
        );
        response.setContentLengthLong(fileDetails.getFileLength());
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Accept-Ranges", "bytes");
    }

    private void setContentType(HttpServletResponse httpResponse, String mimeType) {
        if (mimeType != null && !mimeType.isEmpty()) {
            if (isAvailableContentType(mimeType)) {
                httpResponse.setContentType(mimeType);
                return;
            }
        }
        httpResponse.setContentType("application/octet-stream");
    }

    private boolean isAvailableContentType(String contentType) {
        return Arrays.asList(AVAILABLE_CONTENT_TYPES).contains(contentType);
    }
}
