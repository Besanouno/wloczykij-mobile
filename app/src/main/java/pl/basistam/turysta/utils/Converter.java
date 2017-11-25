package pl.basistam.turysta.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Converter {

    public static String dateToString(Date date) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }

    public static String timeToString(Date date) {
        DateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(date);
    }

    public static String dateTimeToString(Date date) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return df.format(date);
    }

    public static Date stringToDatetime(String datetime) throws ParseException {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return format.parse(datetime);
    }

    public static Date stringToTime(String date) throws ParseException {
        DateFormat format = new SimpleDateFormat("HH:mm");
        return format.parse(date);
    }
}
