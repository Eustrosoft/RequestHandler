/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eustrosoft.cms.dbdatasource.CMSDataSource;
import org.eustrosoft.cms.dbdatasource.DBDataSource;
import org.eustrosoft.cms.dbdatasource.DataSourceProvider;
import org.eustrosoft.cms.dbdatasource.UserStorage;
import org.eustrosoft.cms.exception.CMSException;
import org.eustrosoft.constants.Constants;
import org.eustrosoft.core.annotations.Handler;
import org.eustrosoft.core.interfaces.BasicHandler;
import org.eustrosoft.core.request.RequestBlock;
import org.eustrosoft.core.response.ResponseBlock;
import org.eustrosoft.handlers.cms.dto.CMSObject;
import org.eustrosoft.handlers.cms.dto.CMSObjectUpdateParameters;
import org.eustrosoft.handlers.cms.dto.CMSRequestBlock;
import org.eustrosoft.handlers.cms.dto.CMSResponseBlock;
import org.eustrosoft.handlers.cms.dto.CMSType;
import org.eustrosoft.providers.RequestContextHolder;
import org.eustrosoft.providers.SessionProvider;
import org.eustrosoft.qdbp.QDBPSession;

import java.io.File;
import java.util.List;

import static org.eustrosoft.constants.Constants.SUBSYSTEM_CMS;

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
    public ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
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
            case Constants.REQUEST_RENAME:
                rename(from, to);
                break;
            default:
                cmsResponseBlock.setE(404L);
                cmsResponseBlock.setM("Not yet implemented.");
                break;
        }
        return cmsResponseBlock;
    }

    private String postProcessPath(String path) {
        if (path != null) {
            path = path.replaceAll("\\\\", "/");
        }
        return path;
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
