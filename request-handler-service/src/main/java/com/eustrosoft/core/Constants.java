package com.eustrosoft.core;

public final class Constants {
    public static final String SUBSYSTEM = "s";
    public static final String REQUESTS = "r";
    public static final String REQUEST = "r";
    public static final String TIMEOUT = "t";
    public static final String PARAMETERS = "p";
    public static final String QTISEND = "qend";
    public static final String QTISVER = "qver";

    public static final String SUBSYSTEM_SQL = "sql";
    public static final String SUBSYSTEM_FILE = "file";
    public static final String SUBSYSTEM_PING = "ping";
    public static final String SUBSYSTEM_CMS = "cms";

    public static final String REQUEST_SQL = "sql";
    public static final String REQUEST_FILE_UPLOAD = "upload";
    public static final String REQUEST_CHUNKS_FILE_UPLOAD = "upload_chunks";
    public static final String REQUEST_CHUNKS_BINARY_FILE_UPLOAD = "upload_chunks_binary";

    public static final String REQUEST_CREATE = "create";
    public static final String REQUEST_MOVE = "move";
    public static final String REQUEST_COPY = "copy";
    public static final String REQUEST_DELETE = "delete";
    public static final String REQUEST_RENAME = "rename";

    public static final Integer SESSION_TIMEOUT = 100 * 60;

    // ERR codes
    public static final Short ERR_UNAUTHORIZED = 401;
    public static final Short ERR_OK = 0;

    // Messages
    public static final String MSG_UNAUTHORIZED = "Unauthorized";
    public static final String MSG_OK = "Ok";

    private Constants() {

    }
}
