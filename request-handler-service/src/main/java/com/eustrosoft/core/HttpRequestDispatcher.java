package com.eustrosoft.core;

import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.file.BytesChunkFileHandler;
import com.eustrosoft.core.handlers.file.ChunkFileHandler;
import com.eustrosoft.core.handlers.file.ChunkFileRequestBlock;
import com.eustrosoft.core.handlers.file.FileHandler;
import com.eustrosoft.core.handlers.file.FileRequestBlock;
import com.eustrosoft.core.handlers.requests.QTisRequestObject;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.requests.RequestObject;
import com.eustrosoft.core.handlers.responses.QTisResponse;
import com.eustrosoft.core.handlers.responses.Response;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.handlers.sql.SQLHandler;
import com.eustrosoft.core.handlers.sql.SQLRequestBlock;
import com.eustrosoft.core.tools.QJson;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static com.eustrosoft.core.Constants.PARAMETERS;
import static com.eustrosoft.core.Constants.QTISEND;
import static com.eustrosoft.core.Constants.QTISVER;
import static com.eustrosoft.core.Constants.REQUEST;
import static com.eustrosoft.core.Constants.REQUESTS;
import static com.eustrosoft.core.Constants.REQUEST_CHUNKS_BINARY_FILE_UPLOAD;
import static com.eustrosoft.core.Constants.REQUEST_CHUNKS_FILE_UPLOAD;
import static com.eustrosoft.core.Constants.REQUEST_FILE_UPLOAD;
import static com.eustrosoft.core.Constants.REQUEST_SQL;
import static com.eustrosoft.core.Constants.SUBSYSTEM;
import static com.eustrosoft.core.Constants.SUBSYSTEM_FILE;
import static com.eustrosoft.core.Constants.SUBSYSTEM_SQL;

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
        Response resp = processRequest(request);
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

    private Response processRequest(HttpServletRequest request) throws IOException {
        // Parsing query and getting request blocks

        QJson qJson = new QJson();
        qJson.parseJSONReader(request.getReader());
        RequestObject requestObject = new QTisRequestObject();
        requestObject.setqTisEnd((Boolean) qJson.getItem(QTISEND));
        requestObject.setqTisVer((Long) qJson.getItem(QTISVER));

        QJson requestsArray = qJson.getItemQJson(REQUESTS);
        List<RequestBlock> requestBlocks = new ArrayList<>();

        for (int i = 0; i < requestsArray.size(); i++) {
            QJson reqst = requestsArray.getItemQJson(i);
            String subSystem = reqst.getItemString(SUBSYSTEM);
            String requestType = reqst.getItemString(REQUEST);
            RequestBlock requestBlock;
            QJson params = reqst.getItemQJson(PARAMETERS);
            switch (subSystem) {
                case SUBSYSTEM_SQL:
                    requestBlock = new SQLRequestBlock(request, params);
                    requestBlocks.add(requestBlock);
                    break;
                case SUBSYSTEM_FILE:
                    if (requestType.equals(REQUEST_FILE_UPLOAD)) {
                        requestBlock = new FileRequestBlock(request, params);
                        requestBlocks.add(requestBlock);
                    }
                    if (requestType.equals(REQUEST_CHUNKS_FILE_UPLOAD) ||
                            requestType.equals(REQUEST_CHUNKS_BINARY_FILE_UPLOAD)) {
                        requestBlock = new ChunkFileRequestBlock(request, params);
                        requestBlocks.add(requestBlock);
                    }
                    break;
                default:
                    break;
            }
        }

        // Processing request blocks
        QTisResponse qTisResponse = new QTisResponse();
        List<ResponseBlock> responses = new ArrayList<>();
        for (RequestBlock block : requestBlocks) {
            Handler handler;
            String requestType = block.getRequest();
            switch (requestType) {
                case REQUEST_SQL:
                    handler = new SQLHandler();
                    break;
                case REQUEST_FILE_UPLOAD:
                    handler = new FileHandler();
                    break;
                case REQUEST_CHUNKS_FILE_UPLOAD:
                    handler = new ChunkFileHandler();
                    break;
                case REQUEST_CHUNKS_BINARY_FILE_UPLOAD:
                    handler = new BytesChunkFileHandler();
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
                }
                System.out.println(
                        String.format("%d exceptions occurred\n%s errors msgs",
                                exCount, exceptionsBuilder)
                );
            }

        }
        qTisResponse.setResponseBlocks(responses);
        return qTisResponse;
    }
}
