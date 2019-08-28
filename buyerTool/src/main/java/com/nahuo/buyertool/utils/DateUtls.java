package com.nahuo.buyertool.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by jame on 2017/8/1.
 */

public class DateUtls {
    /**
     * US locale - all HTTP dates are in english
     */
    public final static Locale LOCALE_US = Locale.US;

    /**
     * GMT timezone - all HTTP dates are on GMT
     */
    public final static TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

    /**
     * format for RFC 1123 date string -- "Sun, 06 Nov 1994 08:49:37 GMT"
     */
    public final static String RFC1123_PATTERN =
            "EEE, dd MMM yyyy HH:mm:ss z";

    /**
     * Format for http response header date field
     */
    public static final String HTTP_RESPONSE_DATE_HEADER =
            "EEE, dd MMM yyyy HH:mm:ss zzz";

    // format for RFC 1036 date string -- "Sunday, 06-Nov-94 08:49:37 GMT"
    private final static String rfc1036Pattern =
            "EEEEEEEEE, dd-MMM-yy HH:mm:ss z";

    // format for C asctime() date string -- "Sun Nov  6 08:49:37 1994"
    private final static String asctimePattern =
            "EEE MMM d HH:mm:ss yyyyy";

    /**
     * Pattern used for old cookies
     */
    public final static String OLD_COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";

    /**
     * DateFormat to be used to format dates
     */
    public final static DateFormat rfc1123Format =
            new SimpleDateFormat(RFC1123_PATTERN, LOCALE_US);

    /**
     * DateFormat to be used to format old netscape cookies
     */
    public final static DateFormat oldCookieFormat =
            new SimpleDateFormat(OLD_COOKIE_PATTERN, LOCALE_US);

    public final static DateFormat rfc1036Format =
            new SimpleDateFormat(rfc1036Pattern, LOCALE_US);

    public final static DateFormat asctimeFormat =
            new SimpleDateFormat(asctimePattern, LOCALE_US);

    static {
        rfc1123Format.setTimeZone(GMT_ZONE);
        oldCookieFormat.setTimeZone(GMT_ZONE);
        rfc1036Format.setTimeZone(GMT_ZONE);
        asctimeFormat.setTimeZone(GMT_ZONE);
    }
    /**
     * 0－10分钟时：十分钟内已打过
     10－30分钟时：半小时内已打过
     30分钟－1小时：1小时内已打过
     1小时－2小时：2小时内已打过
     2小时－3小时：3小时内已打过
     超过3小时：上次打印为11月3日 8点25分
     *@author  James Chen
     *@create time in 2017/11/3 15:07
     */
    public static String compareChatTime(long before, long after) {
        String compare = "";
        long compareTime = after - before;
        if (compareTime > 0 && compareTime <10 * 60*1000 ) {
//            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//            // 后面一条系统发送的时间
//            Date today = new Date(after);
//            compare = sdf.format(today);
            return "十分钟内已打印过";
        } else if (compareTime > 10 * 60*1000
                && compareTime <  30 * 60 * 1000) {
//            SimpleDateFormat sdf = new SimpleDateFormat("昨天  HH:mm");
//            // 后面一条系统发送的时间
//            Date today = new Date(after);
//            compare = sdf.format(today);
            return "半小时内已打印过";
        } else if (compareTime > 30 * 60 * 1000
                && compareTime < 1 * 60 * 60 * 1000) {
//            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日  HH:mm");
//            // 后面一条系统发送的时间
//            Date today = new Date(after);
//            compare = sdf.format(today);
            return "1小时内已打印过";
        }
        else if (compareTime > 1*30 * 60 * 1000
                && compareTime < 2 * 60 * 60 * 1000) {
//            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日  HH:mm");
//            // 后面一条系统发送的时间
//            Date today = new Date(after);
//            compare = sdf.format(today);
            return "2小时内已打印过";
        }
        else if (compareTime > 2*30 * 60 * 1000
                && compareTime < 3 * 60 * 60 * 1000) {
//            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日  HH:mm");
//            // 后面一条系统发送的时间
//            Date today = new Date(after);
//            compare = sdf.format(today);
            return "3小时内已打印过";
        }
        else if (compareTime > 3 * 60 * 60 * 1000) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日  HH:mm");
            // 后面一条系统发送的时间
            Date today = new Date(before);
            compare = sdf.format(today);
            return "上次打印时间为"+compare;
        } else {
            return "";
        }
    }
}
