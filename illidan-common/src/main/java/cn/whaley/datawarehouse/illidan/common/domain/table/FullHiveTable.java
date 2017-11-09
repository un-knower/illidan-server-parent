package cn.whaley.datawarehouse.illidan.common.domain.table;

import java.io.Serializable;

/**
 * Created by lituo on 2017/11/9.
 */
public class FullHiveTable implements Serializable {
    private static final long serialVersionUID = 1L;

    private TableWithField hiveTable;

    private TableInfo mysqlTable;

    public TableWithField getHiveTable() {
        return hiveTable;
    }

    public void setHiveTable(TableWithField hiveTable) {
        this.hiveTable = hiveTable;
    }

    public TableInfo getMysqlTable() {
        return mysqlTable;
    }

    public void setMysqlTable(TableInfo mysqlTable) {
        this.mysqlTable = mysqlTable;
    }
}
