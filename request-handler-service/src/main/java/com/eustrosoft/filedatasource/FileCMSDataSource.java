package com.eustrosoft.filedatasource;

import com.eustrosoft.core.tools.ColorTextUtil;
import com.eustrosoft.datasource.exception.CMSException;
import com.eustrosoft.datasource.sources.CMSDataSource;
import com.eustrosoft.datasource.sources.model.CMSDirectory;
import com.eustrosoft.datasource.sources.model.CMSFile;
import com.eustrosoft.datasource.sources.model.CMSObject;
import com.eustrosoft.datasource.sources.model.CMSType;
import com.eustrosoft.datasource.sources.parameters.CMSObjectUpdateParameters;
import com.eustrosoft.filedatasource.util.FileUtils;
import lombok.val;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static com.eustrosoft.core.tools.PropertiesConstants.*;
import static com.eustrosoft.filedatasource.constants.Messages.*;

public class FileCMSDataSource implements CMSDataSource {
    private static final Properties properties = new Properties();
    private String filePath;

    public FileCMSDataSource() throws Exception {
        loadProps();
    }

    @Override
    public List<CMSObject> getContent(String path) throws CMSException, IOException {
        if (path == null || path.isEmpty()) {
            throw new CMSException(MSG_NULL_PARAMS);
        }
        checkPathInjection(path);
        File entry = new File(getRootPath(), path);
        if (!entry.exists()) {
            throw new CMSException(MSG_DIR_NOT_EXIST);
        }
        File [] filesInside = entry.listFiles();
        return fileToCMS(filesInside);
    }

    @Override
    public String createFile(String path, InputStream inputStream)
            throws CMSException, IOException {
        if (isNullOrEmpty(path)) {
            throw new CMSException(MSG_NULL_PARAMS);
        }
        checkPathInjection(path);
        File newFile = new File(getRootPath(), path);
        if (newFile.exists()) {
            throw new CMSException(MSG_FILE_EXIST);
        }
        File directory = newFile.getParentFile();
        if (isFileExist(directory)) {
            FileUtils.createFile(newFile.getAbsolutePath(), inputStream);
        }
        return MSG_FILE_NOT_CREATED;
    }

    @Override
    public String createFile(String path, String name)
            throws CMSException, IOException {
        if (isNullOrEmpty(path)) {
            throw new CMSException(MSG_NULL_PARAMS);
        }
        checkPathInjection(path);
        checkPathInjection(name);
        File directory = new File(getRootPath(), path);
        if (!directory.exists()) {
            throw new CMSException(MSG_DIR_NOT_EXIST);
        }
        File newFile = new File(directory, name);
        if (!newFile.exists()) {
            if (newFile.createNewFile()) {
                return newFile.getAbsolutePath().substring(getRootPath().length());
            }
        }
        return MSG_FILE_NOT_CREATED;
    }

    @Override
    public String createDirectory(String path) throws CMSException {
        if (isNullOrEmpty(path)) {
            throw new CMSException(MSG_NULL_PARAMS);
        }
        checkPathInjection(path);
        File newDirectory = new File(getRootPath(), path);
        if (isFileExist(newDirectory)) {
            throw new CMSException(MSG_DIR_EXISTS);
        }
        boolean isCreated = newDirectory.mkdir();
        if (isCreated) {
            return newDirectory.getAbsolutePath().substring(getRootPath().length());
        }
        return MSG_DIR_NOT_CREATED;
    }

    private boolean isFileExist(File file) throws CMSException {
        if (file == null) {
            throw new CMSException(MSG_DIR_NOT_EXIST);
        }
        return file.exists();
    }

    private List<CMSObject> fileToCMS(List<File> files) throws IOException {
        List<CMSObject> objects = new ArrayList<>();
        for (File file : files) {
            CMSObject obj = null;
            if (!file.exists()) {
                continue;
            }
            BasicFileAttributes attributes =
                    Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            if (file.isFile()) {
                obj = new CMSFile(
                        FilenameUtils.getExtension(file.getName()),
                        file.getName(),
                        postProcessPath(
                                file.getAbsolutePath().substring(getRootPath().length())
                        ),
                        new ArrayList<String>(),
                        new Date(attributes.creationTime().toMillis()),
                        new Date(file.lastModified()),
                        file.length(),
                        String.valueOf(file.hashCode()),
                        CMSType.FILE.toString()
                );
            }
            if (file.isDirectory()) {
                obj = new CMSDirectory(
                        file.getName(),
                        postProcessPath(
                                file.getAbsolutePath().substring(getRootPath().length())
                        ),
                        new ArrayList<String>(),
                        file.length(),
                        new Date(file.lastModified()),
                        new Date(attributes.creationTime().toMillis()),
                        CMSType.DIRECTORY.toString()
                );
            }
            if (obj != null) {
                objects.add(obj);
            }
        }
        return objects;
    }

