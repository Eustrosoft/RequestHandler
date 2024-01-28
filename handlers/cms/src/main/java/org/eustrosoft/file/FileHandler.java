/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.file;

import org.eustrosoft.cms.UserStorage;
import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;
import org.eustrosoft.sam.dao.SamDAO;
import org.eustrosoft.sam.model.User;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.interfaces.ResponseBlock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class FileHandler implements BasicHandler {
    private HttpServletResponse response;
    private HttpServletRequest request;

    public FileHandler(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock)
            throws IOException, SQLException {
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(request),
                DBPoolContext.getUrl(request),
                DBPoolContext.getDriverClass(request)
        );
        QDBPSession dbps = dbPool.logon(new QTISSessionCookie(request, response).getCookieValue());
        SamDAO samDao = new SamDAO(dbps.getConnection());
        User user = samDao.getUserById(samDao.getUserId());
        if (user.getSessionPath() == null || user.getSessionPath().isEmpty()) {
            UserStorage storage = UserStorage.getInstanceForUser(user);
            user.setSessionPath(storage.createAndGetNewStoragePath());
        }
        FileRequestBlock fileRequestBlock = (FileRequestBlock) requestBlock;
        byte[] fileBytes = fileRequestBlock.getFileBytes();
        String fileName = fileRequestBlock.getFileName();
        String answer = "";
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(
                        new File(user.getSessionPath(), fileName)
                )
        )) {
            bos.write(fileBytes);
            bos.flush();
            answer = fileName;
        } catch (Exception ex) {
            // ex.printStackTrace();
            answer = ex.getMessage();
        }
        return new FileResponseBlock();
    }
}
