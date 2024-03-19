package com.eustrosoft.core.providers;

import com.eustrosoft.core.tools.CollectionsUtils;
import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MimeType {
    public static final String MIME_TYPE_JSON = "application/json";
    public static final String MIME_TYPE_WORD = "application/msword";
    public static final String MIME_TYPE_PDF = "application/pdf";
    public static final String MIME_TYPE_SQL = "application/sql";
    public static final String MIME_TYPE_API_JSON = "application/vnd.api+json";
    public static final String MIME_TYPE_EFI = "application/vnd.microsoft.portable-executable";
    public static final String MIME_TYPE_XLS = "application/vnd.ms-excel";
    public static final String MIME_TYPE_PPT = "application/vnd.ms-powerpoint";
    public static final String MIME_TYPE_ODT = "application/vnd.oasis.opendocument.text";
    public static final String MIME_TYPE_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String MIME_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String MIME_TYPE_WWW_FORM = "application/x-www-form-urlencoded";
    public static final String MIME_TYPE_XML = "application/xml";
    public static final String MIME_TYPE_ZIP = "application/zip";
    public static final String MIME_TYPE_ZSTD = "application/zstd";
    public static final String MIME_TYPE_MPEG = "audio/mpeg";
    public static final String MIME_TYPE_OGG = "audio/ogg";
    public static final String MIME_TYPE_AVIF = "image/avif";
    public static final String MIME_TYPE_JPEG = "image/jpeg";
    public static final String MIME_TYPE_PNG = "image/png";
    public static final String MIME_TYPE_SVG = "image/svg+xml";
    public static final String MIME_TYPE_TIFF = "image/tiff";
    public static final String MIME_TYPE_OBJ = "model/obj";
    public static final String MIME_TYPE_FORM_DATA = "multipart/form-data";
    public static final String MIME_TYPE_PLAIN = "text/plain";
    public static final String MIME_TYPE_CSS = "text/css";
    public static final String MIME_TYPE_CSV = "text/csv";
    public static final String MIME_TYPE_HTML = "text/html";
    public static final String MIME_TYPE_JS = "text/javascript";
    public static final String MIME_TYPE_TEXT_XML = "text/xml";

    private static Map<List<String>, String> mimeTypes = new HashMap<>();

    public static String getMimeTypeByFileName(String fileName) {
        return getMimeTypeByExtension(FilenameUtils.getExtension(fileName));
    }

    public static String getMimeTypeByExtension(String ext) {
        init();
        for (Map.Entry<List<String>, String> entry : mimeTypes.entrySet()) {
            if (entry.getKey().contains(ext)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static void init() {
        mimeTypes.put(
                CollectionsUtils.asList("txt"),
                MIME_TYPE_PLAIN
        );
        mimeTypes.put(
                CollectionsUtils.asList("json"),
                MIME_TYPE_JSON
        );
        mimeTypes.put(
                CollectionsUtils.asList("doc"),
                MIME_TYPE_DOCX
        );
        mimeTypes.put(
                CollectionsUtils.asList("pdf"),
                MIME_TYPE_PDF
        );
        mimeTypes.put(
                CollectionsUtils.asList("xls"),
                MIME_TYPE_XLS
        );
        mimeTypes.put(
                CollectionsUtils.asList("xlsx"),
                MIME_TYPE_XLSX
        );
        mimeTypes.put(
                CollectionsUtils.asList("ppt"),
                MIME_TYPE_PPT
        );
        mimeTypes.put(
                CollectionsUtils.asList("pptx"),
                MIME_TYPE_PPTX
        );
        mimeTypes.put(
                CollectionsUtils.asList("xml"),
                MIME_TYPE_XML
        );
        mimeTypes.put(
                CollectionsUtils.asList("odt"),
                MIME_TYPE_ODT
        );
        mimeTypes.put(
                CollectionsUtils.asList("docx"),
                MIME_TYPE_DOCX
        );
        mimeTypes.put(
                CollectionsUtils.asList("zip"),
                MIME_TYPE_ZIP
        );
        mimeTypes.put(
                CollectionsUtils.asList("svg"),
                MIME_TYPE_SVG
        );
        mimeTypes.put(
                CollectionsUtils.asList("mpeg"),
                MIME_TYPE_MPEG
        );
        mimeTypes.put(
                CollectionsUtils.asList("ogg"),
                MIME_TYPE_OGG
        );
        mimeTypes.put(
                CollectionsUtils.asList("png"),
                MIME_TYPE_PNG
        );
        mimeTypes.put(
                CollectionsUtils.asList("tif"),
                MIME_TYPE_TIFF
        );
        mimeTypes.put(
                CollectionsUtils.asList("obj"),
                MIME_TYPE_OBJ
        );
        mimeTypes.put(
                CollectionsUtils.asList("css"),
                MIME_TYPE_CSS
        );
        mimeTypes.put(
                CollectionsUtils.asList("js"),
                MIME_TYPE_JS
        );
        mimeTypes.put(
                CollectionsUtils.asList("html"),
                MIME_TYPE_HTML
        );
        mimeTypes.put(
                CollectionsUtils.asList("csv"),
                MIME_TYPE_CSV
        );
        mimeTypes.put(
                CollectionsUtils.asList("jpg", "jpeg", "jfif", "pjpeg", "pjp"),
                MIME_TYPE_JPEG
        );
        mimeTypes.put(
                CollectionsUtils.asList("xml"),
                MIME_TYPE_XML
        );
    }

    private MimeType() {

    }
}
