package com.eustrosoft.core.handlers.cms;

import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.datasource.exception.CMSException;
import com.eustrosoft.datasource.sources.CMSDataSource;
import com.eustrosoft.datasource.sources.model.CMSObject;
import com.eustrosoft.datasource.sources.model.CMSType;
import com.eustrosoft.filedatasource.FileCMSDataSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.eustrosoft.core.Constants.*;

public final class CMSHandler implements Handler {
    private CMSDataSource cmsDataSource;
    private String requestType;

    public CMSHandler(String requestType) throws Exception {
        this.requestType = requestType;
        this.cmsDataSource = new FileCMSDataSource();
    }

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        CMSRequestBlock cmsRequestBlock = (CMSRequestBlock) requestBlock;
        CMSResponseBlock responseBlock = new CMSResponseBlock();
        responseBlock.setE(0);
        responseBlock.setErrMsg("Ok.");
        switch (requestType) {
            case REQUEST_VIEW:
                List<CMSObject> directoryObjects = getDirectoryObjects(cmsRequestBlock.getPath());
                responseBlock.setContent(directoryObjects);
                break;
            case REQUEST_CREATE:
                if (CMSType.FILE.equals(cmsRequestBlock.getType())) {
                    createFile(
                            cmsRequestBlock.getPath(),
                            cmsRequestBlock.getFileName()
                    );
                }
                if (CMSType.DIRECTORY.equals(cmsRequestBlock.getType())) {
                    createDirectory(
                            cmsRequestBlock.getPath(),
                            cmsRequestBlock.getFileName()
                    );
                }
                break;
            case REQUEST_COPY:
                copyFile(cmsRequestBlock.getFrom(), cmsRequestBlock.getTo());
                break;
            case REQUEST_MOVE:
                move(cmsRequestBlock.getFrom(), cmsRequestBlock.getTo());
                break;
            case REQUEST_DELETE:
                delete(cmsRequestBlock.getPath());
                break;
            default:
                responseBlock.setE(404);
                responseBlock.setErrMsg("Not yet implemented.");
                break;
        }
        return responseBlock;
    }

//    @SneakyThrows
//    public FileTicket getDownloadPathDetails(String filePath) {
//        if (filePath == null || filePath.isEmpty()) {
//            checkPathInjection(filePath);
//        }
//        File file = new File(rootPath, filePath);
//        if (!file.exists()) {
//            throw new CMSException("File is not exist.");
//        }
//        InputStream inputStream;
//        String fileName;
//        if (file.isDirectory()) {
//            File temporaryFolder = temporaryStorageService.getTemporaryFolder();
//            folderDAO.copyDirectory(
//                    filePath,
//                    temporaryFolder.getAbsolutePath().substring(rootPath.length())
//            );
//            String zipFileName = String.format("archive_%d.zip", System.currentTimeMillis());
//            Path path = zipService.createZipOfDirectory(
//                    zipFileName,
//                    temporaryFolder.getAbsolutePath(),
//                    temporaryFolder.getAbsolutePath()
//            );
//            fileName = zipFileName;
//            inputStream = new FileInputStream(path.toFile());
//        } else if (file.isFile()) {
//            inputStream = new FileInputStream(file);
//            fileName = file.getName();
//        } else {
//            throw new CMSException("File type is not recognized");
//        }
//        return fileDownloadService.beginConversation(
//                new DownloadFileDetails(
//                        inputStream,
//                        fileName
//                )
//        );
//    }

    private void checkPathInjection(String... params) throws CMSException {
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                throw new CMSException("Param was null or empty.");
            }
            if (param.contains("..")) {
                throw new CMSException("Path Injection Detected.");
            }
        }
    }

    public boolean move(String from, String to)
            throws CMSException, IOException {
        if (from == null || from.isEmpty() ||
                to == null || to.isEmpty()) {
            throw new CMSException("The path or name was null or empty");
        }
        return moveFile(
                new File(from).getPath(),
                new File(to).getPath()
        );
    }

    public boolean rename(String source, String name) throws IOException, CMSException {
        return moveFile(
                        source,
                        new File(getProcessedParentPath(source), name)
                                .getPath()
                );
    }

    private String getProcessedParentPath(String path) {
        File parentFile = new File(path).getParentFile();
        return parentFile.getPath();
    }

    private boolean moveFile(String from, String to)
            throws CMSException, IOException {
        return this.cmsDataSource.move(from, to);
    }

    private String createDirectory(String path, String name)
            throws CMSException {
        return this.cmsDataSource.createDirectory(new File(path, name).getPath());
    }

    private String createFile(String path, String name)
            throws CMSException, IOException {
        return this.cmsDataSource.createFile(path, name);
    }

    private String createFile(String path, InputStream stream)
            throws CMSException, IOException {
        return this.cmsDataSource.createFile(path, stream);
    }

    private List<CMSObject> getDirectoryObjects(String path)
            throws CMSException, IOException {
        return this.cmsDataSource.getContent(path);
    }

    private boolean copyFile(String source, String dist)
            throws IOException, CMSException {
        return this.cmsDataSource.copy(source, dist);
    }

    private boolean delete(String source)
            throws IOException, CMSException {
        return this.cmsDataSource.delete(source);
    }
}
