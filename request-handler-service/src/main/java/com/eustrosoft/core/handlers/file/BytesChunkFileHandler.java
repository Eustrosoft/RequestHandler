package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.context.User;
import com.eustrosoft.core.context.UserStorage;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.datasource.exception.CMSException;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.eustrosoft.core.tools.FileUtils.checkPathInjection;

public class BytesChunkFileHandler implements Handler {
    public static final int BUF_SIZE = 2 * 1024;
    private UserStorage storage;

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock)
            throws IOException, ServletException, CMSException {
        HttpServletRequest request = requestBlock.getHttpRequest();
        BytesChunkFileRequestBlock requestBl = (BytesChunkFileRequestBlock) requestBlock;
        User user = UsersContext.getInstance()
                .getSQLUser(
                        new QTISSessionCookie(requestBlock.getHttpRequest(), requestBlock.getHttpResponse())
                                .getCookieValue()
                );
        this.storage = UserStorage.getInstanceForUser(user);
        String storagePath = this.storage.getStoragePath();
        if (storagePath == null || storagePath.isEmpty()) {
            throw new IOException("Storage path is not defined for this user.");
        }
        String answer = "";
        String uploadPath = requestBl.getPath();
        checkPathInjection(uploadPath);

        Part part = request.getPart("file");
        String fileName = requestBl.getFileName();
        InputStream inputStream = part.getInputStream();
        saveUploadFile(inputStream, new File(
                        new File(storagePath, uploadPath),
                        fileName
                )
        );
        answer = String.format(
                "Part was uploaded in %s path with file name %s.",
                uploadPath,
                fileName
        );
        if (requestBl.getChunkNumber().equals(requestBl.getChunkCount() - 1)) {
            this.storage.clearChunksOfCurrentPath();
            this.storage.clearPathOfCurrentStoragePath();
        }
        return new FileResponseBlock(answer);
    }

    private void saveUploadFile(InputStream input, File dst) {
        OutputStream out = null;
        try {
            if (dst.exists()) {
                out = new BufferedOutputStream(new FileOutputStream(dst, true), BUF_SIZE);
            } else {
                out = new BufferedOutputStream(new FileOutputStream(dst), BUF_SIZE);
            }
            byte[] buffer = new byte[BUF_SIZE];
            int len;
            while ((len = input.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
