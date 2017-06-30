package cn.whaley.datawarehouse.illidan.engine.param;

import java.util.Date;

/**
 * Created by lituo on 2017/6/28.
 */
public enum DateParam {

    TODAY("today", "today", "yyyy-MM-dd"),
    DAY("day", "today", "yyyy-MM-dd"),
    DAY_P("day_p", "today", "yyyyMMdd"),
    MONTH_P("month_p", "today", "MM"),
    FIRST_SECOND_TODAY("first_second_today", "today", "yyyy-MM-dd HH:mm:ss"),
    LAST_SECOND_TODAY("last_second_today", "lastSecondToday", "yyyy-MM-dd HH:mm:ss"),
    FIRST_SECOND_THIS_WEEK("first_second_this_week", "firstSecondThisWeek", "yyyy-MM-dd HH:mm:ss"),
    FIRST_SECOND_THIS_MONTH("first_second_this_month", "firstSecondThisMonth", "yyyy-MM-dd HH:mm:ss"),
    FIRST_SECOND_THIS_QUARTER("first_second_this_quarter", "firstSecondThisQuarter", "yyyy-MM-dd HH:mm:ss"),
    FIRST_SECOND_THIS_YEAR("first_second_this_year", "firstSecondThisYear", "yyyy-MM-dd HH:mm:ss");

    private String paramName;
    private String methodName;
    private String defaultDateFormat;

    DateParam(String paramName, String methodName, String defaultDateFormat) {
        this.paramName = paramName;
        this.methodName = methodName;
        this.defaultDateFormat = defaultDateFormat;
    }

    public static Date getParamDate(String paramName, Date dataDueTime) {
        DateParam dateParam = findByParamName(paramName);
        if (dateParam == null) {
            return null;
        }
        return DateParamMethod.invokeMethod(dataDueTime, dateParam.getMethodName());
    }

    public static String getDateFormatByParamName(String paramName) {
        DateParam dateParam = findByParamName(paramName);
        if (dateParam == null) {
            return null;
        }
        return dateParam.getDefaultDateFormat();
    }

    public static DateParam findByParamName(String paramName) {
        for (DateParam dateParam : DateParam.values()) {
            if (dateParam.getParamName().equals(paramName)) {
                return dateParam;
            }
        }
        return null;
    }


    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDefaultDateFormat() {
        return defaultDateFormat;
    }

    public void setDefaultDateFormat(String defaultDateFormat) {
        this.defaultDateFormat = defaultDateFormat;
    }
}
