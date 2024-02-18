/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.eustrosoft.cms.dbdatasource.DBDataSource;
import org.eustrosoft.cms.dto.CMSObject;
import org.eustrosoft.cms.exception.CMSException;
import org.eustrosoft.cms.filedownload.DownloadFileDetails;
import org.eustrosoft.cms.filedownload.FileDownloadService;
import org.eustrosoft.cms.filedownload.FileTicket;
import org.eustrosoft.cms.parameters.CMSObjectUpdateParameters;
import org.eustrosoft.cms.providers.DataSourceProvider;
import org.eustrosoft.core.BasicHandler;
import org.eustrosoft.core.RequestContextHolder;
import org.eustrosoft.core.annotation.Handler;
import org.eustrosoft.providers.SessionProvider;
import org.eustrosoft.providers.context.DBPoolContext;
import org.eustrosoft.qdbp.QDBPSession;
import org.eustrosoft.qdbp.QDBPool;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;
import org.eustrosoft.sam.dao.SamDAO;
import org.eustrosoft.services.ZipService;
import org.eustrosoft.spec.Constants;
import org.eustrosoft.spec.interfaces.RequestBlock;
import org.eustrosoft.spec.request.BasicRequestBlock;
import org.eustrosoft.spec.response.BasicResponseBlock;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.List;

import static org.apache.commons.io.IOUtils.DEFAULT_BUFFER_SIZE;
import static org.eustrosoft.spec.Constants.SUBSYSTEM_CMS;
import static org.eustrosoft.tools.FileUtils.checkPathInjection;

@Handler(SUBSYSTEM_CMS)
public final class CMSHandler implements BasicHandler {
    private CMSDataSource cmsDataSource;
    private UserStorage userStorage;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public CMSHandler() {
        RequestContextHolder.ServletAttributes attributes = RequestContextHolder.getAttributes();
        this.response = attributes.getResponse();
        this.request = attributes.getRequest();
    }

    @Override
    public BasicResponseBlock processRequest(BasicRequestBlock requestBlock) throws Exception {
        QDBPSession session = new SessionProvider(this.request, this.response)
                .getSession();
        this.cmsDataSource = DataSourceProvider.getInstance(session.getConnection())
                .getDataSource();

        CMSRequestBlock cmsRequestBlock = (CMSRequestBlock) requestBlock;
        CMSResponseBlock cmsResponseBlock = new CMSResponseBlock();
        String path = cmsRequestBlock.getPath();
        String from = cmsRequestBlock.getFrom();
        String to = cmsRequestBlock.getTo();
        postProcessPath(path);
        postProcessPath(from);
        postProcessPath(to);
        String requestType = cmsRequestBlock.getR();
        switch (requestType) {
            case Constants.REQUEST_VIEW:
                List<CMSObject> directoryObjects = getDirectoryObjects(path);
                cmsResponseBlock.setContent(directoryObjects);
                break;
            case Constants.REQUEST_CREATE:
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
            case Constants.REQUEST_COPY:
                copyFile(from, to);
                break;
            case Constants.REQUEST_MOVE:
                if (from != null && !from.isEmpty()) {
                    move(from, to);
                }
                if (cmsDataSource instanceof DBDataSource) {
                    CMSObjectUpdateParameters data = new CMSObjectUpdateParameters(cmsRequestBlock.getDescription());
                    this.cmsDataSource.update(to, data);
                }
                break;
            case Constants.REQUEST_DELETE:
                delete(path);
                break;
            case Constants.REQUEST_TICKET:
                if (cmsDataSource instanceof DBDataSource) {
                    throw new Exception("This functionality is not implemented for database.");
                }
                FileTicket downloadPathDetails = getFileTicket(requestBlock);
                break;
            case Constants.REQUEST_RENAME:
                rename(from, to);
                break;
            case Constants.REQUEST_DOWNLOAD:
                if (cmsDataSource instanceof DBDataSource) {
                    throw new Exception("This functionality is not implemented for database.");
                }
                download(cmsRequestBlock);
                break;
            default:
                cmsResponseBlock.setE(404L);
                cmsResponseBlock.setM("Not yet implemented.");
                break;
        }
        return cmsResponseBlock;
    }

    public FileTicket getDownloadPathDetails(String pathToDownload, String userDir) throws Exception {
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
        if (path != null) {
            path = path.replaceAll("\\\\", "/");
        }
        return path;
    }

    private FileTicket getFileTicket(RequestBlock requestBlock) throws Exception {
        QDBPool dbPool = DBPoolContext.getInstance(
                DBPoolContext.getDbPoolName(request),
                DBPoolContext.getUrl(request),
                DBPoolContext.getDriverClass(request)
        );
        QDBPSession dbps = dbPool.logon(new QTISSessionCookie(request, response).getCookieValue());
        SamDAO samDao = new SamDAO(dbps.getConnection());
        this.userStorage = UserStorage.getInstanceForUser(samDao.getUserById(samDao.getUserId()));
        return getDownloadPathDetails(
                ((CMSRequestBlock) requestBlock).getPath(),
                this.userStorage.getExistedPathOrCreate()
        );
    }

    private void download(CMSRequestBlock cmsRequestBlock) throws Exception {
        FileDownloadService fds = FileDownloadService.getInstance();
        DownloadFileDetails fileInfo
                = fds.getFileInfoAndEndConversation(cmsRequestBlock.getTicket());
        response.reset();
        setContentType(cmsRequestBlock, response);
        response.setCharacterEncoding("UTF-8");
        response.setHeader(
                "Content-Disposition",
                String.format(
                        "attachment; filename=\"%s\";;filename*=utf-8''%s",
                        fileInfo.getFileName(),
                        URLEncoder.encode(fileInfo.getFileName(), "UTF-8")
                )
        );
        response.setContentLengthLong(fileInfo.getFileLength());
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Accept-Ranges", "bytes");
        OutputStream os = response.getOutputStream();
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
