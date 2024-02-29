/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.handlers.cms;

import com.eustrosoft.cms.CMSDataSource;
import com.eustrosoft.cms.CMSType;
import com.eustrosoft.cms.dbdatasource.DBDataSource;
import com.eustrosoft.cms.dto.CMSGeneralObject;
import com.eustrosoft.cms.dto.CMSObject;
import com.eustrosoft.cms.exception.CMSException;
import com.eustrosoft.cms.parameters.CMSObjectUpdateParameters;
import com.eustrosoft.cms.providers.DataSourceProvider;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import com.eustrosoft.core.providers.SessionProvider;
import com.eustrosoft.core.providers.context.UsersContext;
import com.eustrosoft.core.services.FileDownloadService;
import com.eustrosoft.core.services.UserStorage;
import com.eustrosoft.core.services.ZipService;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.eustrosoft.core.constants.Constants.REQUEST_COPY;
import static com.eustrosoft.core.constants.Constants.REQUEST_CREATE;
import static com.eustrosoft.core.constants.Constants.REQUEST_DELETE;
import static com.eustrosoft.core.constants.Constants.REQUEST_DOWNLOAD;
import static com.eustrosoft.core.constants.Constants.REQUEST_MOVE;
import static com.eustrosoft.core.constants.Constants.REQUEST_RENAME;
import static com.eustrosoft.core.constants.Constants.REQUEST_TICKET;
import static com.eustrosoft.core.constants.Constants.REQUEST_VIEW;
import static com.eustrosoft.core.constants.DBConstants.SEPARATOR;
import static com.eustrosoft.core.tools.FileUtils.checkPathInjection;
import static org.apache.commons.io.IOUtils.DEFAULT_BUFFER_SIZE;

public final class CMSHandler implements Handler {
    public static final String VIRTUAL_SUBSYSTEM = "/s";

    private CMSDataSource cmsDataSource;
    private UserStorage userStorage;
    private UsersContext usersContext;

    @Override
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        QDBPSession session = new SessionProvider(requestBlock.getHttpRequest(), requestBlock.getHttpResponse())
                .getSession();
        this.cmsDataSource = DataSourceProvider.getInstance(session.getConnection())
                .getDataSource();

