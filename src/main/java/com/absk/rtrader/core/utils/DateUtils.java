package com.absk.rtrader.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {    

    public static final String DB_FORMAT_DATETIME = "yyyy-MM-dd";        

    private DateUtils() {
        // not publicly instantiable
    }       

    public static Date getDate(String dateStr, String format) {
        final DateFormat formatter = new SimpleDateFormat(format);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {                
            return null;
        }
    }



} 
