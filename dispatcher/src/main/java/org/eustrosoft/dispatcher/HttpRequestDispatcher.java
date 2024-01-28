/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.dispatcher;

import org.eustrosoft.dispatcher.context.HandlersConfig;
import org.eustrosoft.dispatcher.context.HandlersContext;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.spec.interfaces.Response;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.eustrosoft.tools.LoginChecker.getUnauthorizedResponse;
import static org.eustrosoft.tools.PropertiesConstants.DISPATCHER_FILE_NAME;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 10
)
public class HttpRequestDispatcher extends HttpServlet {

    private HandlersContext handlersContext = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            this.handlersContext = HandlersContext.getInstance(
                    new HandlersConfig(config.getInitParameter(DISPATCHER_FILE_NAME))
            );
            this.handlersContext.initLazy();
        } catch (IOException ex) {
            throw new ServletException("Init for handlers was failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        req.setCharacterEncoding("UTF-8");
//        try {
//            checkLogin(req, resp, SUBSYSTEM_CMS);
//        } catch (Exception ex) {
//            System.err.println("Unauthorized access.");
//            HttpTools.printError(resp, getUnauthorizedResponse());
//            return;
//        }
//
//        String id = req.getParameter("id");
//        String name = req.getParameter("name");
//        String path = req.getParameter("path");
//        String ticket = req.getParameter("ticket");
//        String contentType = req.getParameter("contentType");
//        QDBPSession session = new SessionProvider(req, resp).getSession();
//        CMSDataSource dataSource = DataSourceProvider.getInstance(session.getConnection())
//                .getDataSource();
//
//        try {
//            if (dataSource instanceof DBDataSource) {
//                if ((path == null || path.isEmpty())
//                        && (id == null || id.isEmpty())) {
//                    throw new Exception("You did not provide path. For database data " +
//                            "source is not possible to get file from other sources.");
//                }
//                path = (path == null || path.isEmpty()) ? String.format("/%s", id) : path;
//                InputStream fileStream = dataSource.getFileStream(path);
//                FileDetails fileDetails = dataSource.getFileDetails(path);
//                if (contentType != null && !contentType.isEmpty()) {
//                    fileDetails.setMimeType(contentType);
//                }
//                HttpTools.setHeadersForFileDownload(resp, fileDetails);
//                dataSource.uploadToStream(fileStream, resp.getOutputStream());
//                return;
//            }
//            if (dataSource instanceof FileCMSDataSource) {
//                if (ticket != null && !ticket.isEmpty()) {
//                    FileDownloadService.getInstance().downloadFile(req, resp, ticket, contentType);
//                }
//                if (path != null || !path.isEmpty()) {
//                    Json json = new Json().builder()
//                            .addKeyValue("path", path)
//                            .addKeyValue("r", REQUEST_TICKET)
//                            .build();
//                    CMSRequestBlock cmsRequestBlock = new CMSRequestBlock(req, resp, new QJson(json.toString()));
//                    ResponseBlock responseBlock = new CMSHandler().processRequest(cmsRequestBlock);
//                    String newTicket = responseBlock.getM();
//                    FileDownloadService.getInstance().downloadFile(req, resp, newTicket, contentType);
//                }
//            }
//        } catch (Exception ex) {
//            // ex.printStackTrace();
//            HttpTools.printError(resp,
//                    HttpTools.getExceptionResponse(
//                            ex.getMessage(),
//                            SUBSYSTEM_CMS,
//                            REQUEST_DOWNLOAD,
//                            ERR_SERVER
//                    )
//            );
//        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (this.handlersContext == null) {
            throw new IOException("Config file was not load, try configure again");
        }
        request.setCharacterEncoding("UTF-8");
        PostRequestProcessor requestProcessor = new PostRequestProcessor(request, response, this.handlersContext);
        QDBPSession session = new QDBPSession("123", 1L);
        Response resp = null;
        if (session != null) {
            synchronized (session) {
                resp = requestProcessor.processRequest();
            }
        } else {
            resp = requestProcessor.processRequest();
        }
        setResponseHeaders(response);
        PrintWriter writer = response.getWriter();
        try {
            writer.print(resp.toJsonString());
        } catch (JsonException ex) {
            writer.print(getUnauthorizedResponse());
        }
        response.setStatus(200);
        writer.flush();
        writer.close();
    }

    private void setResponseHeaders(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "nocache");
        response.setCharacterEncoding("UTF-8");
    }
}