        CMSRequestBlock cmsRequestBlock = (CMSRequestBlock) requestBlock;
        CMSResponseBlock cmsResponseBlock = new CMSResponseBlock();
        cmsResponseBlock.setE(0);
        cmsResponseBlock.setErrMsg("Ok.");
        String original = cmsRequestBlock.getPath();
        String path = postProcessPath(original);
        String from = postProcessPath(cmsRequestBlock.getFrom());
        String to = postProcessPath(cmsRequestBlock.getTo());
        String requestType = cmsRequestBlock.getR();
        switch (requestType) {
            case REQUEST_VIEW:
                if (original == null || original.isEmpty() || original.equalsIgnoreCase(SEPARATOR)) {
                    cmsResponseBlock.setContent(getVirtualSubsystem());
                } else {
                    List<CMSObject> directoryObjects = getDirectoryObjects(path);
                    cmsResponseBlock.setContent(directoryObjects);
                }
                break;
            case REQUEST_CREATE:
                if (CMSType.FILE.equals(cmsRequestBlock.getType())) {
                    createFile(
                            path,
                            cmsRequestBlock.getFileName()
                    );
                }
                if (CMSType.DIRECTORY.equals(cmsRequestBlock.getType())) {
                    createDirectory(
                            path,
                            cmsRequestBlock.getFileName(),
                            cmsRequestBlock.getDescription(),
                            cmsRequestBlock.getSecurityLevel()
                    );
                }
                break;
            case REQUEST_COPY:
                copyFile(from, to);
                break;
            case REQUEST_MOVE:
                if (from != null && !from.isEmpty()) {
                    move(from, to);
                }
                if (cmsDataSource instanceof DBDataSource) {
                    CMSObjectUpdateParameters data = new CMSObjectUpdateParameters(
                            to, cmsRequestBlock.getDescription()
                    );
                    this.cmsDataSource.update(to, data);
                }
                break;
            case REQUEST_DELETE:
                delete(path);
                break;
            case REQUEST_TICKET:
                if (cmsDataSource instanceof DBDataSource) {
                    throw new Exception("This functionality is not implemented for database.");
                }
                FileTicket downloadPathDetails = getFileTicket(requestBlock);
                cmsResponseBlock.setErrMsg(downloadPathDetails.getTicket());
                break;
            case REQUEST_RENAME:
                rename(from, new File(to).getName());
                break;
            case REQUEST_DOWNLOAD:
                if (cmsDataSource instanceof DBDataSource) {
                    throw new Exception("This functionality is not implemented for database.");
                }
                download(requestBlock, cmsRequestBlock);
                break;
            default:
                cmsResponseBlock.setE(404);
                cmsResponseBlock.setErrMsg("Not yet implemented.");
                break;
        }
        return cmsResponseBlock;
    }

    private List<CMSObject> getVirtualSubsystem() {
        CMSGeneralObject directory = new CMSGeneralObject(
                null, "s", VIRTUAL_SUBSYSTEM, null, 0L,
                null, null, CMSType.DIRECTORY, null
        );
        List<CMSObject> virtualDirectory = new ArrayList<>();
        virtualDirectory.add(directory);
        return virtualDirectory;
    }

    // TODO: only for file data source
    @SneakyThrows
    public FileTicket getDownloadPathDetails(String pathToDownload, String userDir) {
        checkPathInjection(pathToDownload);
        File file = new File(this.cmsDataSource.getFullPath(pathToDownload));
        if (!file.exists()) {
            throw new CMSException("File is not exist.");
        }
        InputStream inputStream;
        String filePath;
        if (file.isDirectory()) {
            File temporaryFolder = new File(userDir);
            FileUtils.copyDirectory(file, new File(temporaryFolder.getAbsolutePath(), file.getName()));
            String zipFileName = String.format("archive_%d.zip", System.currentTimeMillis());
            Path path = ZipService.createZipOfDirectory(
                    zipFileName,
                    temporaryFolder.getAbsolutePath(),
                    temporaryFolder.getAbsolutePath()
            );
            filePath = path.toString();
            inputStream = new FileInputStream(path.toFile());
        } else if (file.isFile()) {
            inputStream = new FileInputStream(file);
            filePath = file.getName();
        } else {
            throw new CMSException("File type is not recognized");
        }
        return FileDownloadService.getInstance().beginConversation(
                new DownloadFileDetails(
                        inputStream,
                        filePath,
                        file.length()
                )
        );
    }

    private String postProcessPath(String path) {
        if (path == null) {
            return SEPARATOR; // return root if null
        }
        path = path.replaceAll("\\\\", SEPARATOR);
        if (path.startsWith(VIRTUAL_SUBSYSTEM)) {
            path = path.substring(VIRTUAL_SUBSYSTEM.length());
        }
        if (path.isEmpty()) {
            return SEPARATOR;
        }
        return path;
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

    private void download(RequestBlock requestBlock, CMSRequestBlock cmsRequestBlock) throws Exception {
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
                        "attachment; filename=\"%s\";;filename*=utf-8''%s",
                        fileInfo.getFileName(),
                        URLEncoder.encode(fileInfo.getFileName(), "UTF-8")
                )
        );
        httpResponse.setContentLengthLong(fileInfo.getFileLength());
        httpResponse.setBufferSize(DEFAULT_BUFFER_SIZE);
        httpResponse.setHeader("Accept-Ranges", "bytes");
        OutputStream os = httpResponse.getOutputStream();
        // todo: create getFileStream realization
        this.cmsDataSource.uploadToStream(fileInfo.getInputStream(), os);
        // todo: make file deletion
    }

    private boolean move(String from, String to)
            throws Exception {
        if (from == null || from.isEmpty() ||
                to == null || to.isEmpty()) {
            throw new CMSException("The path or name was null or empty");
        }
        return moveFile(
                postProcessPath(new File(from).getPath()),
                postProcessPath(new File(to).getPath())
        );
    }

    private boolean rename(String source, String name) throws Exception {
        return moveFile(
                source,
                postProcessPath(
                        new File(getProcessedParentPath(source), name).getPath()
                )
        );
    }

    private String getProcessedParentPath(String path) {
        File parentFile = new File(path).getParentFile();
        return postProcessPath(parentFile.getPath());
    }

    private boolean moveFile(String from, String to)
            throws Exception {
        return this.cmsDataSource.move(from, to);
    }

    private String createDirectory(String path, String name, String description, Integer securityLevel)
            throws Exception {
        return this.cmsDataSource.createDirectory(
                postProcessPath(new File(path, name).getPath()),
                description,
                securityLevel
        );
    }

    private String createFile(String path, String name)
            throws Exception {
        return this.cmsDataSource.createFile(path, name);
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

    private String getProcessedIdsPath(String path) throws Exception {
        return this.cmsDataSource.getFullPath(path);
    }

    // todo: remove duplicate
    private void setContentType(CMSRequestBlock cmsRequestBlock, HttpServletResponse httpResponse) {
        String contentType = cmsRequestBlock.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            httpResponse.setContentType(contentType);
        } else {
            httpResponse.setContentType("application/octet-stream");
        }
    }
}
