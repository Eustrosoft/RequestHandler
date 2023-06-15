/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.eustrosoft.tools.Constants.DATE_FORMAT;

public final class DateFormatter {
    private SimpleDateFormat sdf;

    public DateFormatter() {
        sdf = new SimpleDateFormat(DATE_FORMAT);
    }

    public String convertToDate(long millis) {
        return sdf.format(millis);
    }

    public long convertToMillis(String date) throws ParseException {
        return sdf.parse(date).getTime();
    }

    public String convertToRightFormat(String date, SimpleDateFormat dateFormat)
            throws ParseException {
        return convertToDate(
                dateFormat.parse(date).getTime()
        );
    }

    public SimpleDateFormat getDateFormat() {
        return sdf;
    }

    public void setDateFormat(SimpleDateFormat sdf) {
        this.sdf = sdf;
    }
}
