package com.eustrosoft.core.tools;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.eustrosoft.tools.Constants.DATE_FORMAT;

public class DateTimeZone {
    private final Date utilDate;
    private final Timestamp sqlDate;
    private final String stringDate;

    private final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

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

    public SimpleDateFormat getSdf() {
        return sdf;
    }

    @Override
    public String toString() {
        return this.stringDate;
    }
}
