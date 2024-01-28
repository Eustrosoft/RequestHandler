/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms.providers;

import org.eustrosoft.cms.CMSDataSource;
import org.eustrosoft.cms.Source;
import org.eustrosoft.cms.dbdatasource.DBDataSource;
import org.eustrosoft.cms.filedatasource.FileCMSDataSource;
import org.eustrosoft.qdbp.QDBPConnection;
import org.eustrosoft.tools.ColorTextUtil;
import org.eustrosoft.tools.PropsContainer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.eustrosoft.cms.constants.Constants.PROPERTY_DATA_SOURCE;
import static org.eustrosoft.tools.PropertiesConstants.CMS_FILE_NAME;

public final class DataSourceProvider implements PropsContainer {
    private static DataSourceProvider dataSourceProvider;
    private Properties properties;
    private CMSDataSource dataSource;

    @Override
    public Properties getProperties() {
        return properties;
    }

    private DataSourceProvider(QDBPConnection connection) throws Exception {
        this.properties = new Properties();
        loadProps();
        String dataSource = getLoadedProps().get(PROPERTY_DATA_SOURCE);
        if (dataSource.equalsIgnoreCase(Source.DATABASE.getValue())) {
            this.dataSource = new DBDataSource(connection);
        }
        if (dataSource.equalsIgnoreCase(Source.FILE_SYSTEM.getValue())) {
            this.dataSource = new FileCMSDataSource();
        }
    }

    public static DataSourceProvider getInstance(QDBPConnection connection) throws Exception {
        if (dataSourceProvider == null) {
            dataSourceProvider = new DataSourceProvider(connection);
        }
        // refresh connection or other info for cms data source based on different instances
        dataSourceProvider.refresh(connection);
        return dataSourceProvider;
    }

    public CMSDataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    public void loadProps() throws Exception {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CMS_FILE_NAME)) {
            if (input == null) {
                throw new FileNotFoundException(
                        "Unable to find cms.properties.\nFile logging will be unavailable.\n"
                );
            }
            properties.load(input);
            String dataSource = properties.getProperty(PROPERTY_DATA_SOURCE);
            if (dataSource == null) {
                throw new Exception(
                        "Property file was found, but " +
                                ColorTextUtil.getColoredString("dataSource", ColorTextUtil.Color.GREEN) +
                                " property wasn't found."
                );
            }
            if (!Source.DATABASE.isInRange(dataSource)) {
                throw new Exception(
                        "Property was found, but " +
                                ColorTextUtil.getColoredString("dataSource", ColorTextUtil.Color.GREEN) +
                                " is incorrect or empty."
                );
            }
        } catch (IOException ex) {
            throw new Exception("Error while processing properties.");
        }
    }

    private void refresh(QDBPConnection connection)
            throws Exception {
        CMSDataSource dataSource = this.getDataSource();
        if (dataSource instanceof DBDataSource) {
            this.dataSource = new DBDataSource(connection);
        }
    }
}
