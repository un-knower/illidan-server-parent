package cn.whaley.datawarehouse.illidan.common.processor;

import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lituo on 2017/11/8.
 */
@Service
public class TableProcessor {

    @Autowired
    private JdbcFactory jdbcFactory;

    @Autowired
    private TableInfoService tableInfoService;

    public boolean createTable(TableWithField hiveTable) {
        return true;
    }

    public boolean changeColumn() {
        return true;
    }

    private boolean createHiveTable(TableWithField hiveTable) {
        return true;
    }

    private boolean createMysqlTable(TableWithField mysqlTable) {
        return true;
    }

    private boolean dropHiveTable(TableWithField hiveTable) {
        return true;
    }

    private boolean addHiveTableColumns(TableWithField hiveTable) {
        return true;
    }

    private boolean addMysqlTableColumns(TableWithField mysqlTable) {
        return true;
    }

}
