package cn.whaley.datawarehouse.illidan.common.processor;

import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.FullHiveTable;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoService;
import cn.whaley.datawarehouse.illidan.common.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lituo on 2017/11/8.
 */
@Service
public class TableProcessor {
    private Logger logger = LoggerFactory.getLogger(TableProcessor.class);

    @Autowired
    private JdbcFactory jdbcFactory;

    @Autowired
    private TableInfoService tableInfoService;

    public boolean createTable(Long hiveTableId) {
        FullHiveTable fullHiveTable = tableInfoService.getFullHiveTable(hiveTableId);
        TableWithField hiveTable = fullHiveTable.getHiveTable();
        createHiveTable(hiveTable);
        try {
            TableWithField mysqlTable = (TableWithField) fullHiveTable.getMysqlTable();
            if (mysqlTable != null) {
                createMysqlTable(mysqlTable, hiveTable);
            }
        } catch (Exception e) {
            dropHiveTable(hiveTable);
            throw e;
        }
        return true;
    }

    public boolean addColumns(Long hiveTableId, List<FieldInfo> newColumns) {
        if (hiveTableId <= 0 || newColumns == null || newColumns.size() == 0) {
            return false;
        }
        FullHiveTable fullHiveTable = tableInfoService.getFullHiveTable(hiveTableId);
        TableWithField hiveTable = fullHiveTable.getHiveTable();
        addHiveTableColumns(hiveTable, newColumns);
        TableWithField mysqlTable = (TableWithField) fullHiveTable.getMysqlTable();
        if (mysqlTable != null) {
            addMysqlTableColumns(mysqlTable, newColumns);
        }
        return true;
    }

    private boolean createHiveTable(TableWithField hiveTable) {
        JdbcTemplate jdbcTemplate = jdbcFactory.create(hiveTable.getDbInfo().getDbCode());
        String createSql = assemblyHiveCreateSql(hiveTable);
        executeSqlWithRetry(jdbcTemplate, createSql, 2);
        return true;
    }


    public boolean createMysqlTable(TableWithField mysqlTable, TableWithField hiveTable) {
        JdbcTemplate jdbcTemplate = jdbcFactory.create(mysqlTable.getDbInfo().getDbCode());
        String createSql = assemblyMysqlCreateSql(mysqlTable, hiveTable);
        executeSqlWithRetry(jdbcTemplate, createSql, 2);
        return true;
    }

    public boolean dropHiveTable(TableWithField hiveTable) {
        JdbcTemplate jdbcTemplate = jdbcFactory.create(hiveTable.getDbInfo().getDbCode());
        String sql = assemblyHiveDropSql(hiveTable);
        executeSqlWithRetry(jdbcTemplate, sql, 2);
        return true;
    }

    public boolean dropMysqlTable(TableWithField mysqlTable) {
        JdbcTemplate jdbcTemplate = jdbcFactory.create(mysqlTable.getDbInfo().getDbCode());
        String sql = assemblyMySqlDropSql(mysqlTable);
        executeSqlWithRetry(jdbcTemplate, sql, 2);
        return true;
    }

    private boolean addHiveTableColumns(TableWithField hiveTable, List<FieldInfo> newColumns) {
        String sql = assemblyHiveAddColumnSql(hiveTable, newColumns);
        JdbcTemplate jdbcTemplate = jdbcFactory.create(hiveTable.getDbInfo().getDbCode());
        executeSqlWithRetry(jdbcTemplate, sql, 2);

        return true;
    }

    private boolean addMysqlTableColumns(TableWithField mysqlTable, List<FieldInfo> newColumns) {
        String sql = assemblyMysqlAddColumnSql(mysqlTable, newColumns);
        JdbcTemplate jdbcTemplate = jdbcFactory.create(mysqlTable.getDbInfo().getDbCode());
        jdbcTemplate.update(sql);
        executeSqlWithRetry(jdbcTemplate, sql, 2);
        return true;
    }

    /**
     * 重试方式执行语句
     * @param jdbcTemplate
     * @param sql
     * @param retry 重试次数，实际执行次数是retry + 1
     */
    private void executeSqlWithRetry(JdbcTemplate jdbcTemplate, String sql, int retry) {
        while (retry >= 0) {
            try {
                jdbcTemplate.update(sql);
                break;
            } catch (BadSqlGrammarException e) {
                logger.warn("语法错误\n", e);
                throw e;
            } catch (Exception e) {
                logger.warn("sql执行失败，还有" + retry + "次机会\n", e);
                if(retry == 0) {
                    throw e;
                }
            }
            retry--;
        }
    }

