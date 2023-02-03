package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.context.User;
import com.eustrosoft.core.context.UserStorage;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.tools.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ChunkFileHandler implements Handler {
    private final char SEPARATOR = '_';

    private UserStorage storage;

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock)
            throws IOException {
        User user = UsersContext.getInstance().getSQLUser(requestBlock.getHttpRequest().getSession(false).getId());
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
                ex.printStackTrace();
                answer = ex.getMessage();
            }
        }
        return new FileResponseBlock(answer);
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