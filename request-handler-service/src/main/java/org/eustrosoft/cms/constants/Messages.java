/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms.constants;

public final class Messages {

    public final static String LINK = "link";
    public final static String FILE = "file";
    public final static String DIRECTORY = "directory";

    public final static String ALREADY_EXIST = "The %s is already exist.";
    public final static String NOT_EXIST = "The %s does not exist.";
    public final static String IS_NOT_CREATED = "The %s is not created. Check permissions or try again later.";


    public final static String MSG_DIR_EXISTS = String.format(ALREADY_EXIST, DIRECTORY);
    public final static String MSG_FILE_EXIST = String.format(ALREADY_EXIST, FILE);
    public final static String MSG_LINK_EXIST = String.format(ALREADY_EXIST, LINK);

    public final static String MSG_DIR_NOT_EXIST = String.format(NOT_EXIST, DIRECTORY);
    public final static String MSG_FILE_NOT_EXIST = String.format(NOT_EXIST, FILE);
    public final static String MSG_LINK_NOT_EXIST = String.format(NOT_EXIST, LINK);

    public final static String MSG_DIR_NOT_CREATED = String.format(IS_NOT_CREATED, DIRECTORY);
    public final static String MSG_FILE_NOT_CREATED = String.format(IS_NOT_CREATED, FILE);
    public final static String MSG_LINK_NOT_CREATE = String.format(IS_NOT_CREATED, LINK);

    public final static String MSG_NULL_PARAMS = String.format("Parameters was null or empty.");

    private Messages() {}
}
