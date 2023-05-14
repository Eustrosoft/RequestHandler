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
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.List;

import static com.eustrosoft.core.Constants.*;
import static com.eustrosoft.core.tools.FileUtils.checkPathInjection;
import static org.apache.commons.io.IOUtils.DEFAULT_BUFFER_SIZE;

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
        CMSResponseBlock cmsResponseBlock = new CMSResponseBlock();
        cmsResponseBlock.setE(0);
        cmsResponseBlock.setErrMsg("Ok.");
        switch (requestType) {
            case REQUEST_VIEW:
                List<CMSObject> directoryObjects = getDirectoryObjects(cmsRequestBlock.getPath());
                cmsResponseBlock.setContent(directoryObjects);
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
                FileTicket downloadPathDetails = getFileTicket(requestBlock);
                cmsResponseBlock.setErrMsg(downloadPathDetails.getTicket());
                break;
            case REQUEST_RENAME:
                rename(cmsRequestBlock.getFrom(), cmsRequestBlock.getTo());
                break;
            case REQUEST_DOWNLOAD:
                download(requestBlock, cmsRequestBlock);
                break;
            default:
                cmsResponseBlock.setE(404);
                cmsResponseBlock.setErrMsg("Not yet implemented.");
                break;
        }
        return cmsResponseBlock;
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

    private FileTicket getFileTicket(RequestBlock requestBlock) throws IOException {
        String session =
                new QTISSessionCookie(requestBlock.getHttpRequest(), requestBlock.getHttpResponse())
                        .getCookieValue();
        this.usersContext = UsersContext.getInstance();
        this.userStorage = UserStorage.getInstanceForUser(usersContext.getSQLUser(session));
        return getDownloadPathDetails(
                ((CMSRequestBlock) requestBlock).getPath(),
                this.userStorage.getExistedPathOrCreate()
        );
    }

    private void download(RequestBlock requestBlock, CMSRequestBlock cmsRequestBlock) throws IOException {
        FileDownloadService fds = FileDownloadService.getInstance();
        DownloadFileDetails fileInfo
                = fds.getFileInfoAndEndConversation(cmsRequestBlock.getTicket());
        HttpServletResponse httpResponse = requestBlock.getHttpResponse();
        httpResponse.reset();
        setContentType(cmsRequestBlock, httpResponse);
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setHeader(
                "Content-Disposition",
                String.format(
                        "attachment; filename=\"%s;\";filename*=utf-8''%s",
                        fileInfo.getFileName(),
                        URLEncoder.encode(fileInfo.getFileName(), "UTF-8")
                )
        );
        httpResponse.setContentLengthLong(fileInfo.getFileLength());
        httpResponse.setBufferSize(DEFAULT_BUFFER_SIZE);
        httpResponse.setHeader("Accept-Ranges", "bytes");
        OutputStream os = httpResponse.getOutputStream();
        try (InputStream inputStream = fileInfo.getInputStream()) {
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) != -1) {
                os.write(buf, 0, bytesRead);
            }
        } finally {
            os.flush();
            os.close();
            // TODO: make delete action
        }
    }

    public boolean move(String from, String to)
            throws Exception {
        if (from == null || from.isEmpty() ||
                to == null || to.isEmpty()) {
            throw new CMSException("The path or name was null or empty");
        }
        return moveFile(
                new File(from).getPath(),
                new File(to).getPath()
        );
    }

    public boolean rename(String source, String name) throws Exception {
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
            throws Exception {
        return this.cmsDataSource.move(from, to);
    }

    private String createDirectory(String path, String name)
            throws Exception {
        return this.cmsDataSource.createDirectory(new File(path, name).getPath());
    }

    private String createFile(String path, String name)
            throws Exception {
        return this.cmsDataSource.createFile(path, name);
    }

    private String createFile(String path, InputStream stream)
            throws Exception {
        return this.cmsDataSource.createFile(path, stream);
    }

    private List<CMSObject> getDirectoryObjects(String path)
            throws Exception {
        return this.cmsDataSource.getContent(path);
    }

    private boolean copyFile(String source, String dist)
            throws Exception {
        return this.cmsDataSource.copy(source, dist);
    }

    private boolean delete(String source)
            throws Exception {
        return this.cmsDataSource.delete(source);
    }

    private void setContentType(CMSRequestBlock cmsRequestBlock, HttpServletResponse httpResponse) {
        String contentType = cmsRequestBlock.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            httpResponse.setContentType(contentType);
        } else {
            httpResponse.setContentType("application/octet-stream");
        }
    }
}
