package com.eustrosoft.core.tools;

import com.eustrosoft.cms.parameters.FileDetails;
import com.eustrosoft.core.handlers.ExceptionBlock;
import com.eustrosoft.core.tools.json.JsonParser;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import static com.eustrosoft.core.constants.Constants.ERR_UNSUPPORTED;
import static org.apache.commons.io.IOUtils.DEFAULT_BUFFER_SIZE;

public final class HttpTools {
    private static final String[] AVAILABLE_CONTENT_TYPES = new String[]{
            "application/octet-stream", "application/json",
            "application/pdf", "application/xml", "plain/text",
            "image/gif", "image/jpeg", "image/jpg", "image/png",
            "image/svg", "video/mp4", "video/mpeg"
    };

    public static void setContentType(HttpServletResponse httpResponse, String mimeType) {
        if (mimeType != null && !mimeType.isEmpty()) {
            if (isAvailableContentType(mimeType)) {
                httpResponse.setContentType(mimeType);
                return;
            }
        }
        httpResponse.setContentType("application/octet-stream");
    }

    public static void setHeadersForFileDownload(HttpServletResponse response, FileDetails fileDetails)
            throws UnsupportedEncodingException {
        response.reset();
        setContentType(response, fileDetails.getMimeType());
        response.setCharacterEncoding(fileDetails.getEncoding());
        response.setHeader(
                "Content-Disposition",
                String.format(
                        "attachment; filename=\"%s\";;filename*=utf-8''%s",
                        fileDetails.getFileName(),
                        URLEncoder.encode(fileDetails.getFileName(), "UTF-8")
                )
        );
        response.setContentLengthLong(fileDetails.getFileLength());
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Accept-Ranges", "bytes");
    }

    public static void printError(HttpServletResponse resp, ExceptionBlock exceptionBlock)
            throws Exception {
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.println(new JsonParser().parseObject(exceptionBlock));
        writer.flush();
        writer.close();
    }

    public static String getUnsupportedException() {
        try {
            return new JsonParser().parseObject(getExceptionResponse("Unsupported", "", "", ERR_UNSUPPORTED));
        } catch (Exception ex) {
            return "exception duting parsing";
        }
    }

    public static ExceptionBlock getExceptionResponse(
            String message, String subsystem,
            String request, Short errType
    ) {
        return new ExceptionBlock(message, errType, subsystem, request);
    }

    private static boolean isAvailableContentType(String contentType) {
        return Arrays.asList(AVAILABLE_CONTENT_TYPES).contains(contentType);
    }
}
