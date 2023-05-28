package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.context.User;
import com.eustrosoft.core.context.UserStorage;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.providers.DataSourceProvider;
import com.eustrosoft.core.providers.SessionProvider;
import com.eustrosoft.datasource.sources.CMSDataSource;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import static com.eustrosoft.core.tools.FileUtils.checkPathInjection;
import static com.eustrosoft.core.tools.FileUtils.getNextIterationFilePath;

public class HexFileHandler implements Handler {
    private CMSDataSource cmsDataSource;
    private UserStorage storage;

    @Override
    public synchronized ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        HttpServletRequest request = requestBlock.getHttpRequest();
        HexFileRequestBlock requestBl = (HexFileRequestBlock) requestBlock;
        User user = UsersContext.getInstance()
                .getSQLUser(
                        new QTISSessionCookie(requestBlock.getHttpRequest(), requestBlock.getHttpResponse())
                                .getCookieValue()
                );
        this.storage = UserStorage.getInstanceForUser(user);
        String storagePath = this.storage.getBaseUploadPath();
        if (storagePath == null) {
            throw new IOException("Storage path is not defined for this user.");
        }
        if (storagePath.isEmpty()) {
            storagePath = this.storage.getStoragePath();
        }
        String answer = "";
        String uploadPath = requestBl.getPath();
        checkPathInjection(uploadPath);

        String fileName = requestBl.getFileName();
        String hexString = requestBl.getHexString();
        Map<String, String> userPaths = this.storage.getUserPaths();
        String filePath = userPaths.get(fileName);
        if (filePath == null || filePath.isEmpty()) {
            filePath = getNextIterationFilePath(
                    new File(storagePath, uploadPath).getAbsolutePath(),
                    fileName
            );
            userPaths.put(fileName, filePath);
        }
        if (filePath.isEmpty()) {
            throw new IOException("File path was not specified for this user.");
        }
        this.cmsDataSource = DataSourceProvider
                .getInstance(
                        new SessionProvider(
                                request,
                                requestBlock.getHttpResponse()
                        ).getSession().getConnection()
                ).getDataSource();
        String uploadedFile = this.cmsDataSource.createFileHex(
                filePath,
                fileName,
                hexString,
                requestBl.getFileHash()
        );
        answer = String.format(
                "Part was uploaded in %s path with file name %s.",
                uploadPath,
                uploadedFile
        );
        if (requestBl.getChunkNumber().equals(requestBl.getChunkCount() - 1)) {
            this.storage.clearChunksOfCurrentPath();
            this.storage.clearPathOfCurrentStoragePath();
            userPaths.remove(fileName);
        }
        return new FileResponseBlock(answer);
    }
}