    private String postProcessPath(String path) {
        return path
                .replace("//", "/")
                .replace("\\", "/");
    }

    private List<CMSObject> fileToCMS(File[] files) throws IOException {
        return fileToCMS(Arrays.asList(files));
    }

    @Override
    public String createLink(String source, String target) {
        return null;
    }

    @Override
    public boolean update(String path, CMSObjectUpdateParameters data) throws CMSException {
        if (isNullOrEmpty(path) || data == null) {
            throw new CMSException(MSG_NULL_PARAMS);
        }
        checkPathInjection(path, data.getName());
        File file = new File(getRootPath(), path);
        if (!file.exists()) {
            throw new CMSException(MSG_FILE_NOT_EXIST);
        }
        String newFileName = data.getName();
        if (isNullOrEmpty(newFileName)) { // TODO: change when there is more parameters
            throw new CMSException(MSG_NULL_PARAMS);
        }
        File nfn = new File(file.getParentFile(), newFileName);
        return file.renameTo(nfn);
    }

    @Override
    public boolean copy(String source, String direction) throws IOException, CMSException {
        if (isNullOrEmpty(source) || isNullOrEmpty(direction)) {
            throw new CMSException(MSG_NULL_PARAMS);
        }
        checkPathInjection(source, direction);
        File sourceFile = new File(getRootPath(), source);
        if (!sourceFile.exists()) {
            throw new CMSException(MSG_FILE_NOT_EXIST);
        }
        File directionDirectory = new File(getRootPath(), direction);
        if (!isFileExist(directionDirectory)) {
            directionDirectory = new File(directionDirectory, sourceFile.getName());
            if (sourceFile.isFile()) {
                org.apache.commons.io.FileUtils.copyFile(sourceFile, directionDirectory);
            }
            if (sourceFile.isDirectory()) {
                org.apache.commons.io.FileUtils.copyDirectory(sourceFile, directionDirectory);
            }
        }
        return true;
    }

    @Override
    public boolean move(String source, String direction) throws IOException, CMSException {
        if (isNullOrEmpty(source) || isNullOrEmpty(direction)) {
            throw new CMSException(MSG_NULL_PARAMS);
        }
        checkPathInjection(source, direction);
        File sourceFile = new File(getRootPath(), source);
        if (!sourceFile.exists()) {
            throw new CMSException(MSG_FILE_NOT_EXIST);
        }
        File directionFile = new File(getRootPath(), direction);
        if (directionFile.exists()) {
            throw new CMSException(MSG_FILE_EXIST);
        }
        if (sourceFile.isDirectory()) {
            org.apache.commons.io.FileUtils.moveDirectory(sourceFile, directionFile);
        }
        if (sourceFile.isFile()) {
            org.apache.commons.io.FileUtils.moveFile(sourceFile, directionFile);
        }
        return true;
    }

    @Override
    public String getFullPath(String source) throws IOException, CMSException {
        checkPathInjection(source);
        return new File(getRootPath(), source).getAbsolutePath(); // Admin tool
    }

    @Override
    public boolean delete(String path) throws IOException, CMSException {
        checkPathInjection(path);
        if (isNullOrEmpty(path)) {
            throw new CMSException(MSG_FILE_NOT_EXIST);
        }
        File entry = new File(getRootPath(), path);
        if (entry.exists()) {
            if (entry.isDirectory()) {
                org.apache.commons.io.FileUtils.deleteDirectory(entry);
            } else {
                org.apache.commons.io.FileUtils.delete(entry);
            }
            return true;
        }
        return false;
    }

    private boolean isNullOrEmpty(String val) {
        return val == null || val.isEmpty();
    }

    private void checkPathInjection(String... params) throws CMSException {
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                throw new CMSException("Param was null or empty.");
            }
            if (param.contains("..")) {
                throw new CMSException("Path Injection Detected.");
            }
        }
    }

    private String getRootPath() {
        return filePath;
    }

    private void loadProps() throws Exception {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CMS_FILE_NAME)) {
            if (input == null) {
                throw new FileNotFoundException(
                        "Unable to find logging.properties.\nFile logging will be unavailable.\n"
                );
            }
            properties.load(input);
            String cmsRootPath = properties.getProperty(PROPERTY_CMS_ROOT_PATH);
            if (cmsRootPath == null) {
                throw new Exception(
                        "Property file was found, but " +
                        ColorTextUtil.getColoredString("rootPath", ColorTextUtil.Color.GREEN) +
                        " property wasn't found."
                );
            }
            File rootPath = new File(cmsRootPath);
            if (!rootPath.exists()) {
                throw new FileNotFoundException(
                        "Property was found, but " +
                        ColorTextUtil.getColoredString("rootPath", ColorTextUtil.Color.GREEN) +
                        " does not exist."
                );
            }
            this.filePath = rootPath.getAbsolutePath();
        } catch (IOException ex) {
            throw new Exception("Error while processing properties.");
        }
    }
}
