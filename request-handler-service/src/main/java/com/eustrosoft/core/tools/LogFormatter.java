/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.core.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return String.format("[%s] %s: %s\n",
                record.getLevel().getName(),
                getFormattedDate(record.getMillis()),
                record.getMessage());
    }

    private String getFormattedDate(long millis) {
        Date date = new Date(millis);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(date);
    }
}
