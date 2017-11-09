package cn.whaley.datawarehouse.illidan.common.service.field;

import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.mapper.field.FieldInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Service
public class FieldInfoServiceImpl implements FieldInfoService {
    private Logger logger = LoggerFactory.getLogger(FieldInfoServiceImpl.class);
    @Autowired
    private FieldInfoMapper fieldInfoMapper;

    public FieldInfo get(final Long id) {
        if (id == null){
            logger.error("get: id is null.");
            return null;
        }
        return fieldInfoMapper.get(id);
    }

    public List<FieldInfo> getByTableId(final Long tableId) {
        if (tableId == null){
            logger.error("getByTableId: tableId is null.");
            return null;
        }
        return fieldInfoMapper.getByTableId(tableId);
    }

    @Override
    public void insertBatch(List<FieldInfo> list) {
        if (list == null || list.size()<=0){
            logger.error("insertBatch: FieldInfo list is null.");
            return;
        }
        fieldInfoMapper.insertBatch(list);
    }

    @Override
    public void removeByTableId(Long tableId) {
        if (tableId == null){
            logger.error("removeByTableId: tableId is null.");
            return;
        }
        fieldInfoMapper.removeByTableId(tableId);
    }

    public List<String> findPartitionFields(final Long tableId){
        if (tableId == null){
            logger.error("findPartitionFields: tableId is null.");
            return null;
        }
        List<FieldInfo> fieldInfos = getByTableId(tableId);
        if (fieldInfos == null || fieldInfos.size()<=0){
            logger.error("findPartitionFields: fieldInfos is null. tableId: "+tableId);
            return null;
        }
        List<String> partitionFields = new ArrayList<String>();
        for(FieldInfo fieldInfo : fieldInfos) {
            if("1".equals(fieldInfo.getIsPartitionCol())) {
                partitionFields.add(fieldInfo.getColName());
            }
        }
        return partitionFields;
    }

    public Long updateById(final FieldInfo fieldInfo){
        if (fieldInfo == null){
            logger.error("updateById: fieldInfo is null.");
            return null;
        }
        return fieldInfoMapper.updateById(fieldInfo);
    }

    public void setFiledValue(List<FieldInfo> fieldInfos){
        if (fieldInfos == null || fieldInfos.size()<=0){
            logger.error("setFiledValue: fieldInfos is null.");
            return;
        }
        int i = 1;
        for (FieldInfo fieldInfo:fieldInfos){
            fieldInfo.setCreateTime(new Date());
            fieldInfo.setUpdateTime(new Date());
            fieldInfo.setColIndex(i);
            i++;
        }

    }

}
