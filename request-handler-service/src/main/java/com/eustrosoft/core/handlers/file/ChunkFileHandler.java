package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.context.User;
import com.eustrosoft.core.context.UserStorage;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChunkFileHandler implements Handler {
    private UserStorage storage;

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock, HttpServletRequest request)
            throws IOException {
        User user = UsersContext.getInstance().getSQLUser(request.getSession(false).getId());
        this.storage = new UserStorage(user);
        String uploadPath = this.storage.getExistedPathOrCreate();
        String answer = "";

        if (uploadPath != null && !uploadPath.isEmpty()) {
            ChunkFileRequestBlock fileRequestBlock = (ChunkFileRequestBlock) requestBlock;
            byte[] fileBytes = fileRequestBlock.getFileBytes();
            String fileName = fileRequestBlock.getFileName();
            Long chunk = fileRequestBlock.getChunkNumber();
            try (BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(new File(uploadPath, String.format("%s_%d", fileName, chunk))))
            ) {
                bos.write(fileBytes);
                bos.flush();
                answer = String.format(
                        "%s %d of %d was uploaded.",
                        fileName,
                        fileRequestBlock.getChunkNumber(),
                        fileRequestBlock.getChunkCount()
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                answer = ex.getMessage();
            }
        }
        return new FileResponseBlock(answer);
    }
}
