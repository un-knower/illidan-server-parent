package cn.whaley.datawarehouse.illidan.server.service;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.processor.JdbcFactory;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.common.service.field.FieldInfoService;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoService;
import cn.whaley.datawarehouse.illidan.server.util.SqlParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lituo on 2017/11/14.
 */
@Service
public class TableFieldService {

    @Autowired
    private TableInfoService tableInfoService;

    @Autowired
    private FieldInfoService fieldInfoService;

    @Autowired
    private DbInfoService dbInfoService;

    @Autowired
    private JdbcFactory jdbcFactory;


    /**
     * 通过与hive交互获取字段、表名等信息
     *
     * @param hiveTableId hive表id（系统内id）
     * @return
     */
    public TableWithField getTableInfoFromHive(Long hiveTableId) {
        TableWithField incompleteTable = tableInfoService.getTableWithField(hiveTableId);

        int maxColIndex = 0;

        for (FieldInfo fieldInfo : incompleteTable.getFieldList()) {
            if (fieldInfo.getColIndex() > maxColIndex) {
                maxColIndex = fieldInfo.getColIndex();
            }
        }

        String dbName = incompleteTable.getDbInfo().getDbCode();
        String tableName = incompleteTable.getTableCode();
        String createSql = getCreateSqlFromHive(dbName, tableName);

        TableWithField result = parseHiveFromCreateSql(createSql, incompleteTable.getDbInfo().getDbCode());

        for (FieldInfo fieldInfo : result.getFieldList()) {
            fieldInfo.setTableId(hiveTableId);
        }
        result.setId(hiveTableId);

        return result;
    }

    /**
     * 通过建表sql语句，解析得到表信息
     *
     * @param sql    建表语句
     * @param dbName 数据库名，如果sql中包含了数据库名，则以sql中的为准
     * @return
     */
    public TableWithField parseHiveFromCreateSql(String sql, String dbName) {

        SqlParseUtil.Table table = SqlParseUtil.parseCreateSql(sql, dbName);

        TableWithField tableWithField = new TableWithField();
        tableWithField.setTableCode(table.tableCode);
        tableWithField.setTableDes(table.comment);

        DbInfoWithStorage dbInfo = dbInfoService.getDbWithStorageByCode(dbName);
        tableWithField.setDbInfo(dbInfo);
        tableWithField.setDbId(dbInfo.getId());

        tableWithField.setDataType("parquet");

        List<FieldInfo> fieldInfos = new ArrayList<>();
        tableWithField.setFieldList(fieldInfos);
        int index = 0;
        for (SqlParseUtil.Field field : table.fields) {
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setTableId(null);
            fieldInfo.setColName(field.fieldCode);
            fieldInfo.setColType(field.fieldType);
            fieldInfo.setColDes(field.comment);
            fieldInfo.setIsPartitionCol(field.partitionColumn ? "1" : "0");
            index++;
            fieldInfo.setColIndex(index);
            fieldInfos.add(fieldInfo);
        }

        return tableWithField;
    }

    /**
     * 通过表名和库名，到hive中去获取建表语句
     *
     * @param dbName
     * @param tableName
     * @return
     */
    public String getCreateSqlFromHive(String dbName, String tableName) {

        JdbcTemplate jdbcTemplate = jdbcFactory.getJdbcTemplate(dbName);
        String sql = String.format("show create table `%s.%s`", dbName, tableName);
        String createSql = jdbcTemplate.execute(sql, (PreparedStatementCallback<String>) ps -> {
            ResultSet rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                String line = rs.getString(1).trim();
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        });
        return createSql;
    }

/*    public void completeTableInfo() {
        List<TableInfo> tableInfos = tableInfoService.findByTableInfo(new TableInfoQuery());
        for(TableInfo tableInfo: tableInfos) {
            List<FieldInfo> fieldInfos = getRealColumnsFromHive(tableInfo);
            if(fieldInfos != null && fieldInfos.size() > 0) {
                fieldInfoService.insertBatch(fieldInfos);
            }
        }
    }*/
}
