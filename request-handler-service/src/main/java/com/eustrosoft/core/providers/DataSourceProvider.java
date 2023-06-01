package com.eustrosoft.core.providers;

import com.eustrosoft.core.tools.ColorTextUtil;
import com.eustrosoft.datasource.sources.CMSDataSource;
import com.eustrosoft.datasource.sources.PropsContainer;
import com.eustrosoft.datasource.sources.Source;
import com.eustrosoft.dbdatasource.core.DBDataSource;
import com.eustrosoft.filedatasource.FileCMSDataSource;
import lombok.Getter;
import org.eustrosoft.qdbp.QDBPConnection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.eustrosoft.core.tools.PropertiesConstants.CMS_FILE_NAME;
import static com.eustrosoft.datasource.sources.Constants.PROPERTY_DATA_SOURCE;

public final class DataSourceProvider implements PropsContainer {
    private static DataSourceProvider dataSourceProvider;
    @Getter
    private Properties properties;
    private CMSDataSource dataSource;

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
