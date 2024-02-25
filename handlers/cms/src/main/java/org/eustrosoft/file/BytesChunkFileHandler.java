/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.file;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.eustrosoft.cms.dbdatasource.UserStorage;
import org.eustrosoft.core.db.model.User;
import org.eustrosoft.core.interfaces.BasicHandler;
import org.eustrosoft.core.request.RequestBlock;
import org.eustrosoft.core.response.ResponseBlock;
import org.eustrosoft.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;
import org.eustrosoft.sam.dao.SamDAO;
import org.eustrosoft.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Map;
import java.util.zip.CRC32;

public class BytesChunkFileHandler implements BasicHandler {
    public static final int BUF_SIZE = 1 * 1024;
    private UserStorage storage;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public BytesChunkFileHandler(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock)
            throws Exception {
        BytesChunkFileRequestBlock requestBl = (BytesChunkFileRequestBlock) requestBlock;
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
        if (storagePath.isEmpty()) { // TODO
            storagePath = this.storage.getStoragePath();
        }
        String answer = "";
        String uploadPath = requestBl.getPath();
        FileUtils.checkPathInjection(uploadPath);

        Part part = request.getPart("file");
        String fileName = requestBl.getFileName();
        InputStream inputStream = part.getInputStream();
        Map<String, String> userPaths = this.storage.getUserPaths();
        String filePath = userPaths.get(fileName);
        if (filePath == null || filePath.isEmpty()) {
            filePath = FileUtils.getNextIterationFilePath(
                    new File(storagePath, uploadPath).getAbsolutePath(),
                    fileName
            );
            userPaths.put(fileName, filePath);
        }
        if (filePath.isEmpty()) {
            throw new IOException("File path was not specified for this user.");
        }
        saveUploadFile(
                inputStream,
                new File(filePath),
                requestBl.getFileHash()
        );
        answer = String.format(
                "Part was uploaded in %s path with file name %s.",
                uploadPath,
                fileName
        );
        if (requestBl.getChunkNumber().equals(requestBl.getChunkCount() - 1)) {
            this.storage.clearChunksOfCurrentPath();
            this.storage.clearPathOfCurrentStoragePath();
            userPaths.remove(fileName);
        }
        return new FileResponseBlock();
    }

    private synchronized void saveUploadFile(InputStream input, File dst, String fileHash) throws Exception {
        if (fileHash == null || fileHash.isEmpty()) {
            throw new Exception("Hash code not found in request.");
        }
        RandomAccessFile stream = new RandomAccessFile(dst, "rw");
        FileChannel channel = stream.getChannel();
        FileLock lock = null;
        CRC32 crc32 = new CRC32();
        byte[] buffer = new byte[BUF_SIZE];
        int len;
        try {
            lock = channel.tryLock();
            while ((len = input.read(buffer)) > 0) {
                crc32.update(buffer, 0, len);
                stream.seek(dst.length());
                stream.write(buffer, 0, len);
            }
            String value = String.format("%x", crc32.getValue());
            if (!fileHash.contains(value) && !value.contains(fileHash)) {
                dst.delete();
                throw new Exception("Hash code did not match.");
            }
        } catch (Exception e) {
            stream.close();
            channel.close();
            // e.printStackTrace();
        } finally {
            if (null != lock) {
                lock.release();
            }
            if (null != input) {
                try {
                    input.close();
                } catch (IOException e) {
                    // e.printStackTrace();
                }
            }
            stream.close();
            channel.close();
        }
    }
}
