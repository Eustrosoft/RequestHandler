package org.eustrosoft.dispatcher.context;

import org.eustrosoft.tools.ColorTextUtil;
import org.eustrosoft.tools.PropsContainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.eustrosoft.tools.PropertiesConstants.DISPATCHER_FILE_NAME;
import static org.eustrosoft.tools.PropertiesConstants.PROPERTY_HANDLERS_PATH;

public class HandlersConfig implements PropsContainer {
    private final Map<String, String> allowedHandlers;
    private final Properties properties;
    private String path; // Path to config file

    public HandlersConfig(String path) throws IOException {
        this.path = path;
        this.allowedHandlers = new HashMap<>();
        this.properties = new Properties();
        loadProps();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getAllowedHandlers() {
        return allowedHandlers;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public void loadProps() throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(DISPATCHER_FILE_NAME)) {
            if (input == null) {
                throw new FileNotFoundException(
                        "Unable to find dispatcher.properties.\nFile logging will be unavailable.\n"
                );
            }
            properties.load(input);
            String handlersPath = properties.getProperty(PROPERTY_HANDLERS_PATH);
            if (handlersPath == null) {
                throw new IOException(
                        "Property file was found, but " +
                                ColorTextUtil.getColoredString(PROPERTY_HANDLERS_PATH, ColorTextUtil.Color.GREEN) +
                                " property wasn't found."
                );
            }
            File rootPath = new File(handlersPath);
            if (!rootPath.exists()) {
                throw new FileNotFoundException(
                        "Property was found, but " +
                                ColorTextUtil.getColoredString(rootPath.getAbsolutePath(), ColorTextUtil.Color.GREEN) +
                                " does not exist."
                );
            }
            loadHandlers(rootPath);
        }
    }

    private void loadHandlers(File rootPath) throws IOException {
        this.path = rootPath.getAbsolutePath();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String buffer = "";
        while ((buffer = reader.readLine()) != null) {
            if (buffer.startsWith("#"))
                continue;
            String[] splitted = buffer.split("=");
            allowedHandlers.put(splitted[0], splitted[1]);
        }
        reader.close();
    }
}
