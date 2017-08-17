package cn.whaley.datawarehouse.illidan.export.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by lituo on 2017/8/15.
 */
public class PhoenixUtils {

    private static Logger logger = LoggerFactory.getLogger(MysqlUtils.class);

    public static String getUpsertSql(List<String> columns, List<String> keys, Map<String, String> map) {
        String tableName = map.get("phoenixTable");
        String database = map.get("phoenixDb");
        //字段拼接
        StringBuffer fieldSb = new StringBuffer();
        //问号拼接
        StringBuffer markSb = new StringBuffer();
        for (String key : columns) {
            fieldSb.append(key.split("\\.")[1]);
            fieldSb.append(",");
            markSb.append("?,");
        }
        String fields = fieldSb.toString();
        fields = fields.substring(0, fields.length() - 1);
        String marks = markSb.toString();
        marks = marks.substring(0, marks.length() - 1);
        //拼接sql
        String insertSql = "insert into " + database + "." + tableName + " (" + fields + ") values(" + marks + ")";

        //TODO on duplicate
        return null;
    }
}
