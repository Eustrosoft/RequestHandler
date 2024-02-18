/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.file;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eustrosoft.cms.UserStorage;
import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;
import org.eustrosoft.sam.dao.SamDAO;
import org.eustrosoft.sam.model.User;
import org.eustrosoft.spec.request.BasicRequestBlock;
import org.eustrosoft.spec.response.BasicResponseBlock;
import org.eustrosoft.tools.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;

public class ChunkFileHandler implements BasicHandler {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private final char SEPARATOR = '_';

    private UserStorage storage;

    public ChunkFileHandler(HttpServletResponse response, HttpServletRequest request) {
        this.request = request;
        this.response = response;
    }

    @Override
    public BasicResponseBlock processRequest(BasicRequestBlock requestBlock)
            throws IOException, SQLException {
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(request),
                DBPoolContext.getUrl(request),
                DBPoolContext.getDriverClass(request)
        );
        QDBPSession dbps = dbPool.logon(new QTISSessionCookie(request, response).getCookieValue());
        SamDAO samDao = new SamDAO(dbps.getConnection());
        User user = samDao.getUserById(samDao.getUserId());
        this.storage = UserStorage.getInstanceForUser(user);
        String uploadPath = this.storage.getExistedPathOrCreate();
        String answer = "";

        if (uploadPath != null && !uploadPath.isEmpty()) {
            ChunkFileRequestBlock fileRequestBlock = (ChunkFileRequestBlock) requestBlock;
            byte[] fileBytes = fileRequestBlock.getFileBytes(); // TODO: chunks uploading wit bytes may be used
            String fileString = fileRequestBlock.getFileString();
            String fileName = fileRequestBlock.getFileName();
            Long chunk = fileRequestBlock.getChunkNumber();
            Long allChunks = fileRequestBlock.getChunkCount();
            try (BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(new File(uploadPath, String.format("%s%c%d", fileName, SEPARATOR, chunk))))
            ) {
                bos.write(fileString.getBytes());
                bos.flush();
                if (mergeIfAllChunksCollected(allChunks)) {
                    answer = "File was merged.";
                } else {
                    answer = String.format(
                            "%s %d of %d was uploaded.",
                            fileName,
                            chunk,
                            allChunks
                    );
                }
            } catch (Exception ex) {
                // ex.printStackTrace();
                answer = ex.getMessage();
            }
        }
        return new FileResponseBlock();
    }

    private boolean mergeIfAllChunksCollected(Long chunksCount) throws IOException {
        String path = this.storage.getExistedPathOrCreate();
        File dir = new File(path);
        if (!dir.exists()) {
            throw new FileNotFoundException("The directory for chunks uploading does not exists.");
        }
        File[] chunkFiles = dir.listFiles();
        if (chunkFiles == null) {
            return false;
        }
        if (chunksCount == chunkFiles.length) {
            mergeFiles(chunkFiles);
            this.storage.clearPathOfCurrentStoragePath();
            return true;
        }
        return false;
    }

    private void mergeFiles(File[] filesToMerge) throws IOException {
        int[] chunksPositions = new int[filesToMerge.length];
        String fileNameWithoutIndex = "";
        for (int i = 0; i < filesToMerge.length; i++) {
            String fileName = filesToMerge[i].getName();
            int separatorIndex = fileName.lastIndexOf(SEPARATOR);
            int fileIndex = Integer.parseInt(fileName.substring(separatorIndex + 1));
            fileNameWithoutIndex = fileName.substring(0, separatorIndex);
            chunksPositions[fileIndex] = i;
        }
        File tempFile = new File(filesToMerge[0].getParentFile(), fileNameWithoutIndex);
        tempFile.createNewFile();
        merge(tempFile, filesToMerge, chunksPositions);
    }

    private void merge(File tempFile, File[] filesToMerge, int[] chunksPositions)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(tempFile, true);
        for (int i = 0; i < chunksPositions.length; i++) {
            String str = new String(
                    FileUtils.readFileToByteArray(
                            filesToMerge[chunksPositions[i]]
                    )
            );
            fos.write(Base64.getDecoder().decode(str.getBytes()));
        }
        fos.flush();
        fos.close();
    }
}
