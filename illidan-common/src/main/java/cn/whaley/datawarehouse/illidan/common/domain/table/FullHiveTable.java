package cn.whaley.datawarehouse.illidan.common.domain.table;

/**
 * Created by lituo on 2017/11/9.
 */
public class FullHiveTable extends TableWithField {
    private static final long serialVersionUID = 1L;

    private TableInfo mysqlTable;

    public TableInfo getMysqlTable() {
        return mysqlTable;
    }

    public void setMysqlTable(TableInfo mysqlTable) {
        this.mysqlTable = mysqlTable;
    }
}
