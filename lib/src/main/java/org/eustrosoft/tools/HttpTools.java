package org.eustrosoft.tools;

import org.eustrosoft.json.JsonUtil;
import org.eustrosoft.json.exception.JsonException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import static org.apache.commons.io.IOUtils.DEFAULT_BUFFER_SIZE;
import static org.eustrosoft.json.Constants.*;
import static org.eustrosoft.json.JsonUtil.toJson;
import static org.eustrosoft.spec.Constants.ERR_UNSUPPORTED;

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

    public static void printError(HttpServletResponse resp, String exceptionBlock)
            throws IOException {
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.println(exceptionBlock);
        writer.flush();
        writer.close();
    }

    public static String getUnsupportedException() {
        return getExceptionResponse("Unsupported", "", "", ERR_UNSUPPORTED);
    }

    public static String getExceptionResponse(
            String message, String subsystem,
            String request, Long errType
    ) {
        try {
            return toJson(
                    JsonUtil.getFormatString(4),
                    JsonUtil.AsEntry.getStringParams(PARAM_DISPATCHER_MESSAGE, message),
                    JsonUtil.AsEntry.getNumberParams(PARAM_DISPATCHER_ERROR, errType),
                    JsonUtil.AsEntry.getStringParams(PARAM_DISPATCHER_SUBSYSTEM, subsystem),
                    JsonUtil.AsEntry.getStringParams(PARAM_DISPATCHER_REQUEST, request)


            );
        } catch (JsonException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static boolean isAvailableContentType(String contentType) {
        return Arrays.asList(AVAILABLE_CONTENT_TYPES).contains(contentType);
    }
}
