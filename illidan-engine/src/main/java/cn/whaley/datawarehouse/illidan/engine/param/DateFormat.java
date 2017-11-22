package cn.whaley.datawarehouse.illidan.engine.param;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lituo on 2017/6/28.
 */
public enum DateFormat {

    SHORT_DATE(1, "shortdate", "yyyyMMdd"),
    DATE(2, "date", "yyyy-MM-dd"),
    DATETIME(3, "datetime", "yyyy-MM-dd HH:mm:ss"),
    SHORT_DATE_WITH_HOUR(4, "shortdatewithhour", "yyyyMMddHH");

    private int code;
    private String name;
    private String dateFormat;

    private DateFormat(int code, String name, String dateFormat) {
        this.code = code;
        this.name = name;
        this.dateFormat = dateFormat;
    }

    public static String format(Date date, String param) {
        if (date == null) {
            return null;
        }
        DateFormat df = null;
        try {
            int code = Integer.parseInt(param.trim());
            df = getByCode(code);
        } catch (Exception e) {

        }
        if (df == null) {
            df = getByName(param);
        }

        String dateFormatStr;
        if (df != null) {
            dateFormatStr = df.getDateFormat();
        } else {
            dateFormatStr = param.trim();
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
            return sdf.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static DateFormat getByCode(int code) {
        for (DateFormat df : DateFormat.values()) {
            if (df.getCode() == code) {
                return df;
            }
        }
        return null;
    }

    public static DateFormat getByName(String name) {
        for (DateFormat df : DateFormat.values()) {
            if (df.getName().equalsIgnoreCase(name.trim())) {
                return df;
            }
        }
        return null;
    }

    public Date parse(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat(this.getDateFormat());
        sdf.setLenient(false);
        try {
            return sdf.parse(str);
        } catch (Exception e) {
            return null;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
