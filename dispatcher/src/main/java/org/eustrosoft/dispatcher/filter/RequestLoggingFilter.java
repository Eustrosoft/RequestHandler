/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.dispatcher.filter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.eustrosoft.constants.PropertiesConstants;
import org.eustrosoft.logging.LogFormatter;
import org.eustrosoft.util.ColorTextUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

//@WebFilter(
//        urlPatterns = {"/*"},
//        filterName = "RequestLoggingFilter",
//        description = "Logging Filter"
//)
public class RequestLoggingFilter {
    private static final Logger logger = Logger.getLogger(RequestLoggingFilter.class.getName());
    private static final Properties loggingProperties = new Properties();
    private static FileHandler fileHandler;
    private static ConsoleHandler consoleHandler;

    public void init(FilterConfig filterConfig) throws ServletException {
        addConsoleHandler();
        addFileHandler();
    }

    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {
        logger.log(Level.INFO,
                String.format("Produced request from: %s. Protocol: %s. Content-Type: %s",
                        req.getRemoteAddr(), req.getProtocol(), req.getContentType())
        );
        chain.doFilter(req, resp);
    }

    public void destroy() {
    }

    private void addFileHandler() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(PropertiesConstants.LOGGING_FILE_NAME)) {
            if (input == null) {
                logger.warning("Unable to find logging.properties.\nFile logging will be unavailable.\n");
                return;
            }
            loggingProperties.load(input);
            String logFilePath = loggingProperties.getProperty(PropertiesConstants.PROPERTY_LOG_FILE);
            if (logFilePath == null) {
                logger.warning("Property file was found, but " +
                        ColorTextUtil.getColoredString("logFile", ColorTextUtil.Color.GREEN) +
                        " property wasn't found.");
                return;
            }
            File logFile = new File(logFilePath);
            if (!logFile.exists()) {
                logger.warning("Property was found, but " +
                        ColorTextUtil.getColoredString("file", ColorTextUtil.Color.GREEN) +
                        " does not exist.");
                return;
            }
            fileHandler = new FileHandler(logFilePath, true);
            fileHandler.setFormatter(new LogFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Error while processing properties.");
        }
    }

    private void addConsoleHandler() {
        consoleHandler = new ConsoleHandler();
        logger.addHandler(consoleHandler);
    }
}
