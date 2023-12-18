/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.model.user.User;
import com.eustrosoft.core.providers.context.UsersContext;
import com.eustrosoft.core.services.UserStorage;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Map;
import java.util.zip.CRC32;

import static com.eustrosoft.core.tools.FileUtils.checkPathInjection;
import static com.eustrosoft.core.tools.FileUtils.getNextIterationFilePath;

public class BytesChunkFileHandler implements Handler {
    public static final int BUF_SIZE = 1 * 1024;
    private UserStorage storage;

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock)
            throws Exception {
        HttpServletRequest request = requestBlock.getHttpRequest();
        BytesChunkFileRequestBlock requestBl = (BytesChunkFileRequestBlock) requestBlock;
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
        if (storagePath.isEmpty()) { // TODO
            storagePath = this.storage.getStoragePath();
        }
        String answer = "";
        String uploadPath = requestBl.getPath();
        checkPathInjection(uploadPath);

        Part part = request.getPart("file");
        String fileName = requestBl.getFileName();
        InputStream inputStream = part.getInputStream();
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
        return new FileResponseBlock(answer);
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
