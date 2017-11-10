package cn.whaley.datawarehouse.illidan.common.service.table;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.FullHiveTable;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.mapper.table.TableInfoMapper;
import cn.whaley.datawarehouse.illidan.common.processor.TableProcessor;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.common.service.field.FieldInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Service
public class TableInfoServiceImpl implements TableInfoService {
    private Logger logger = LoggerFactory.getLogger(TableInfoServiceImpl.class);
    @Autowired
    private TableInfoMapper tableInfoMapper;

    @Autowired
    private DbInfoService dbInfoService;

    @Autowired
    private FieldInfoService fieldInfoService;

    @Autowired
    private TableProcessor tableProcessor;

    public TableInfo get(final Long id) {
        if (id == null){
            logger.error("get: id is null.");
            return null;
        }
        return tableInfoMapper.get(id);
    }

    @Override
    public Long insert(TableInfo tableInfo) throws Exception {
        if (tableInfo == null){
            logger.error("insert: tableInfo is null.");
            return null;
        }
        tableInfo.setStatus("1");
        Long count = tableInfoMapper.isExistTableInfo(tableInfo.getTableCode(),tableInfo.getDbId());
        if (count == null){
            logger.error("insert: count is null.");
            return null;
        }
        if (count > 0) {
            throw new Exception("表已经存在不能重复新增");
        }
        tableInfoMapper.insert(tableInfo);
        return tableInfo.getId();
    }

    public TableWithField getTableWithField(final Long id) {
        if (id == null){
            logger.error("getTableWithField: id is null.");
            return null;
        }
        //目标表实体
        TableInfo tableInfo = tableInfoMapper.get(id);
        if (tableInfo == null){
            logger.error("getTableWithField: tableInfo is null. tableId: "+id);
            return null;
        }
        if(!"1".equals(tableInfo.getStatus())) {
            logger.error("getTableWithField: table is invalid. tableId: "+id);
            return null;
        }
        //目标数据库实体
        DbInfoWithStorage dbInfo = dbInfoService.getDbWithStorage(tableInfo.getDbId());
        if (dbInfo == null){
           logger.error("getTableWithField: dbInfo is null. tableId: "+id+", dbId: "+tableInfo.getDbId());
           return null;
        }
        //列名实体
        List<FieldInfo> fieldInfoList = fieldInfoService.getByTableId(tableInfo.getId());
//        if (fieldInfoList == null || fieldInfoList.size()<=0){
//            logger.error("getTableWithField: fieldInfoList is null. tableId: "+id+", dbId: "+tableInfo.getDbId());
//            return null;
//        }
        TableWithField tableWithField = new TableWithField();
        BeanUtils.copyProperties(tableInfo, tableWithField);
        tableWithField.setDbInfo(dbInfo);
        tableWithField.setFieldList(fieldInfoList);

        return tableWithField;
    }

    public List<TableInfo> findByTableInfo(final TableInfoQuery tableInfo){
        if (tableInfo == null){
            logger.error("findByTableInfo: tableInfo is null.");
            return null;
        }
        return tableInfoMapper.findByTableInfo(tableInfo);
    }

    public TableInfo findOne(final TableInfoQuery tableInfo) {
        if (tableInfo == null){
            logger.error("findOne: tableInfo is null.");
            return null;
        }
        tableInfo.setLimitStart(0);
        tableInfo.setLimitEnd(1);
        List<TableInfo> datas = tableInfoMapper.findByTableInfo(tableInfo);
        if (datas != null && datas.size() > 0) {
            return datas.get(0);
        }
        logger.error("findOne: data list is null. tableInfo: "+tableInfo.toString());
        return null;
    }

    public Long updateById(final TableInfo tableInfo){
        if (tableInfo == null){
            logger.error("updateById: tableInfo is null.");
            return null;
        }
        return tableInfoMapper.updateById(tableInfo);
    }

