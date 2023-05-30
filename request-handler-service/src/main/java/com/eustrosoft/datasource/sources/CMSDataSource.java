package com.eustrosoft.datasource.sources;

import com.eustrosoft.datasource.sources.model.CMSObject;
import com.eustrosoft.datasource.sources.parameters.CMSObjectUpdateParameters;

import java.io.InputStream;
import java.util.List;

/**
 * Interface for CMS functionality.
 *
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

    String createDirectory(String path) throws Exception;

    String getFullPath(String source) throws Exception;

    boolean update(String path, CMSObjectUpdateParameters data) throws Exception;

    boolean copy(String source, String direction) throws Exception;

    boolean move(String source, String direction) throws Exception;

    // TODO: add recursive variant
    boolean delete(String path) throws Exception;
}
