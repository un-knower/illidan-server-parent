package cn.whaley.datawarehouse.illidan.server.service;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.processor.JdbcFactory;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.common.service.field.FieldInfoService;
import cn.whaley.datawarehouse.illidan.common.service.table.TableInfoService;
import cn.whaley.datawarehouse.illidan.server.util.SqlParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lituo on 2017/11/14.
 */
@Service
public class TableFieldService {

    private static Logger logger = LoggerFactory.getLogger(TableFieldService.class);

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
            fieldInfo.setColIndex(fieldInfo.getColIndex() + maxColIndex);
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
        if(dbInfo == null) {
            throw new RuntimeException("hive数据库不存在");
        }
        tableWithField.setDbInfo(dbInfo);
        tableWithField.setDbId(dbInfo.getId());

        if(table.fileFormat != null) {
            tableWithField.setDataType(table.fileFormat);
        } else {
            tableWithField.setDataType("parquet");
        }

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
            try {
                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    String line = rs.getString(1).trim();
                    sb.append(line);
                    sb.append("\n");
                }
                return sb.toString();
            }finally {
                rs.close();
            }

        });
        return createSql;
    }

    /**
     * 检查对比所有hive表配置和对应的hive表中的实际字段
     * 如果配置的hive表在hive中不存在，会记录失败
     * 如果系统的配置中缺少字段，会自动补充
     * 如果配置中存在hive表实际中不存在的字段，会记录失败
     */
    public void completeTableInfoAll() {

        List<TableInfo> tableInfos = tableInfoService.findByTableInfo(new TableInfoQuery());
        logger.info("开始补充已有表配置的字段");
        int failCount = 0;
        List<TableInfo> failList = new ArrayList<>();
        for(TableInfo tableInfo: tableInfos) {
            try {
                completeTableInfo(tableInfo.getId());
            }catch (Exception e) {
                failCount ++;
                failList.add(tableInfo);
                logger.warn(tableInfo.getTableCode() + "表字段更新失败");
            }
        }
        logger.warn(failCount + "个表字段更新失败");
        logger.warn("失败表id：\n"
                + failList.stream().map(f-> f.getId().toString()).collect(Collectors.joining(",\n")));
    }

    public void completeTableInfo(Long hiveTableId) {
        TableWithField oldTable = tableInfoService.getTableWithField(hiveTableId);
        TableWithField tableWithField = getTableInfoFromHive(hiveTableId);
        List<FieldInfo> fieldInfos = tableWithField.getFieldList();

        List<FieldInfo> needAddFields = new ArrayList<>();

        for (FieldInfo fieldInfo : oldTable.getFieldList()) {
            if (!fieldInfo.existIn(tableWithField.getFieldList())) {
                throw new RuntimeException(tableWithField.getTableCode() + "表字段不匹配，"
                        + fieldInfo.getColName() + "在hive实体表中不存在");
            }
        }

        for (FieldInfo fieldInfo : fieldInfos) {
            if ("1".equals(fieldInfo.getIsPartitionCol())) {
                continue;
            }
            if (!fieldInfo.existIn(oldTable.getFieldList())) {
                needAddFields.add(fieldInfo);
            }
        }

        if (needAddFields.size() > 0) {
            fieldInfoService.insertBatch(needAddFields);
            logger.info(tableWithField.getTableCode() + "表成功增加" + needAddFields.size() + "个字段");
        } else {
            logger.info(tableWithField.getTableCode() + "表无需更新");
        }
    }
}
