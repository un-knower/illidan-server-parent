package cn.whaley.datawarehouse.illidan.engine.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static final ThreadLocal<SimpleDateFormat> simpleDateFormatBySecond = new ThreadLocal<SimpleDateFormat>() {
        protected synchronized SimpleDateFormat initialValue() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat;
        }
    };
    public static final ThreadLocal<SimpleDateFormat> simpleDateFormatByDay = new ThreadLocal<SimpleDateFormat>() {
        protected synchronized SimpleDateFormat initialValue() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat;
        }
    };
    public static final ThreadLocal<SimpleDateFormat> simpleDateFormatByMillisecond = new ThreadLocal<SimpleDateFormat>() {
        protected synchronized SimpleDateFormat initialValue() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            return simpleDateFormat;
        }
    };

    public static Date makeDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DATE, day);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    /**
     * 获得年
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.YEAR);
    }

    /**
     * 获得一年中的第一天
     *
     * @param thisDate
     * @return
     */
    public static Date getFirstDayOfYear(Date thisDate) {

        Date date = dateFormat(thisDate, 0, 0, 0);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.DAY_OF_YEAR, 1);
        return c.getTime();
    }

    /**
     * 获得一年中的最后一秒
     *
     * @param thisDate
     * @return
     */
    public static Date getLastSecondOfYear(Date thisDate) {

        Date date = dateFormat(thisDate, 23, 59, 59);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.DAY_OF_YEAR, c.getActualMaximum(Calendar.DAY_OF_YEAR));
        return c.getTime();
    }

    /**
     * 获得一季度中的第一天
     *
     * @param thisDate
     * @return
     */
    public static Date getFirstDayOfQuarter(Date thisDate) {

        Date date = dateFormat(thisDate, 0, 0, 0);

        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        int curMonth = cDay.get(Calendar.MONTH);
        if (curMonth >= Calendar.JANUARY && curMonth <= Calendar.MARCH) {
            cDay.set(Calendar.MONTH, Calendar.JANUARY);
        }
        if (curMonth >= Calendar.APRIL && curMonth <= Calendar.JUNE) {
            cDay.set(Calendar.MONTH, Calendar.APRIL);
        }
        if (curMonth >= Calendar.JULY && curMonth <= Calendar.AUGUST) {
            cDay.set(Calendar.MONTH, Calendar.JULY);
        }
        if (curMonth >= Calendar.OCTOBER && curMonth <= Calendar.DECEMBER) {
            cDay.set(Calendar.MONTH, Calendar.OCTOBER);
        }
        cDay.set(Calendar.DAY_OF_MONTH, 1);
        return cDay.getTime();
    }

    /**
     * 获得一季度中的最后一秒
     *
     * @param thisDate
     * @return
     */
    public static Date getLastSecondOfQuarter(Date thisDate) {

        Date date = dateFormat(thisDate, 23, 59, 59);

        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        int curMonth = cDay.get(Calendar.MONTH);
        if (curMonth >= Calendar.JANUARY && curMonth <= Calendar.MARCH) {
            cDay.set(Calendar.MONTH, Calendar.MARCH);
        }
        if (curMonth >= Calendar.APRIL && curMonth <= Calendar.JUNE) {
            cDay.set(Calendar.MONTH, Calendar.JUNE);
        }
        if (curMonth >= Calendar.JULY && curMonth <= Calendar.AUGUST) {
            cDay.set(Calendar.MONTH, Calendar.AUGUST);
        }
        if (curMonth >= Calendar.OCTOBER && curMonth <= Calendar.DECEMBER) {
            cDay.set(Calendar.MONTH, Calendar.DECEMBER);
        }
        cDay.set(Calendar.DAY_OF_MONTH, cDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cDay.getTime();
    }

    /**
     * 获得月
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 获得一个月中的第几天
     *
     * @param date
     * @return
     */
    public static int getDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 得到一个月中有多少天
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDayCount(int year, int month) {
        if (month < 0) {
            return 0;
        }

        if (month > 11) {
            return 0;
        }

        int[] monthDays = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
            monthDays[1]++;

        return monthDays[month];
    }

    /**
     * 获得一个月的第一天
     *
     * @param thisDate
     * @return
     */
    public static Date getFirstDayOfMonth(Date thisDate) {

        Date date = dateFormat(thisDate, 0, 0, 0);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.DATE, 1);
        return c.getTime();
    }

    /**
     * 获得一个月的最后一秒
     *
     * @param thisDate
     * @return
     */
    public static Date getLastSecondOfMonth(Date thisDate) {

        Date date = dateFormat(thisDate, 23, 59, 59);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
        return c.getTime();
    }

    /**
     * 获得一个月的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        date = dateFormat(date, 0, 0, 0);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
        return c.getTime();
    }

    /**
     * 获得一周的第一天(周一为第一天)
     *
     * @param date
     * @return
     */
    public static Date getLastMondayOfWeek(Date date) {
        date = dateFormat(date, 0, 0, 0);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        //系统中每周的第一天是周日，所以如果是周日往前一天到上一周
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            c.add(Calendar.DATE, -1);
        }

        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return c.getTime();
    }

    /**
     * 获得一周的最后一天(周日为最后一天)
     *
     * @param date
     * @return
     */
    public static Date getLastSecondOfWeek(Date date) {
        date = dateFormat(date, 23, 59, 59);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        //系统中每周的第一天是周日，所以如果不是周日往后移7天到下一周
        if (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            c.add(Calendar.DATE, 7);
        }

        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return c.getTime();
    }

    /**
     * 获得一个星期中的第几天(周日是第一天)
     *
     * @param date
     * @return
     */
    public static int getDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 得到2个日期之间相隔的天数
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static int getDaysBetween(Date beginTime, Date endTime) {

        if (beginTime.after(endTime)) {
            Date tmpTime = beginTime;
            beginTime = endTime;
            endTime = tmpTime;
        }

        beginTime = dateFormat(beginTime, 0, 0, 0);
        endTime = dateFormat(endTime, 0, 0, 0);

        return (int) ((endTime.getTime() - beginTime.getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date dateFormat(Date date, int hour, int minute, int second) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), hour, minute, second);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date firstSecondOfDate(Date date) {
        return dateFormat(date, 0, 0, 0);
    }

    public static Date lastSecondOfDate(Date date) {
        return dateFormat(date, 23, 59, 59);
    }

    /**
     * 获得后一天
     *
     * @param date
     * @return
     */
    public static Date getNextDay(Date date) {
        return getNextNDay(date, 1);
    }

    /**
     * 获得后N天
     *
     * @param date
     * @return
     */
    public static Date getNextNDay(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DATE, c.get(Calendar.DATE) + days);
        return c.getTime();
    }

    /**
     * 下一个足月时间
     *
     * @param date
     * @return
     */
    public static Date getNextMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(c.get(Calendar.YEAR)
                , c.get(Calendar.MONTH) + 1
                , c.get(Calendar.DATE) - 1
                , c.get(Calendar.HOUR)
                , c.get(Calendar.MINUTE)
                , c.get(Calendar.SECOND));

        return c.getTime();
    }

    /**
     * 下一个足年时间
     *
     * @param date
     * @return
     */
    public static Date getNextYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(c.get(Calendar.YEAR) + 1
                , c.get(Calendar.MONTH)
                , c.get(Calendar.DATE) - 1
                , c.get(Calendar.HOUR)
                , c.get(Calendar.MINUTE)
                , c.get(Calendar.SECOND));

        return c.getTime();
    }

    /**
     * 按天的周期数
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static double getPeriodCountByDay(Date beginTime, Date endTime) {
        int daysBetween = getDaysBetween(beginTime, endTime);
        return daysBetween + 1;
    }

    /**
     * 按足月的周期数
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static double getPeriodCountByMonth(Date beginTime, Date endTime) {
        return getPeriodCountByMonthOrYear(beginTime, endTime, true);
    }

    public static double getPeriodCountByWeek(Date beginTime, Date endTime) {
        int daysBetween = getDaysBetween(beginTime, endTime);
        return (daysBetween + 1) / 7.0;
    }

    /**
     * 按足年的周期数
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static double getPeriodCountByYear(Date beginTime, Date endTime) {
        return getPeriodCountByMonthOrYear(beginTime, endTime, false);
    }

    private static double getPeriodCountByMonthOrYear(Date beginTime, Date endTime, boolean isMonth) {

        beginTime = dateFormat(beginTime, 0, 0, 0);
        endTime = dateFormat(endTime, 0, 0, 0);

        double periodCount = 0;

        Date nextTime = getNext(beginTime, isMonth);
        while (nextTime.before(endTime)) {
            periodCount += 1;

            beginTime = getNextDay(nextTime);
            nextTime = getNext(beginTime, isMonth);
        }

        int leftDaysCount = getDaysBetween(beginTime, endTime) + 1;
        int periodDaysCount = getDaysBetween(beginTime, nextTime) + 1;


        periodCount += leftDaysCount * 1.0 / periodDaysCount;

        return periodCount;
    }

    private static Date getNext(Date date, boolean isMonth) {
        if (isMonth) {
            return getNextMonth(date);
        }

        return getNextYear(date);
    }

}