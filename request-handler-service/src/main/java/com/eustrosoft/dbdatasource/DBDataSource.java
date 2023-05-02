package com.eustrosoft.dbdatasource;

import com.eustrosoft.datasource.exception.CMSException;
import com.eustrosoft.datasource.sources.CMSDataSource;
import com.eustrosoft.datasource.sources.model.CMSObject;
import com.eustrosoft.datasource.sources.parameters.CMSObjectUpdateParameters;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DBDataSource implements CMSDataSource {
    @Override
    public List<CMSObject> getContent(String path) throws CMSException, IOException {
        return null;
    }

    @Override
    public String createLink(String source, String target) {
        return null;
    }

    @Override
    public String createFile(String path, InputStream stream) throws CMSException, IOException {

        return null;
    }

    @Override
    public String createFile(String path, String name) throws CMSException, IOException {
        return null;
    }

    @Override
    public String createDirectory(String path) throws CMSException {
        return null;
    }

    @Override
    public String getFullPath(String source) throws IOException, CMSException {
        return null;
    }

    @Override
    public boolean update(String path, CMSObjectUpdateParameters data) throws CMSException {
        return false;
    }

    @Override
    public boolean copy(String source, String direction) throws IOException, CMSException {
        return false;
    }

    @Override
    public boolean move(String source, String direction) throws IOException, CMSException {
        return false;
    }

    @Override
    public boolean delete(String path) throws IOException, CMSException {
        return false;
    }
}
