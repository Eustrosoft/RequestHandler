package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.eustrosoft.core.filter.Constants.PROPERTY_UPLOAD_FILE;
import static com.eustrosoft.core.filter.Constants.SYSTEM_FILE_NAME;

public class FileHandler implements Handler {
    private static final Properties systemProperties = new Properties();
    private String uploadFilePath = null;

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        setUploadFilePath();
        FileRequestBlock fileRequestBlock = (FileRequestBlock) requestBlock;
        byte[] fileBytes = fileRequestBlock.getFileBytes();
        String fileName = fileRequestBlock.getFileName();
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(new File(uploadFilePath, fileName)))
        ) {
            bos.write(fileBytes);
            bos.flush();
        }
        return null;
    }

    private void setUploadFilePath() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(SYSTEM_FILE_NAME)) {
            systemProperties.load(input);
            this.uploadFilePath = systemProperties.getProperty(PROPERTY_UPLOAD_FILE); // TODO externalize to provider
        } catch (IOException ex) {

        }
    }
}
