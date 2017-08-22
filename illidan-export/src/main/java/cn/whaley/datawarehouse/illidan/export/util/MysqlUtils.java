package cn.whaley.datawarehouse.illidan.export.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by guohao on 2017/7/14.
 */
public class MysqlUtils {
    private static Logger logger = LoggerFactory.getLogger(MysqlUtils.class);

    /**
     * 拼接insert sql
     *
     * @param columns 从hive获取的数据列名
     * @return
     */
    static public String getInsertSql(List<String> columns, Map<String, String> map) {

        String tableName = map.get("mysqlTable");
        String database = map.get("mysqlDb");
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
        logger.info("insertSql is " + insertSql);
        return insertSql;
    }

    static public String getDeleteSql(Map<String, String> map) {
        String tableName = map.get("mysqlTable");
        String database = map.get("mysqlDb");
        String filerCondition = map.get("filterCondition");
        String deleteSql = "delete from " + database + "." + tableName + " " + filerCondition;
        logger.info("deleteSql is " + deleteSql);
        return deleteSql;
    }

}
