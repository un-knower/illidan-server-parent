package cn.whaley.datawarehouse.illidan.engine.param;

import cn.whaley.datawarehouse.illidan.engine.util.DateUtils;

import java.util.Date;

import static cn.whaley.datawarehouse.illidan.engine.param.DateParam.*;

/**
 * Created by lituo on 2017/6/29.
 */
public enum ExecuteIntervalType {
    HOUR("hour", FIRST_SECOND_THIS_HOUR, LAST_SECOND_THIS_HOUR),
    DAY("day", FIRST_SECOND_TODAY, LAST_SECOND_TODAY),
    WEEK("week", FIRST_SECOND_THIS_WEEK, LAST_SECOND_TODAY),
    MONTH("month", FIRST_SECOND_THIS_MONTH, LAST_SECOND_TODAY),
    QUARTER("quarter", FIRST_SECOND_THIS_QUARTER, LAST_SECOND_TODAY),
    YTD("ytd", FIRST_SECOND_THIS_YEAR, LAST_SECOND_TODAY);


    private String name;
    private DateParam startParam;
    private DateParam endParam;

    ExecuteIntervalType(String name, DateParam startParam, DateParam endParam) {
        this.name = name;
        this.startParam = startParam;
        this.endParam = endParam;
    }

    public static ExecuteIntervalType getByName(String name) {
        for (ExecuteIntervalType type : ExecuteIntervalType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public boolean shouldExecute(Date dataDate) {
        switch (this) {
            case HOUR:
                return dataDate.equals(DateUtils.lastSecondOfHour(dataDate));
            case DAY:
                return dataDate.equals(DateUtils.lastSecondOfDate(dataDate));
            case WEEK:
                return dataDate.equals(DateUtils.getLastSecondOfWeek(dataDate));
            case MONTH:
                return dataDate.equals(DateUtils.getLastSecondOfMonth(dataDate));
            case QUARTER:
                return dataDate.equals(DateUtils.getLastSecondOfQuarter(dataDate));
            case YTD:
                return dataDate.equals(DateUtils.lastSecondOfDate(dataDate));
            default:
                return false;
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateParam getStartParam() {
        return startParam;
    }

    public void setStartParam(DateParam startParam) {
        this.startParam = startParam;
    }

    public DateParam getEndParam() {
        return endParam;
    }

    public void setEndParam(DateParam endParam) {
        this.endParam = endParam;
    }
}
