package com.eustrosoft.core;

public final class Constants {
    public static final String SUBSYSTEM = "s";
    public static final String REQUESTS = "r";
    public static final String REQUEST = "r";
    public static final String TIMEOUT = "t";
    public static final String PARAMETERS = "parameters";
    public static final String QTISEND = "qend";
    public static final String QTISVER = "qver";

    public static final String SUBSYSTEM_SQL = "sql";
    public static final String SUBSYSTEM_FILE = "file";
    public static final String SUBSYSTEM_PING = "ping";
    public static final String SUBSYSTEM_CMS = "cms";
    public static final String SUBSYSTEM_LOGIN = "login";

    public static final String REQUEST_SQL = "sql";
    public static final String REQUEST_FILE_UPLOAD = "upload";
    public static final String REQUEST_CHUNKS_FILE_UPLOAD = "upload_chunks";
    public static final String REQUEST_CHUNKS_HEX_FILE_UPLOAD = "upload_chunks_hex";
    public static final String REQUEST_CHUNKS_BINARY_FILE_UPLOAD = "upload_chunks_binary";

    public static final String REQUEST_VIEW = "view";
    public static final String REQUEST_CREATE = "create";
    public static final String REQUEST_MOVE = "move";
    public static final String REQUEST_COPY = "copy";
    public static final String REQUEST_DELETE = "delete";
    public static final String REQUEST_TICKET = "ticket";
    public static final String REQUEST_DOWNLOAD = "download";
    public static final String REQUEST_RENAME = "rename";
    public static final String REQUEST_LOGIN = "login";
    public static final String REQUEST_LOGOUT = "logout";

    public static final Integer SESSION_TIMEOUT = 100 * 60;

    // ERR codes
    public static final Short ERR_UNAUTHORIZED = 401;
    public static final Short ERR_SERVER = 500;
    public static final Short ERR_OK = 0;

    // Messages
    public static final String MSG_UNAUTHORIZED = "Unauthorized";
    public static final String MSG_OK = "Ok";

    // Database Constants
    @Deprecated
    public static final String LOGIN_POOL = "loginPool";
    @Deprecated
    public static final String POSTGRES_DRIVER = "org.postgresql.Driver";

    private Constants() {

    }
}
