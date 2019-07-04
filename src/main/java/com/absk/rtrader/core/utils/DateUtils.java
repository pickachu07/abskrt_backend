package com.absk.rtrader.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//make proper use of this 
public class DateUtils {    

    public static final String DB_FORMAT_DATETIME = "yyyy-MM-dd";        
    public static final String EXCHANGE_FORMAT_DATETIME = "dd-MM-yyyy";  
    private DateUtils() {
        // not publicly instantiable
    }       

    public static String convertDateFormat(String sourceFormat, String destFormat, String inputDateString){
    
    	SimpleDateFormat sFormat = new SimpleDateFormat(sourceFormat);
    	SimpleDateFormat dFormat = new SimpleDateFormat(destFormat);
    	Date date;
    	try {
			date = sFormat.parse(inputDateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return dFormat.format(date);
    	
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
