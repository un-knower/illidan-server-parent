package cn.whaley.datawarehouse.illidan.common.processor;

import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.FullHiveTable;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lituo on 2017/11/8.
 */
@Service
public class TableProcessor {

    @Autowired
    private JdbcFactory jdbcFactory;

    @Autowired
    private TableInfoService tableInfoService;

    public boolean createTable(Long hiveTableId) {
        FullHiveTable fullHiveTable = tableInfoService.getFullHiveTable(hiveTableId);
        TableWithField hiveTable = fullHiveTable.getHiveTable();
        createHiveTable(hiveTable);
        try {
            TableWithField mysqlTable = (TableWithField)fullHiveTable.getMysqlTable();
            if(mysqlTable != null) {
                createMysqlTable(mysqlTable, hiveTable);
            }
        } catch (Exception e) {
            dropHiveTable(hiveTable);
            throw e;
        }
        return true;
    }

    public boolean changeColumn() {
        return true;
    }

    private boolean createHiveTable(TableWithField hiveTable) {
        JdbcTemplate jdbcTemplate = jdbcFactory.create(hiveTable.getDbInfo().getDbCode());
        String createSql = assemblyHiveCreateSql(hiveTable);
        jdbcTemplate.update(createSql);
        return true;
    }


    private boolean createMysqlTable(TableWithField mysqlTable, TableWithField hiveTable) {
        JdbcTemplate jdbcTemplate = jdbcFactory.create(mysqlTable.getDbInfo().getDbCode());
        String createSql = assemblyMysqlCreateSql(mysqlTable, hiveTable);
//        jdbcTemplate.update(createSql); TODO 暂不执行
        return true;
    }

    private boolean dropHiveTable(TableWithField hiveTable) {
        JdbcTemplate jdbcTemplate = jdbcFactory.create(hiveTable.getDbInfo().getDbCode());
        String createSql = assemblyHiveDropSql(hiveTable);
        jdbcTemplate.update(createSql);
        return true;
    }

    private boolean addHiveTableColumns(TableWithField hiveTable) {
        return true;
    }

    private boolean addMysqlTableColumns(TableWithField mysqlTable) {
        return true;
    }

    private String assemblyHiveCreateSql(TableWithField hiveTable) {
        List<FieldInfo> fieldInfoList = hiveTable.getFieldList();
        String sql = "CREATE TABLE `" + hiveTable.getDbInfo().getDbCode()+ "." + hiveTable.getTableCode() + "`( \n";

        sql += fieldInfoList.stream().sorted(Comparator.comparingInt(FieldInfo::getColIndex))
                .filter(f -> !"1".equals(f.getIsPartitionCol()))
                .map(f -> f.getColName() + " " + f.getColType() + " COMMENT '"+ f.getColDes() +"'")
                .collect(Collectors.joining(",\n"));
        sql += ") COMMENT '" + hiveTable.getTableDes() + "'\n";
        sql += " PARTITIONED BY (" +
                fieldInfoList.stream().sorted(Comparator.comparingInt(FieldInfo::getColIndex))
                        .filter(f -> "1".equals(f.getIsPartitionCol()))
                        .map(f -> f.getColName() + " " + f.getColType()).collect(Collectors.joining(", "))
                + ")\n";
        sql += " STORED AS PARQUET\n";
        return sql;
    }

    private String assemblyHiveDropSql(TableWithField hiveTable) {
        return "DROP TABLE IF EXISTS " + hiveTable.getDbInfo().getDbCode()+ "." + hiveTable.getTableCode();
    }

    private String assemblyMysqlCreateSql(TableWithField mysqlTable, TableWithField hiveTable) {
        List<FieldInfo> fieldInfoList = hiveTable.getFieldList();

        String sql = "CREATE TABLE `" + mysqlTable.getDbInfo().getDbCode()+ "." + mysqlTable.getTableCode() + "`( \n";

        sql += fieldInfoList.stream().sorted(Comparator.comparingInt(FieldInfo::getColIndex))
                .map(f ->
                "`" + f.getColName() + "` " + f.getColType() + " DEFAULT NULL COMMENT '"+ f.getColDes() +"'")
                .collect(Collectors.joining(",\n"));
        sql += ") COMMENT '" + hiveTable.getTableDes() + "'\n";

        return sql;
    }

}
