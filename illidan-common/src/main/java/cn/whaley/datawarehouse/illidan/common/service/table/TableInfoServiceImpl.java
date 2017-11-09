package cn.whaley.datawarehouse.illidan.common.service.table;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.mapper.table.TableInfoMapper;
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
    public Long removeByIds(List<Long> ids) {
        if (ids == null || ids.size()<=0){
            logger.error("removeByIds: id list is null.");
        }
        return tableInfoMapper.removeByIds(ids);
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
}
