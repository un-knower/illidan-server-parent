package cn.whaley.datawarehouse.illidan.engine.param;

import cn.whaley.datawarehouse.illidan.engine.util.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by lituo on 2017/6/28.
 */
public class DateParamMethod {

    public static Date invokeMethod(Date executeDate, String paramName) {
        String methodName = "get" + StringUtils.capitalize(paramName);
        try {
            Method method = DateParamMethod.class.getMethod(methodName, Date.class);
            return (Date) method.invoke(null, executeDate);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date getToday(Date dataDueTime) {
        return DateUtils.firstSecondOfDate(dataDueTime);
    }

    public static Date getLastSecondToday(Date dataDueTime) {
        return DateUtils.lastSecondOfDate(dataDueTime);
    }

    public static Date getYesterday(Date dataDueTime) {
        return DateUtils.getNextNDay(DateUtils.firstSecondOfDate(dataDueTime), -1);
    }

    public static Date getLastSecondYesterday(Date dataDueTime) {
        return DateUtils.getNextNDay(DateUtils.lastSecondOfDate(dataDueTime), -1);
    }

    /**
     * 一周的第一天第一秒，周一算第一天
     *
     * @param dataDueTime
     * @return
     */
    public static Date getFirstSecondThisWeek(Date dataDueTime) {
        return DateUtils.getLastMondayOfWeek(dataDueTime);
    }

    public static Date getFirstSecondThisMonth(Date dataDueTime) {
        return DateUtils.getFirstDayOfMonth(dataDueTime);
    }

    public static Date getFirstSecondThisQuarter(Date dataDueTime) {
        return DateUtils.getFirstDayOfQuarter(dataDueTime);
    }

    public static Date getFirstSecondThisYear(Date dataDueTime) {
        return DateUtils.getFirstDayOfYear(dataDueTime);
    }

    public static Date getFirstSecondThisHour(Date dataDueTime) {
        return DateUtils.firstSecondOfHour(dataDueTime);
    }

    public static Date getLastSecondThisHour(Date dataDueTime) {
        return DateUtils.lastSecondOfHour(dataDueTime);
    }


}
