package com.eustrosoft.core;

import com.eustrosoft.core.handlers.Handler;
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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(
        name = "EustrosoftRequestDispatcher",
        description = "Dispatches request depending on it's body dispatch value",
        urlPatterns = {"/api/dispatch"}
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
        requestObject.setqTisEnd((Boolean) qJson.getItem("qtisend"));
        requestObject.setqTisVer((Long) qJson.getItem("qtisver"));

        QJson requestsArray = qJson.getItemQJson("requests");
        List<RequestBlock> requestBlocks = new ArrayList<>();

        for (int i = 0; i < requestsArray.size(); i++) {
            QJson reqst = requestsArray.getItemQJson(i);
            String subSystem = reqst.getItemString("subsystem");
            RequestBlock requestBlock;
            QJson params = reqst.getItemQJson("parameters");
            switch (subSystem) {
                case "sql":
                    requestBlock = new SQLRequestBlock(params);
                    requestBlocks.add(requestBlock);
                    break;
                case "file":
                    requestBlock = new FileRequestBlock(params);
                    requestBlocks.add(requestBlock);
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
                case "sql":
                    handler = new SQLHandler();
                    break;
                case "upload":
                    handler = new FileHandler();
                    break;
                default:
                    handler = null;
                    break;
            }
            if (handler != null) {
                int exCount = 0;
                StringBuilder exceptionsBuilder = new StringBuilder();
                try {
                    ResponseBlock respBlock = handler.processRequest(block, request);
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
