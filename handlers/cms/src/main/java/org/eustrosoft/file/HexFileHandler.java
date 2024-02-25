/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.file;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eustrosoft.cms.dbdatasource.CMSDataSource;
import org.eustrosoft.cms.dbdatasource.DBDataSource;
import org.eustrosoft.cms.dbdatasource.DataSourceProvider;
import org.eustrosoft.cms.dbdatasource.FileCMSDataSource;
import org.eustrosoft.cms.dbdatasource.UserStorage;
import org.eustrosoft.constants.Constants;
import org.eustrosoft.core.db.model.User;
import org.eustrosoft.core.interfaces.BasicHandler;
import org.eustrosoft.core.request.RequestBlock;
import org.eustrosoft.core.response.ResponseBlock;
import org.eustrosoft.handlers.cms.dto.HexFileParams;
import org.eustrosoft.handlers.cms.dto.HexFileResult;
import org.eustrosoft.providers.SessionProvider;
import org.eustrosoft.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;
import org.eustrosoft.sam.dao.SamDAO;
import org.eustrosoft.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class HexFileHandler implements BasicHandler {
    private CMSDataSource cmsDataSource;
    private UserStorage storage;
    private HttpServletRequest request;
    private HttpServletResponse response;


    public HexFileHandler(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public synchronized ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        HexFileRequestBlock requestBl = (HexFileRequestBlock) requestBlock;
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(request),
                DBPoolContext.getUrl(request),
                DBPoolContext.getDriverClass(request)
        );
        QDBPSession dbps = dbPool.logon(new QTISSessionCookie(request, response).getCookieValue());
        SamDAO samDao = new SamDAO(dbps.getConnection());
        User user = samDao.getUserById(samDao.getUserId());
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
                        new SessionProvider(request, response).getSession().getConnection()
                ).getDataSource();

        String answer = "";
        String uploadPath = requestBl.getPath();
        FileUtils.checkPathInjection(uploadPath);

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
                path = FileUtils.getNextIterationFilePath(
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
                        Constants.CHUNK_SIZE
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
        return new FileResponseBlock();
    }
}
