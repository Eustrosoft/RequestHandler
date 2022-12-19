package com.eustrosoft.core;

import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.QTisRequestObject;
import com.eustrosoft.core.handlers.RequestObject;
import com.eustrosoft.core.handlers.Response;
import com.eustrosoft.core.handlers.requests.RequestBlock;
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

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        super.doPost(request, response);
        Response resp = processRequest(request);
        response.setStatus(200);
    }

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
        QJson qJson = new QJson();
        qJson.parseJSONReader(request.getReader());
        RequestObject requestObject = new QTisRequestObject();
        requestObject.setqTisEnd(qJson.getItemString("qTisEnd").equals("true"));
        requestObject.setqTisVer(Integer.parseInt(qJson.getItemString("qTisVer")));
        QJson requestsArray = qJson.getItemQJson("requests");
        List<RequestBlock> requestBlocks = new ArrayList<>();
        for (int i = 0; i < requestsArray.size(); i++) {
            QJson reqst = requestsArray.getItemQJson(i);
            String subsys = reqst.getItemString("subsystem");
            String reqs = reqst.getItemString("request");
            RequestBlock requestBlock;
            switch (subsys) {
                case "sql":
                    requestBlock = new SQLRequestBlock();
                    break;
                case "file":
                    System.out.println("File processing...");
                    break;
                default:
                    break;
            }
        }
        //requestObject.setRequestBlocks();

        // for {
        QJson reqs = qJson.getItemQJson("requests"); // List<QJSon>
        String subsys = reqs.getItemString("subsystem");
        String req = reqs.getItemString("request");
        Handler handler;
        RequestBlock requestBlock = null;
        switch (subsys) {
            case "sql":
                requestBlock = new SQLRequestBlock();
                handler = new SQLHandler();
                break;
            case "file":
                System.out.println("File processing...");
                handler = null;
                break;
            default:
                handler = null;
                break;
        }
        if (handler != null) {
            handler.processRequest(requestBlock);
        }
        // }
        return null;
    }
}
