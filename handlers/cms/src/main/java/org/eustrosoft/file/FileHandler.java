/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.file;

import org.eustrosoft.core.handlers.BasicHandler;
import org.eustrosoft.core.handlers.requests.RequestBlock;
import org.eustrosoft.core.handlers.responses.ResponseBlock;
import org.eustrosoft.core.model.user.User;
import org.eustrosoft.core.providers.context.UsersContext;
import org.eustrosoft.core.services.UserStorage;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHandler implements BasicHandler {
    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock)
            throws IOException {
        User user = UsersContext.getInstance()
                .getSQLUser(
                        new QTISSessionCookie(requestBlock.getHttpRequest(), requestBlock.getHttpResponse())
                                .getCookieValue()
                );
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
        return new FileResponseBlock(answer);
    }
}