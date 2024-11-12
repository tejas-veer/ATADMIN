package net.media.autotemplate.factory;

import net.media.autotemplate.bean.DateRange;
import spark.Request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

/*
    Created by shubham-ar
    on 18/12/17 3:12 PM   
*/
public class DateFactory {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final long ONE_DAYS_DURATION = 1 * 24 * 60 * 60 * 1000L;

    public static DateRange makeDateRangeForDays(int days, String startDate, String endDate) {
        Date date = new Date();

        long duration = ONE_DAYS_DURATION * days;
        startDate = Optional.ofNullable(startDate).orElse(dateFormat.format(new Date(date.getTime() - duration)));
        endDate = Optional.ofNullable(endDate).orElse(dateFormat.format(date));
        return new DateRange(startDate, endDate);
    }

    public static String convertToISO8601(String dateString) {
        try {
            dateString = dateString.replaceAll("GMT[+-]\\d{4} \\(.*\\)", "");
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
            Date date = inputDateFormat.parse(dateString);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static DateRange makeDateRange(String startDate, String endDate) {
        startDate = convertToISO8601(startDate);
        endDate = convertToISO8601(endDate);
        return new DateRange(startDate, endDate);
    }

    public static DateRange makeDateRange() {
        return makeDateRangeForDays(7, null, null);
    }

    public static DateRange makeDateRange(Request request) {
        return makeDateRangeForDays(7, request.queryParams("startDate"), request.queryParams("endDate"));
    }
}
