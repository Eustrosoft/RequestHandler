/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.services;

import com.eustrosoft.core.handlers.cms.CMSHandler;
import com.eustrosoft.core.handlers.cms.CMSRequestBlock;
import com.eustrosoft.core.handlers.cms.DownloadFileDetails;
import com.eustrosoft.core.handlers.cms.FileDownloadMap;
import com.eustrosoft.core.handlers.cms.FileTicket;
import com.eustrosoft.core.tools.Json;
import com.eustrosoft.core.tools.QJson;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import static com.eustrosoft.core.constants.Constants.ERR_SERVER;
import static com.eustrosoft.core.constants.Constants.REQUEST_DOWNLOAD;
import static com.eustrosoft.core.constants.Constants.SUBSYSTEM_CMS;
import static com.eustrosoft.core.tools.HttpTools.getExceptionResponse;
import static com.eustrosoft.core.tools.HttpTools.printError;

public class FileDownloadService {
    private static final String GENERATING_ALGORITHM = "AES";
    private static final FileDownloadMap PATHS = new FileDownloadMap();
    private static FileDownloadService fds;

    public static synchronized FileDownloadService getInstance() {
        if (fds == null) {
            fds = new FileDownloadService();
        }
        return fds;
    }

    public synchronized String getIdAndBeginConversation(DownloadFileDetails downloadFileDetails)
            throws NoSuchAlgorithmException {
        String id = this.generateId();
        PATHS.put(id, downloadFileDetails);
        return id;
    }

    public synchronized FileTicket beginConversation(DownloadFileDetails downloadFileDetails)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return getTicketMap(getIdAndBeginConversation(downloadFileDetails));
    }

    public synchronized DownloadFileDetails getFileInfoAndEndConversation(String ticket) throws UnsupportedEncodingException {
        DownloadFileDetails fi = PATHS.get(URLDecoder.decode(ticket, StandardCharsets.UTF_8.displayName()));
        PATHS.remove(ticket);
        return fi;
    }

    private synchronized String generateId() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(GENERATING_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        keyGenerator.init(secureRandom);
        SecretKey key = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public synchronized FileTicket getTicketMap(String id) throws UnsupportedEncodingException {
        String ticket = URLEncoder.encode(id, StandardCharsets.UTF_8.displayName());
        DownloadFileDetails dfi = PATHS.get(id);
        return new FileTicket(ticket, dfi);
    }

    public void downloadFile(HttpServletRequest req, HttpServletResponse resp,
                             String ticket, String contentType)
            throws Exception {
        Json.JsonBuilder builder = new Json().builder();
        builder.addKeyValue("s", SUBSYSTEM_CMS);
        if (ticket != null && !ticket.isEmpty()) {
            Json.JsonBuilder jsonBuilder = builder.addKeyValue("ticket", ticket)
                    .addKeyValue("r", REQUEST_DOWNLOAD);
            if (contentType != null && !contentType.isEmpty()) {
                jsonBuilder.addKeyValue("contentType", contentType);
            }
            Json json = jsonBuilder.build();
            CMSRequestBlock cmsRequestBlock = new CMSRequestBlock(req, resp, new QJson(json.toString()));
            try {
                new CMSHandler().processRequest(cmsRequestBlock);
            } catch (Exception e) {
                printError(
                        resp,
                        getExceptionResponse(
                                "Error while downloading file occurred.",
                                SUBSYSTEM_CMS, REQUEST_DOWNLOAD, ERR_SERVER
                        )
                );
            }
        }
    }

    private FileDownloadService() {

    }
}