    @Override
    public Long removeById(Long id) {
        if (id == null){
            logger.error("removeByIds: id is null.");
        }
        return tableInfoMapper.removeById(id);
    }

    public Long countByTableInfo(final TableInfoQuery tableInfo) {
        if (tableInfo == null){
            logger.error("countByTableInfo: tableInfo is null.");
            return null;
        }
        return tableInfoMapper.countByTableInfo(tableInfo);
    }

    @Override
    public HashMap<Long,Long> insertTableWithField(List<TableWithField> tableList) throws Exception {
        HashMap<Long,Long> tableIdMap = new HashMap<Long,Long>();
        for (int i=0; i<=tableList.size()-1; ++i) {
            DbInfoWithStorage dbInfo = dbInfoService.getDbWithStorage(tableList.get(i).getDbId());
            if (dbInfo == null){
                return null;
            }
            //存储类型,1:hive,2:mysql
            Long storageType = dbInfo.getStorageType();
            TableWithField tableWithField = tableList.get(i);
            tableWithField.setDbInfo(dbInfo);
            TableInfo tableInfo = new TableInfo();
            //复制tableWithField信息到tableInfo中
            BeanUtils.copyProperties(tableWithField, tableInfo);
            //插入表信息,并返回其主键id
            if(tableIdMap.get(2L)!=null){
                tableInfo.setMysqlTableId(tableIdMap.get(2L));
            }
            Long tableId = insert(tableInfo);
            if (tableId == null){
                logger.error("insertTableWithField: 插入table_info返回的tableId is null. tableInfo: "+tableInfo.toString());
                return null;
            }
            tableIdMap.put(storageType,tableId);

            //插入字段到field_info
            List<FieldInfo> fieldInfoList = tableWithField.getFieldList();
            List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
            if (fieldInfoList!=null && fieldInfoList.size()>0){
                for (FieldInfo f : fieldInfoList) {
                    FieldInfo fieldInfo = new FieldInfo();
                    BeanUtils.copyProperties(f, fieldInfo);
                    fieldInfo.setTableId(tableId);
                    fieldInfos.add(fieldInfo);
                }
                //批量插入fieldInfos
                //1.删除历史记录
                fieldInfoService.removeByTableId(tableId);
                //2.批量插入
                fieldInfoService.insertBatch(fieldInfos);
            }
        }
        return tableIdMap;
    }

    @Override
    public Long insertFullHiveTable(FullHiveTable table) throws Exception {
        if (table == null) {
            throw new RuntimeException("参数不合法");
        }

        //保存hive和mysql表描述
        TableWithField hiveTable = table.getHiveTable();
        Long mysqlTableId = null;
        if (table.getMysqlTable() != null) {
            TableWithField mysqlTable = new TableWithField();
            BeanUtils.copyProperties(table.getMysqlTable(), mysqlTable);
            mysqlTableId = insertTableWithField(mysqlTable);
        }
        hiveTable.setMysqlTableId(mysqlTableId);

        Long hiveTableId = null;
        try {
            hiveTableId = insertTableWithField(hiveTable);
        } catch (Exception e) {
            if(mysqlTableId != null) {
                removeById(mysqlTableId);
            }
            throw e;
        }

        //创建实体表
        try {
            tableProcessor.createTable(hiveTableId);
        } catch (Exception e) {
            logger.error("创建目标表失败", e);
            //回滚保存的配置
            removeById(hiveTableId);
            if(mysqlTableId != null) {
                removeById(mysqlTableId);
            }
            throw e;
        }

        return hiveTableId;
    }


