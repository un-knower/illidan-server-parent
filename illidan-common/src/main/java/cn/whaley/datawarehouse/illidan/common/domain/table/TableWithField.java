package cn.whaley.datawarehouse.illidan.common.domain.table;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;

import java.util.List;

/**
 * Created by lituo on 2017/6/28.
 */
public class TableWithField extends TableInfo {

    /**
     * 目标表包含的字段
     */
    private List<FieldInfo> fieldList;

    /**
     * 目标表所属数据库
     */
    private DbInfo dbInfo;

    public List<FieldInfo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FieldInfo> fieldList) {
        this.fieldList = fieldList;
    }

    public DbInfo getDbInfo() {
        return dbInfo;
    }

    public void setDbInfo(DbInfo dbInfo) {
        this.dbInfo = dbInfo;
    }
}