    private String assemblyHiveAddColumnSql(TableWithField hiveTable, List<FieldInfo> newColumns) {
        String sql = "ALTER TABLE `" + hiveTable.getDbInfo().getDbCode() + "`.`" + hiveTable.getTableCode()
                + "` ADD COLUMNS  (\n";
        sql += newColumns.stream().sorted(Comparator.comparingInt(FieldInfo::getColIndex))
                .filter(f -> !"1".equals(f.getIsPartitionCol()))
                .map(f -> "`" + f.getColName() + "` " + f.getColType() + " COMMENT '" + f.getColDes() + "'")
                .collect(Collectors.joining(",\n"));
        sql += ")";
        return sql;
    }

    private String assemblyMysqlAddColumnSql(TableWithField mysqlTable, List<FieldInfo> newColumns) {
        String sql = "ALTER TABLE `" + mysqlTable.getDbInfo().getDbCode() + "`.`" + mysqlTable.getTableCode()
                + "`\n";
        sql += newColumns.stream().sorted(Comparator.comparingInt(FieldInfo::getColIndex))
                .filter(f -> !"1".equals(f.getIsPartitionCol()))
                .map(f -> " ADD COLUMN " + f.getColName() + " " + mapColTypeToMysql(f.getColType()) + " COMMENT '" + f.getColDes() + "'")
                .collect(Collectors.joining(",\n"));
        return sql;
    }

    private String assemblyHiveCreateSql(TableWithField hiveTable) {
        List<FieldInfo> fieldInfoList = hiveTable.getFieldList();
        String sql = "CREATE TABLE `" + hiveTable.getDbInfo().getDbCode() + "`.`" + hiveTable.getTableCode() + "`( \n";

        sql += fieldInfoList.stream().sorted(Comparator.comparingInt(FieldInfo::getColIndex))
                .filter(f -> !"1".equals(f.getIsPartitionCol()))
                .map(f -> "`" + f.getColName() + "` " + f.getColType() + " COMMENT '" + f.getColDes() + "'")
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
//        return "DROP TABLE IF EXISTS " + hiveTable.getDbInfo().getDbCode()+ "." + hiveTable.getTableCode();
        return "ALTER TABLE " + hiveTable.getDbInfo().getDbCode() + "." + hiveTable.getTableCode() + " RENAME TO "
                + hiveTable.getDbInfo().getDbCode() + "."
                + "deleted_" + hiveTable.getTableCode() + "_" + DateUtils.shortDateFormatBySecond.get().format(new Date());
    }

    private String assemblyMysqlCreateSql(TableWithField mysqlTable, TableWithField hiveTable) {
        List<FieldInfo> fieldInfoList = hiveTable.getFieldList();

        String sql = "CREATE TABLE `" + mysqlTable.getDbInfo().getDbCode() + "`.`" + mysqlTable.getTableCode() + "`( \n";
        sql += " `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n";
        sql += fieldInfoList.stream().sorted(Comparator.comparingInt(FieldInfo::getColIndex))
                .map(f ->
                        "`" + f.getColName() + "` " + mapColTypeToMysql(f.getColType()) + " DEFAULT NULL COMMENT '" + f.getColDes() + "'")
                .collect(Collectors.joining(",\n"));
        sql += ",  PRIMARY KEY (`id`)";
        sql += ") COMMENT '" + hiveTable.getTableDes() + "'\n";

        return sql;
    }

    private String assemblyMySqlDropSql(TableWithField mysqlTable) {
        return "RENAME TABLE " + mysqlTable.getDbInfo().getDbCode() + "." + mysqlTable.getTableCode() + " TO "
                + mysqlTable.getDbInfo().getDbCode() + "."
                + "deleted_" + mysqlTable.getTableCode() + "_" +
                DateUtils.shortDateFormatBySecond.get().format(new Date());
    }

    private String mapColTypeToMysql(String hiveColType) {
        if (hiveColType.equalsIgnoreCase("string")) {
            return "varchar(256)";
        } else if (hiveColType.equalsIgnoreCase("INT")) {
            return "int(11)";
        } else if (hiveColType.equalsIgnoreCase("BIGINT")) {
            return "bigint(11)";
        } else if (hiveColType.equalsIgnoreCase("FLOAT")) {
            return "float";
        } else if (hiveColType.equalsIgnoreCase("DOUBLE")) {
            return "double";
        } else if (hiveColType.equalsIgnoreCase("TIMESTAMP")) {
            return "datetime";
        } else {
            throw new RuntimeException("不支持的hive数据类型：" + hiveColType);
        }
    }

}
