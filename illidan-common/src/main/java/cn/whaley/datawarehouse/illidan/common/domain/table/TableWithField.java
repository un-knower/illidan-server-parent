package cn.whaley.datawarehouse.illidan.common.domain.table;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
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
    private DbInfoWithStorage dbInfo;

    public boolean containsField(FieldInfo fieldInfo) {
        for(FieldInfo field : fieldList) {
            if(field.same(fieldInfo)) {
                return true;
            }
        }
        return false;
    }

    public List<FieldInfo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FieldInfo> fieldList) {
        this.fieldList = fieldList;
    }

    public DbInfoWithStorage getDbInfo() {
        return dbInfo;
    }

    public void setDbInfo(DbInfoWithStorage dbInfo) {
        this.dbInfo = dbInfo;
    }
}
