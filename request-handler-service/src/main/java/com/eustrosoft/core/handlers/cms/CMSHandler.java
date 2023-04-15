package com.eustrosoft.core.handlers.cms;

import com.eustrosoft.core.context.UserStorage;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.tools.FileDownloadService;
import com.eustrosoft.core.tools.ZipService;
import com.eustrosoft.datasource.exception.CMSException;
import com.eustrosoft.datasource.sources.CMSDataSource;
import com.eustrosoft.datasource.sources.model.CMSObject;
import com.eustrosoft.datasource.sources.model.CMSType;
import com.eustrosoft.filedatasource.FileCMSDataSource;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

import static com.eustrosoft.core.Constants.REQUEST_COPY;
import static com.eustrosoft.core.Constants.REQUEST_CREATE;
import static com.eustrosoft.core.Constants.REQUEST_DELETE;
import static com.eustrosoft.core.Constants.REQUEST_DOWNLOAD;
import static com.eustrosoft.core.Constants.REQUEST_MOVE;
import static com.eustrosoft.core.Constants.REQUEST_TICKET;
import static com.eustrosoft.core.Constants.REQUEST_VIEW;

public final class CMSHandler implements Handler {
    private CMSDataSource cmsDataSource;
    private String requestType;
    private UserStorage userStorage;
    private UsersContext usersContext;

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
            case REQUEST_TICKET:
                String session = requestBlock.getHttpRequest().getSession(false).getId();
                this.usersContext = UsersContext.getInstance();
                this.userStorage = UserStorage.getInstanceForUser(usersContext.getSQLUser(session));
                FileTicket downloadPathDetails = getDownloadPathDetails(
                        ((CMSRequestBlock) requestBlock).getPath(),
                        this.userStorage.getExistedPathOrCreate()
                );
                responseBlock.setErrMsg(downloadPathDetails.getTicket());
                break;
            case REQUEST_DOWNLOAD:
                FileDownloadService fds = FileDownloadService.getInstance();
                DownloadFileDetails fileInfo
                        = fds.getFileInfoAndEndConversation(((CMSRequestBlock) requestBlock).getTicket());
                HttpServletResponse httpResponse = requestBlock.getHttpResponse();
                httpResponse.setContentType("application/octet-stream");
                httpResponse.setHeader(
                        "Content-Disposition",
                        String.format(
                                "attachment; filename=%s",
                                fileInfo.getFileName()
                        )
                );
                httpResponse.setContentLengthLong(fileInfo.getFileLength());
                OutputStream os = httpResponse.getOutputStream();
                try (InputStream inputStream = fileInfo.getInputStream()) {
                    byte[] buf = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buf)) != -1) {
                        os.write(buf, 0, bytesRead);
                    }
                }
                finally {
                    os.flush();
                    os.close();
                    // TODO: make delete action
                }
                break;
            default:
                responseBlock.setE(404);
                responseBlock.setErrMsg("Not yet implemented.");
                break;
        }
        return responseBlock;
    }

    @SneakyThrows
    public FileTicket getDownloadPathDetails(String pathToDownload, String userDir) {
        checkPathInjection(pathToDownload);
        File file = new File(this.cmsDataSource.getFullPath(pathToDownload));
        if (!file.exists()) {
            throw new CMSException("File is not exist.");
        }
        InputStream inputStream;
        String fileName;
        if (file.isDirectory()) {
            File temporaryFolder = new File(userDir);
            FileUtils.copyDirectory(file, new File(temporaryFolder.getAbsolutePath(), file.getName()));
            String zipFileName = String.format("archive_%d.zip", System.currentTimeMillis());
            Path path = ZipService.createZipOfDirectory(
                    zipFileName,
                    temporaryFolder.getAbsolutePath(),
                    temporaryFolder.getAbsolutePath()
            );
            fileName = zipFileName;
            inputStream = new FileInputStream(path.toFile());
        } else if (file.isFile()) {
            inputStream = new FileInputStream(file);
            fileName = file.getName();
        } else {
            throw new CMSException("File type is not recognized");
        }
        return FileDownloadService.getInstance().beginConversation(
                new DownloadFileDetails(
                        inputStream,
                        fileName,
                        file.length()
                )
        );
    }

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
