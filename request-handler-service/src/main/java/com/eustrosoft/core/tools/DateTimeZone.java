package com.eustrosoft.core.tools;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.eustrosoft.tools.Constants.DATE_FORMAT;

@Getter
@Setter
public class DateTimeZone {
    private final Date utilDate;
    private final Timestamp sqlDate;
    private final String stringDate;

    private final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    public DateTimeZone(Long millis) throws ParseException {
        this.stringDate = sdf.format(new Date(millis));
        this.utilDate = sdf.parse(this.stringDate);
        this.sqlDate = new Timestamp(this.utilDate.getTime());
    }

    public DateTimeZone(Date date) throws ParseException {
        this.stringDate = sdf.format(date);
        this.utilDate = sdf.parse(this.stringDate);
        this.sqlDate = new Timestamp(date.getTime());
    }

    public DateTimeZone(String date) throws ParseException {
        this.stringDate = sdf.format(date);
        this.utilDate = sdf.parse(this.stringDate);
        this.sqlDate = new Timestamp(this.utilDate.getTime());
    }

    public DateTimeZone(Timestamp date) throws ParseException {
        this.stringDate = sdf.format(date.getTime());
        this.utilDate = sdf.parse(this.stringDate);
        this.sqlDate = new Timestamp(this.utilDate.getTime());
    }

    @Override
    public String toString() {
        return this.stringDate;
    }
}
