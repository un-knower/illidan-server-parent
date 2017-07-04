package cn.whaley.datawarehouse.illidan.common.service.field;

import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.mapper.field.FieldInfoMapper;
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
    @Autowired
    private FieldInfoMapper fieldInfoMapper;

    public FieldInfo get(final Long id) {
        return fieldInfoMapper.get(id);
    }

    public List<FieldInfo> getByTableId(final Long tableId) {
        List<FieldInfo> fieldInfos = fieldInfoMapper.getByTableId(tableId);
        if(fieldInfos == null) {
            return new ArrayList<FieldInfo>();
        }
        return fieldInfos;
    }

    @Override
    public void insertBatch(List<FieldInfo> list) {
        fieldInfoMapper.insertBatch(list);
    }

    @Override
    public void removeByTableId(Long tableId) {
        fieldInfoMapper.removeByTableId(tableId);
    }

    public List<String> findPartitionFields(final Long tableId){
        List<FieldInfo> fieldInfos = getByTableId(tableId);
        List<String> partitionFields = new ArrayList<String>();
        for(FieldInfo fieldInfo : fieldInfos) {
            if("1".equals(fieldInfo.getIsPartitionCol())) {
                partitionFields.add(fieldInfo.getColName());
            }
        }
        return partitionFields;
    }

    public Long updateById(final FieldInfo fieldInfo){
        return fieldInfoMapper.updateById(fieldInfo);
    }

    public void setFiledValue(List<FieldInfo> fieldInfos){
        for (FieldInfo fieldInfo:fieldInfos){
            fieldInfo.setColType("string");
            fieldInfo.setIsPartitionCol("1");
            fieldInfo.setCreateTime(new Date());
            fieldInfo.setUpdateTime(new Date());
            if (fieldInfo.getColName().equals("date_type")){
                fieldInfo.setColDes("日期类型");
                fieldInfo.setColIndex(1);
            } else if (fieldInfo.getColName().equals("product_line")){
                fieldInfo.setColDes("产品线");
                fieldInfo.setColIndex(2);
            } else if (fieldInfo.getColName().equals("month_p")){
                fieldInfo.setColDes("月分区");
                fieldInfo.setColIndex(3);
            } else if (fieldInfo.getColName().equals("day_p")){
                fieldInfo.setColDes("天分区");
                fieldInfo.setColIndex(4);
            }
        }

    }

}
