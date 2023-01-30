package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.context.User;
import com.eustrosoft.core.context.UserStorage;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ChunkFileHandler implements Handler {
    private final char SEPARATOR = '_';

    private UserStorage storage;

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock, HttpServletRequest request)
            throws IOException {
        User user = UsersContext.getInstance().getSQLUser(request.getSession(false).getId());
        this.storage = UserStorage.getInstanceForUser(user);
        String uploadPath = this.storage.getExistedPathOrCreate();
        String answer = "";

        if (uploadPath != null && !uploadPath.isEmpty()) {
            ChunkFileRequestBlock fileRequestBlock = (ChunkFileRequestBlock) requestBlock;
            byte[] fileBytes = fileRequestBlock.getFileBytes();
            String fileName = fileRequestBlock.getFileName();
            Long chunk = fileRequestBlock.getChunkNumber();
            Long allChunks = fileRequestBlock.getChunkCount();
            try (BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(new File(uploadPath, String.format("%s%c%d", fileName, SEPARATOR, chunk))))
            ) {
                bos.write(fileBytes);
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
            return true;
        }
        return false;
    }

    private void mergeFiles(File[] filesToMerge) throws IOException {
        int[] chunksPositions = new int[filesToMerge.length];
        for (int i = 0; i < filesToMerge.length; i++) {
            String fileName = filesToMerge[i].getName();
            int separatorIndex = fileName.lastIndexOf(SEPARATOR);
            int fileIndex = Integer.parseInt(fileName.substring(separatorIndex + 1));
            chunksPositions[i] = fileIndex;
        }
        File file = filesToMerge[chunksPositions[0]];
        for (int i = 1; i < chunksPositions.length; i++) {
            File mergedFile = filesToMerge[chunksPositions[i]];
            merge(file.getAbsolutePath(), mergedFile.getAbsolutePath());
        }
    }

    private void merge(String firstFile, String secondFile)
            throws IOException {
        PrintWriter pw = new PrintWriter(firstFile);
        BufferedReader br = new BufferedReader(new FileReader(firstFile));
        String line = br.readLine();
        while (line != null) {
            pw.println(line);
            line = br.readLine();
        }
        br = new BufferedReader(new FileReader(secondFile));
        line = br.readLine();
        while (line != null) {
            pw.println(line);
            line = br.readLine();
        }
        pw.flush();
        br.close();
        pw.close();
    }
}