    public Long updateFullHiveTable(final FullHiveTable table) throws Exception {
        if (table == null) {
            throw new RuntimeException("参数不合法");
        }
        Long hiveTableId = table.getHiveTable().getId();
        if (hiveTableId == null || hiveTableId <= 0) {
            throw new RuntimeException("tableId不合法");
        }
        TableWithField oldHiveTable = getTableWithField(hiveTableId);
        if (oldHiveTable == null) {
            throw new RuntimeException("table不存在, tableId = " + hiveTableId);
        }

        TableWithField hiveTable = table.getHiveTable();

        List<FieldInfo> newFields = new ArrayList<>();
        int containSize = 0;
        for (FieldInfo fieldInfo : hiveTable.getFieldList()) {
            if (oldHiveTable.getFieldList().contains(fieldInfo)) {
                containSize++;
            } else {
                fieldInfo.setTableId(hiveTableId);
                newFields.add(fieldInfo);
            }
        }
        if (containSize != oldHiveTable.getFieldList().size()) {
            throw new RuntimeException("table已有的字段临时不支持修改");
        }

        //处理mysql配置变动的情况,如果变动，新信息中管理的mysqlId需要设为null
        if(hiveTable.getMysqlTableId() == null && oldHiveTable.getMysqlTableId() != null) {
            if(table.getMysqlTable() == null) {
                updateById(hiveTable);
            } else {
                removeById(oldHiveTable.getMysqlTableId());
                TableWithField mysqlTable = new TableWithField();
                BeanUtils.copyProperties(table.getMysqlTable(), mysqlTable);
                Long mysqlTableId = insertTableWithField(mysqlTable);
                hiveTable.setMysqlTableId(mysqlTableId);
                updateById(hiveTable);
            }
        }

        fieldInfoService.insertBatch(newFields);

        return 0L;
    }


    public FullHiveTable getFullHiveTable(final Long id) {
        TableWithField hiveTable = getTableWithField(id);
        FullHiveTable fullHiveTable = new FullHiveTable();
        fullHiveTable.setHiveTable(hiveTable);

        Long mysqlTableId = hiveTable.getMysqlTableId();
        if(mysqlTableId != null) {
            TableWithField mysqlTableWithDb = getTableWithField(mysqlTableId);
            fullHiveTable.setMysqlTable(mysqlTableWithDb);
        }

        return fullHiveTable;
    }

    @Override
    public Boolean isExport2Mysql(final Long id){
        if (id == null){
            logger.error("isExport2Mysql: id is null.");
            return null;
        }
        Boolean flag = false;
        if (tableInfoMapper.get(id).getMysqlTableId() != null){
            flag = true;
        }
        return flag;
    }


    private Long insertTableWithField(TableWithField tableWithField) throws Exception {

        DbInfoWithStorage dbInfo = dbInfoService.getDbWithStorage(tableWithField.getDbId());
        if (dbInfo == null) {
            return null;
        }
        //存储类型,1:hive,2:mysql
        Long storageType = dbInfo.getStorageType();
        tableWithField.setDbInfo(dbInfo);
        TableInfo tableInfo = new TableInfo();
        //复制tableWithField信息到tableInfo中
        BeanUtils.copyProperties(tableWithField, tableInfo);
        //插入表信息,并返回其主键id
        Long tableId = insert(tableInfo);
        if (tableId == null || tableId <= 0) {
            logger.error("insertTableWithField: 插入table_info返回的tableId is null. tableInfo: " + tableInfo.toString());
            throw new RuntimeException("保存表失败，tableCode = " + tableWithField.getTableCode());
        }

        //插入字段到field_info
        List<FieldInfo> fieldInfoList = tableWithField.getFieldList();
        List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
        if (fieldInfoList != null && fieldInfoList.size() > 0) {
            for (FieldInfo f : fieldInfoList) {
                FieldInfo fieldInfo = new FieldInfo();
                BeanUtils.copyProperties(f, fieldInfo);
                fieldInfo.setTableId(tableId);
                fieldInfos.add(fieldInfo);
            }
            //批量插入fieldInfos
            //1.删除历史记录
            fieldInfoService.removeByTableId(tableId);
            //2.批量插入
            fieldInfoService.insertBatch(fieldInfos);
        }
        return tableId;
    }

}
