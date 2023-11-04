/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.cms;

import com.eustrosoft.cms.dbdatasource.DBDataSource;
import com.eustrosoft.cms.filedatasource.FileCMSDataSource;
import com.eustrosoft.cms.providers.DataSourceProvider;
import com.eustrosoft.core.handlers.cms.CMSHandler;
import com.eustrosoft.core.handlers.cms.CMSRequestBlock;
import com.eustrosoft.core.handlers.responses.QTisResponse;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.providers.SessionProvider;
import com.eustrosoft.core.tools.QJson;
import lombok.SneakyThrows;
import org.eustrosoft.qdbp.QDBPSession;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static com.eustrosoft.core.constants.Constants.ERR_SERVER;
import static com.eustrosoft.core.constants.Constants.ERR_UNEXPECTED;
import static com.eustrosoft.core.constants.Constants.REQUEST_VIEW;
import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_CMS;
import static com.eustrosoft.core.tools.HttpTools.getExceptionResponse;
import static com.eustrosoft.core.tools.HttpTools.getUnsupportedException;
import static com.eustrosoft.core.tools.HttpTools.printError;
import static com.eustrosoft.core.tools.LoginChecker.checkLogin;
import static com.eustrosoft.core.tools.LoginChecker.getUnauthorizedResponse;

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
public class CMSController extends HttpServlet {

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

        String path = req.getParameter("path");

        QDBPSession session = new SessionProvider(req, resp).getSession();
        CMSDataSource dataSource = DataSourceProvider.getInstance(session.getConnection())
                .getDataSource();

        try {
            if (dataSource instanceof DBDataSource) {
                if (path == null || path.isEmpty()) {
                    printError(
                            resp,
                            getExceptionResponse(
                                    "You did not provide path", SUBSYSTEM_CMS,
                                    REQUEST_VIEW, ERR_UNEXPECTED
                            )
                    );
                }
                long millis = System.currentTimeMillis();
                CMSHandler handler = new CMSHandler();
                ResponseBlock responseBlock = handler.processRequest(
                        new CMSRequestBlock(
                                req, resp,
                                new QJson(
                                        String.format("{\"path\":\"%s\", \"r\":\"%s\"}", path, REQUEST_VIEW)
                                )
                        )
                );
                List<ResponseBlock> respBlocks = new ArrayList<>();
                respBlocks.add(responseBlock);
                QTisResponse qTisResponse = new QTisResponse();
                qTisResponse.setResponseBlocks(respBlocks);
                qTisResponse.setT(System.currentTimeMillis() - millis);
                resp.setContentType("application/json");
                resp.setHeader("Cache-Control", "nocache");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter writer = resp.getWriter();
                writer.print(qTisResponse.getJson());
                resp.setStatus(200);
                writer.flush();
                writer.close();
            }
            if (dataSource instanceof FileCMSDataSource) {
                printError(resp, getUnsupportedException());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            printError(resp,
                    getExceptionResponse(
                            ex.getMessage(),
                            SUBSYSTEM_CMS,
                            REQUEST_VIEW,
                            ERR_SERVER
                    )
            );
        }
    }
}
