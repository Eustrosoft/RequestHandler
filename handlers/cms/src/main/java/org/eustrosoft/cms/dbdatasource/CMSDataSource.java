/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms.dbdatasource;

import org.eustrosoft.handlers.cms.dto.CMSObject;
import org.eustrosoft.handlers.cms.dto.CMSObjectUpdateParameters;
import org.eustrosoft.handlers.cms.dto.FileDetails;
import org.eustrosoft.handlers.cms.dto.HexFileParams;
import org.eustrosoft.handlers.cms.dto.HexFileResult;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Interface for CMS functionality.
 * <p>
 * This interface need to be implemented as main datasource
 * for looking into directories, files in database or file system,
 * creating files or directories and other.
 */
public interface CMSDataSource {

    List<CMSObject> getContent(String path) throws Exception;

    String createLink(String source, String target) throws Exception;

    String createFile(String path, InputStream stream) throws Exception;

    String createFile(String path, String name) throws Exception;

    HexFileResult createFileHex(HexFileParams params) throws Exception;

    InputStream getFileStream(String path) throws Exception;

    FileDetails getFileDetails(String path) throws Exception;

    default void uploadToStream(InputStream in, OutputStream out) throws Exception {
        try (InputStream inputStream = in) {
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
        } finally {
            out.flush();
            out.close();
            in.close();
        }
    }

    String createDirectory(String path, String description, Integer securityLevel) throws Exception;

    String getFullPath(String source) throws Exception;

    boolean update(String path, CMSObjectUpdateParameters data) throws Exception;

    boolean copy(String source, String direction) throws Exception;

    boolean move(String source, String direction) throws Exception;

    boolean delete(String path) throws Exception;
}
