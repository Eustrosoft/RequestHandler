/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.dispatcher;

import lombok.SneakyThrows;
import org.eustrosoft.cms.CMSDataSource;
import org.eustrosoft.cms.dbdatasource.DBDataSource;
import org.eustrosoft.cms.filedatasource.FileCMSDataSource;
import org.eustrosoft.cms.parameters.FileDetails;
import org.eustrosoft.cms.providers.DataSourceProvider;
import org.eustrosoft.core.handlers.cms.CMSHandler;
import org.eustrosoft.core.handlers.cms.CMSRequestBlock;
import org.eustrosoft.core.handlers.responses.Response;
import org.eustrosoft.core.handlers.responses.ResponseBlock;
import org.eustrosoft.core.providers.SessionProvider;
import org.eustrosoft.core.services.FileDownloadService;
import org.eustrosoft.core.tools.HttpTools;
import org.eustrosoft.core.tools.Json;
import org.eustrosoft.core.tools.QJson;
import org.eustrosoft.qdbp.QDBPSession;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import static org.eustrosoft.core.constants.Constants.*;
import static org.eustrosoft.core.tools.LoginChecker.checkLogin;
import static org.eustrosoft.core.tools.LoginChecker.getUnauthorizedResponse;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 10
)
public class HttpRequestDispatcher extends HttpServlet {

    @Override
    @SneakyThrows
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        req.setCharacterEncoding("UTF-8");
        try {
            checkLogin(req, resp, SUBSYSTEM_CMS);
        } catch (Exception ex) {
            System.err.println("Unauthorized access.");
            HttpTools.printError(resp, getUnauthorizedResponse());
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
                HttpTools.setHeadersForFileDownload(resp, fileDetails);
                dataSource.uploadToStream(fileStream, resp.getOutputStream());
                return;
            }
            if (dataSource instanceof FileCMSDataSource) {
                if (ticket != null && !ticket.isEmpty()) {
                    FileDownloadService.getInstance().downloadFile(req, resp, ticket, contentType);
                }
                if (path != null || !path.isEmpty()) {
                    Json json = new Json().builder()
                            .addKeyValue("path", path)
                            .addKeyValue("r", REQUEST_TICKET)
                            .build();
                    CMSRequestBlock cmsRequestBlock = new CMSRequestBlock(req, resp, new QJson(json.toString()));
                    ResponseBlock responseBlock = new CMSHandler().processRequest(cmsRequestBlock);
                    String newTicket = responseBlock.getM();
                    FileDownloadService.getInstance().downloadFile(req, resp, newTicket, contentType);
                }
            }
        } catch (Exception ex) {
            // ex.printStackTrace();
            HttpTools.printError(resp,
                    HttpTools.getExceptionResponse(
                            ex.getMessage(),
                            SUBSYSTEM_CMS,
                            REQUEST_DOWNLOAD,
                            ERR_SERVER
                    )
            );
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        PostRequestProcessor requestProcessor = new PostRequestProcessor(request, response);
        QDBPSession session = new QDBPSession("123", 1L);
//        try {
//            session = new SessionProvider(request, response).getSession();
//        } catch (Exception ex) {
//            System.out.println("User without session");
//        }
        Response resp = null;
        if (session != null) {
            synchronized (session) {
                resp = requestProcessor.processRequest();
            }
        } else {
            resp = requestProcessor.processRequest();
        }
        response.setContentType("application/json"); // todo think about it
        response.setHeader("Cache-Control", "nocache");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(resp.getJson());
        response.setStatus(200);
        writer.flush();
        writer.close();
    }
}