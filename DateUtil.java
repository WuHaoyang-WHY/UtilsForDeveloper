import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {

    // 年月日(无下划线)
    public static final String dtShort = "yyyyMMdd";

    public static final String DATE_FULL_STR      = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_MINUTE_STR    = "yyyy-MM-dd HH:mm";
    public static final String DATE_SMALL_STR     = "yyyy-MM-dd";
    public static final String DATE_DAY_START_STR = "yyyy-MM-dd 00:00:00";
    public static final String DATE_DAY_END_STR   = "yyyy-MM-dd 23:59:59";

    private static final DateFormat getFormat(String format) {
        return new SimpleDateFormat(format);
    }

    /**
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    /**
     * 获取当前日期是星期几<br>
     * <p>
     * "星期日" : 0
     * "星期一" : 1
     * "星期二" : 2
     * "星期三" : 3
     * "星期四" : 4
     * "星期五" : 5
     * "星期六" : 6
     *
     * @return 当前日期是星期几
     */
    public static int getWeekOfDate() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return w;
    }

    /**
     * Gets get current day.
     *
     * @return the get current day
     */
    public static String getCurrentDay() {
        return (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
    }

    /**
     * Gets get before current day.
     *
     * @return the get before current day
     */
    public static String getBeforeCurrentDay() {
        //构造一分钟前的cmd指令
        Calendar beforeData = Calendar.getInstance();
        //获取starTime
        beforeData.add(Calendar.DATE, -1);// 1分钟之前的时间
        return (new SimpleDateFormat("yyyy-MM-dd")).format(beforeData.getTime());
    }

    /**
     * 返回短日期格式（yyyyMMdd格式）
     *
     * @param Date
     * @return
     * @throws ParseException
     */
    public static final String shortDate(Date Date) {
        if (Date == null) {
            return null;
        }
        return getFormat(dtShort).format(Date);
    }

    /**
     * 返回日期时间（Add by Gonglei）
     *
     * @param stringDate (yyyyMMdd)
     * @return
     * @throws ParseException
     */
    public static final Date shortstring2Date(String stringDate)
            throws ParseException {
        if (stringDate == null) {
            return null;
        }

        return getFormat(dtShort).parse(stringDate);
    }

    /**
     * alahan add 20050825 获取传入时间相差的日期
     *
     * @param dt   传入日期，可以为空
     * @param diff 需要获取相隔diff天的日期 如果为正则取以后的日期，否则时间往前推
     * @return
     */
    public static String getDiffStringDate(Date dt, int diff) {
        Calendar ca = Calendar.getInstance();

        if (dt == null) {
            ca.setTime(new Date());
        } else {
            ca.setTime(dt);
        }

        ca.add(Calendar.DATE, diff);
        return shortDate(ca.getTime());
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(strDate, pos);
    }

    /**
     * 传入的日期到现在差几天
     * @param date
     * @return
     */
    public static Integer daysToNow(Date date) {
        return (int) ((System.currentTimeMillis() - date.getTime()) / 1000 / 60 / 60 / 24);
    }

    /**
     * 时间间隔
     * @param from
     * @param to
     * @return
     */
    public static Integer daySpace(Date from, Date to) {
        return (int) ((to.getTime() - from.getTime()) / 1000 / 60 / 60 / 24);
    }

    /**
     * Parse date.
     *
     * @param date    the date
     * @param pattern the pattern
     * @return the date
     */
    public static Date parse(Date date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_FULL_STR);

        try {
            String format = format(date, pattern);
            return null == format ? null : df.parse(format);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Format string.
     *
     * @param date    the date
     * @param pattern the pattern
     * @return the string
     */
    public static String format(Date date, String pattern) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 获取时间对应的秒数
     * @param date
     * @return
     */
    public static Long secondsTime(Date date) {
        if (null == date) {
            return 0L;
        }
        return date.getTime() / 1000;
    }

    /**
     * 获取今天已经过去的分钟数
     * @param date
     * @return
     */
    public static int afterZeroMinutes(Date date) {
        return (int) (date.getTime() - beginTime(date).getTime()) / 1000 / 60;
    }

    /**
     * 时间相差分钟数
     * @param from
     * @param to
     * @return
     */
    public static long minuteSpace(Date from, Date to) {
        return (long) (to.getTime() - from.getTime()) / 1000 / 60;
    }

    /**
     * 时间相差小时数
     * @param from
     * @param to
     * @return
     */
    public static long hourSpace(Date from, Date to) {
        return (long) (to.getTime() - from.getTime()) / 1000 / 3600;
    }

    /**
     * 获取一天的开始时间
     * @param date
     * @return
     */
    public static Date beginTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取一天的结束时间
     * @param date
     * @return
     */
    public static Date addOneDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }
}