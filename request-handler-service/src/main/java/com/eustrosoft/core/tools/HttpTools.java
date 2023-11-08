package com.eustrosoft.core.tools;

import com.eustrosoft.cms.parameters.FileDetails;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Arrays;

import static com.eustrosoft.core.constants.Constants.ERR_UNSUPPORTED;
import static com.eustrosoft.core.handlers.responses.ResponseLang.en_US;
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

    @SneakyThrows
    public static void setHeadersForFileDownload(HttpServletResponse response, FileDetails fileDetails) {
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

    public static void printError(HttpServletResponse resp, JsonObject exceptionBlock)
            throws IOException {
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.println(new Gson().toJson(exceptionBlock));
        writer.flush();
        writer.close();
    }

    public static JsonObject getUnsupportedException() {
        return getExceptionResponse("Unsupported", "", "", ERR_UNSUPPORTED);
    }

    public static JsonObject getExceptionResponse(
            String message, String subsystem,
            String request, Short errType
    ) {
        JsonObject object = new JsonObject();
        object.addProperty("l", en_US);
        JsonObject response = new JsonObject();
        response.addProperty("m", message);
        response.addProperty("e", errType);
        response.addProperty("s", subsystem);
        response.addProperty("r", request);
        object.add("r", response);
        return object;
    }

    private static boolean isAvailableContentType(String contentType) {
        return Arrays.asList(AVAILABLE_CONTENT_TYPES).contains(contentType);
    }
}
