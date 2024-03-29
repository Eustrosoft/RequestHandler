/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.cms.filedatasource;

import com.eustrosoft.cms.CMSDataSource;
import com.eustrosoft.cms.CMSType;
import com.eustrosoft.cms.dto.CMSGeneralObject;
import com.eustrosoft.cms.dto.CMSObject;
import com.eustrosoft.cms.exception.CMSException;
import com.eustrosoft.cms.parameters.CMSObjectUpdateParameters;
import com.eustrosoft.cms.parameters.FileDetails;
import com.eustrosoft.cms.parameters.HexFileParams;
import com.eustrosoft.cms.parameters.HexFileResult;
import com.eustrosoft.cms.util.FileUtils;
import com.eustrosoft.core.tools.ColorTextUtil;
import com.eustrosoft.core.tools.DateTimeZone;
import com.eustrosoft.core.tools.PropsContainer;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.CRC32;

import static com.eustrosoft.cms.constants.Messages.*;
import static com.eustrosoft.core.tools.PropertiesConstants.CMS_FILE_NAME;
import static com.eustrosoft.core.tools.PropertiesConstants.PROPERTY_CMS_ROOT_PATH;

public class FileCMSDataSource implements CMSDataSource, PropsContainer {
    private final Properties properties = new Properties();
    private String filePath;

    @Override
    public Properties getProperties() {
        return properties;
    }

    public String getFilePath() {
        return filePath;
    }

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
        File[] filesInside = entry.listFiles();
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
    public HexFileResult createFileHex(HexFileParams params)
            throws Exception {
        String crc32 = params.getCrc32();
        String hex = params.getHex();
        String dist = params.getDestination();
        if (crc32 == null || crc32.isEmpty()) {
            throw new Exception("Hash code not found in request.");
        }
        File destinationFile = new File(dist);
        RandomAccessFile stream = new RandomAccessFile(destinationFile, "rw");
        FileChannel channel = stream.getChannel();
        FileLock lock = null;
        CRC32 crc = new CRC32();
        byte[] buffer = decodeHexString(hex);
        try {
            lock = channel.tryLock();
            byte[] bytes = hex.getBytes();
            crc.update(bytes, 0, bytes.length);
            stream.seek(destinationFile.length());
            stream.write(buffer);
            String value = String.format("%x", crc.getValue());
            if (!crc32.contains(value) && !value.contains(crc32)) { // TODO
                destinationFile.delete();
                throw new Exception("Hash code did not match.");
            }
        } catch (Exception e) {
            stream.close();
            channel.close();
            // e.printStackTrace();
        } finally {
            if (null != lock) {
                lock.release();
            }
            stream.close();
            channel.close();
        }
        return new HexFileResult("", "", destinationFile.getName(), params.getDestination());
    }

    @Override
    public InputStream getFileStream(String path) throws Exception {
        return null; // todo
    }

    @Override
    public FileDetails getFileDetails(String path) throws Exception {
        return null; // todo
    }

    @Override
    public String createDirectory(String path, String description, Integer securityLevel) throws CMSException {
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
            if (!file.exists()) {
                continue;
            }
            BasicFileAttributes attributes =
                    Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            CMSGeneralObject obj = new CMSGeneralObject(
                    FilenameUtils.getExtension(file.getName()),
                    file.getName(),
                    postProcessPath(
                            file.getAbsolutePath().substring(getRootPath().length())
                    ),
                    new ArrayList<>(),
                    file.length(),
                    -1,
                    String.valueOf(file.hashCode()),
                    null,
                    ""
            );
            if (file.isDirectory()) {
                obj.setType(CMSType.DIRECTORY);
            } else {
                obj.setType(CMSType.FILE);
            }
            try {
                obj.setCreated(new DateTimeZone(attributes.creationTime().toMillis()).toString());
            } catch (Exception ex) {
            }
            obj.setModified(new Date(file.lastModified()));
            objects.add(obj);
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
        return new File(getRootPath(), source).getAbsolutePath();
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

    public void loadProps() throws Exception {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CMS_FILE_NAME)) {
            if (input == null) {
                throw new FileNotFoundException(
                        "Unable to find cms.properties.\nFile logging will be unavailable.\n"
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

    public byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    public byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: " + hexChar);
        }
        return digit;
    }
}
