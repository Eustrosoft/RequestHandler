/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.date;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.eustrosoft.Constants.DATE_FORMAT;
import static org.eustrosoft.Constants.JSON_DATE_FORMAT;

public class DateTimeZone {
    private final Date utilDate;
    private final Timestamp sqlDate;
    private final String stringDate;

    private final SimpleDateFormat sdf = new SimpleDateFormat(JSON_DATE_FORMAT);

    public DateTimeZone(Long millis) throws ParseException {
        if (millis == null) {
            this.stringDate = null;
            this.utilDate = null;
            this.sqlDate = null;
        } else {
            this.stringDate = sdf.format(new Date(millis));
            this.utilDate = sdf.parse(this.stringDate);
            this.sqlDate = new Timestamp(this.utilDate.getTime());
        }
    }

    public DateTimeZone(Date date) throws ParseException {
        if (date == null) {
            this.stringDate = null;
            this.utilDate = null;
            this.sqlDate = null;
        } else {
            this.stringDate = sdf.format(date);
            this.utilDate = sdf.parse(this.stringDate);
            this.sqlDate = new Timestamp(date.getTime());
        }
    }

    public DateTimeZone(String date) throws ParseException {
        if (date == null) {
            this.stringDate = null;
            this.utilDate = null;
            this.sqlDate = null;
        } else {
            this.stringDate = sdf.format(date);
            this.utilDate = sdf.parse(this.stringDate);
            this.sqlDate = new Timestamp(this.utilDate.getTime());
        }
    }

    public DateTimeZone(Timestamp date) throws ParseException {
        if (date == null) {
            this.stringDate = null;
            this.utilDate = null;
            this.sqlDate = null;
        } else {
            this.stringDate = sdf.format(date.getTime());
            this.utilDate = sdf.parse(this.stringDate);
            this.sqlDate = new Timestamp(this.utilDate.getTime());
        }
    }

    public Date getUtilDate() {
        return utilDate;
    }

    public Timestamp getSqlDate() {
        return sqlDate;
    }

    public String getStringDate() {
        return stringDate;
    }

    public long getMillis() {
        return utilDate.getTime();
    }

    @Override
    public String toString() {
        return this.stringDate;
    }

    public static class Formatter {
        private SimpleDateFormat sdf;


        public Formatter() {
            sdf = new SimpleDateFormat(DATE_FORMAT);
        }

        public Formatter(SimpleDateFormat sdf) {
            this.sdf = sdf;
        }

        public Date convertToDate(long millis) throws ParseException {
            return sdf.parse(new Date(millis).toString());
        }

        public Date convertToDate(String date) throws ParseException {
            return new Date(sdf.parse(date).getTime());
        }

        public Date convertToDate(DateTimeZone date) throws ParseException {
            return new Date(date.getMillis());
        }

        public SimpleDateFormat getDateFormat() {
            return sdf;
        }

        public void setDateFormat(SimpleDateFormat sdf) {
            this.sdf = sdf;
        }
    }
}
