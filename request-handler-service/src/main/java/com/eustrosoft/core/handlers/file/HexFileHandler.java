/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.file;

import com.eustrosoft.cms.CMSDataSource;
import com.eustrosoft.cms.dbdatasource.DBDataSource;
import com.eustrosoft.cms.filedatasource.FileCMSDataSource;
import com.eustrosoft.cms.parameters.HexFileParams;
import com.eustrosoft.cms.parameters.HexFileResult;
import com.eustrosoft.cms.providers.DataSourceProvider;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.model.user.User;
import com.eustrosoft.core.providers.SessionProvider;
import com.eustrosoft.core.providers.context.UsersContext;
import com.eustrosoft.core.services.UserStorage;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import static com.eustrosoft.core.constants.Constants.CHUNK_SIZE;
import static com.eustrosoft.core.handlers.cms.CMSHandler.VIRTUAL_SUBSYSTEM;
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
        this.cmsDataSource = DataSourceProvider
                .getInstance(
                        new SessionProvider(
                                request,
                                requestBlock.getHttpResponse()
                        ).getSession().getConnection()
                ).getDataSource();

        String answer = "";
        String originPath = requestBl.getPath();
        String uploadPath = originPath.substring(VIRTUAL_SUBSYSTEM.length());
        checkPathInjection(uploadPath);

        String fileName = requestBl.getFileName();
        String hexString = requestBl.getHexString();
        Map<String, HexFileResult> userPaths = this.storage.getUserHexUploads();
        HexFileResult fileResult = userPaths.get(uploadPath);
        String recordId = null;
        String recordVer = null;
        String filePid = null;
        String filePath = null;
        if (fileResult == null || fileResult.isEmpty()) {
            String path = null;
            if (cmsDataSource instanceof FileCMSDataSource) {
                path = getNextIterationFilePath(
                        new File(storagePath, uploadPath).getAbsolutePath(),
                        fileName
                );
            }
            if (cmsDataSource instanceof DBDataSource) {
                path = uploadPath;
            }
            if (path == null) {
                throw new Exception("Path was not found for file.");
            }
            fileResult = new HexFileResult("", "", "", path);
            userPaths.put(uploadPath, fileResult);
            filePath = path;
        } else {
            recordId = fileResult.getRecordId();
            recordVer = fileResult.getRecordVer();
            filePid = fileResult.getFilePid();
            filePath = fileResult.getFilePath();
        }
        if (fileResult.isEmpty()) {
            throw new IOException("File path was not specified for this user.");
        }
        if (requestBl.getChunkNumber() == 0) {
            recordId = fileName;
        }
        HexFileResult result = this.cmsDataSource.createFileHex(
                new HexFileParams(
                        filePath,
                        recordId,
                        recordVer,
                        filePid,
                        hexString,
                        requestBl.getFileHash(),
                        requestBl.getDescription(),
                        requestBl.getSecurityLevel(),
                        requestBl.getChunkNumber(),
                        requestBl.getChunkCount(),
                        CHUNK_SIZE
                )
        );
        fileResult.setFilePid(result.getFilePid());
        fileResult.setRecordId(result.getRecordId());
        fileResult.setRecordVer(result.getRecordVer());
        fileResult.setFilePath(result.getFilePath());
        answer = String.format(
                "Part %d was uploaded in %s path with file name %s.",
                requestBl.getChunkNumber(),
                uploadPath,
                fileName
        );
        if (requestBl.getChunkNumber().equals(requestBl.getChunkCount() - 1)) {
            this.storage.clearChunksOfCurrentPath();
            this.storage.clearPathOfCurrentStoragePath();
            userPaths.remove(uploadPath);
        }
        return new FileResponseBlock(answer);
    }
}
