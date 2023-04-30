package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.context.User;
import com.eustrosoft.core.context.UserStorage;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHandler implements Handler {
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
            ex.printStackTrace();
            answer = ex.getMessage();
        }
        return new FileResponseBlock(answer);
    }
}
