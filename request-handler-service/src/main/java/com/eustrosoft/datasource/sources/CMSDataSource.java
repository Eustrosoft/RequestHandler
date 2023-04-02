package com.eustrosoft.datasource.sources;

import com.eustrosoft.datasource.exception.CMSException;
import com.eustrosoft.datasource.sources.model.CMSObject;
import com.eustrosoft.datasource.sources.parameters.CMSObjectUpdateParameters;

import java.io.IOException;
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

    List<CMSObject> getContent(String path) throws CMSException, IOException;

    String createLink(String source, String target);

    String createFile(String path, InputStream stream) throws CMSException, IOException;

    String createDirectory(String path) throws CMSException;

    boolean update(String path, CMSObjectUpdateParameters data) throws CMSException;

    boolean copy(String source, String direction) throws IOException, CMSException;

    boolean move(String source, String direction) throws IOException, CMSException;

    // TODO: add recursive variant
    boolean delete(String path) throws IOException, CMSException;
}
