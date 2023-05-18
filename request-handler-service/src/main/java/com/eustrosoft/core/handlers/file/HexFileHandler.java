package com.eustrosoft.core.handlers.file;

import com.eustrosoft.core.context.User;
import com.eustrosoft.core.context.UserStorage;
import com.eustrosoft.core.context.UsersContext;
import com.eustrosoft.core.handlers.Handler;
import com.eustrosoft.core.handlers.requests.RequestBlock;
import com.eustrosoft.core.handlers.responses.ResponseBlock;
import org.eustrosoft.qtis.SessionCookie.QTISSessionCookie;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Map;
import java.util.zip.CRC32;

import static com.eustrosoft.core.tools.FileUtils.checkPathInjection;
import static com.eustrosoft.core.tools.FileUtils.getNextIterationFilePath;

public class HexFileHandler implements Handler {
    private UserStorage storage;

    @Override
    public synchronized ResponseBlock processRequest(RequestBlock requestBlock) throws Exception {
        HttpServletRequest request = requestBlock.getHttpRequest();
        HexFileRequestBlock requestBl = (HexFileRequestBlock) requestBlock;
        User user = UsersContext.getInstance()
                .getSQLUser(
                        new QTISSessionCookie(requestBlock.getHttpRequest(), requestBlock.getHttpResponse())
                                .getCookieValue()
                );
        this.storage = UserStorage.getInstanceForUser(user);
        String storagePath = this.storage.getBaseUploadPath();
        if (storagePath == null) {
            throw new IOException("Storage path is not defined for this user.");
        }
        if (storagePath.isEmpty()) {
            storagePath = this.storage.getStoragePath();
        }
        String answer = "";
        String uploadPath = requestBl.getPath();
        checkPathInjection(uploadPath);

        String fileName = requestBl.getFileName();
        String hexString = requestBl.getHexString();
        Map<String, String> userPaths = this.storage.getUserPaths();
        String filePath = userPaths.get(fileName);
        if (filePath == null || filePath.isEmpty()) {
            filePath = getNextIterationFilePath(
                    new File(storagePath, uploadPath).getAbsolutePath(),
                    fileName
            );
            userPaths.put(fileName, filePath);
        }
        if (filePath.isEmpty()) {
            throw new IOException("File path was not specified for this user.");
        }
        saveUploadFile(
                hexString,
                new File(filePath),
                requestBl.getFileHash()
        );
        answer = String.format(
                "Part was uploaded in %s path with file name %s.",
                uploadPath,
                fileName
        );
        if (requestBl.getChunkNumber().equals(requestBl.getChunkCount() - 1)) {
            this.storage.clearChunksOfCurrentPath();
            this.storage.clearPathOfCurrentStoragePath();
            userPaths.remove(fileName);
        }
        return new FileResponseBlock(answer);
    }

    private byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] =
                    (byte) ((Character.digit(s.charAt(i), 16) << 4)
                            + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private synchronized void saveUploadFile(String hexString, File dst, String fileHash) throws Exception {
        if (fileHash == null || fileHash.isEmpty()) {
            throw new Exception("Hash code not found in request.");
        }
        RandomAccessFile stream = new RandomAccessFile(dst, "rw");
        FileChannel channel = stream.getChannel();
        FileLock lock = null;
        CRC32 crc32 = new CRC32();
        byte[] buffer = decodeHexString(hexString);
        int len = buffer.length;
        try {
            lock = channel.tryLock();
            byte[] bytes = hexString.getBytes();
            crc32.update(bytes, 0, bytes.length);
            stream.seek(dst.length());
            stream.write(buffer);
            String value = String.format("%x", crc32.getValue());
            if (!fileHash.contains(value) && !value.contains(fileHash)) {
                dst.delete();
                throw new Exception("Hash code did not match.");
            }
        } catch (Exception e) {
            stream.close();
            channel.close();
            e.printStackTrace();
        } finally {
            if (null != lock) {
                lock.release();
            }
            stream.close();
            channel.close();
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
