package cn.whaley.datawarehouse.illidan.common.service.field;

import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.mapper.field.FieldInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return fieldInfoMapper.getByTableId(tableId);
    }

    @Override
    public void insertBatch(List<FieldInfo> list) {
        fieldInfoMapper.insertBatch(list);
    }

    @Override
    public void removeByTableId(Long tableId) {
        fieldInfoMapper.removeByTableId(tableId);
    }
}
