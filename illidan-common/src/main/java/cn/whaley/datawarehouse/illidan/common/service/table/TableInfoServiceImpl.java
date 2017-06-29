package cn.whaley.datawarehouse.illidan.common.service.table;

import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.mapper.table.TableInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Service
public class TableInfoServiceImpl implements TableInfoService {
    @Autowired
    private TableInfoMapper tableInfoMapper;

    public TableInfo get(final Long id) {
        return tableInfoMapper.get(id);
    }

    @Override
    public Long insert(TableInfo tableInfo)  throws Exception{
        Long count = tableInfoMapper.isExistTableInfo(tableInfo.getTableCode());
        if (count >0 ){
            throw new Exception("表已经存在不能重复新增");
        }
        tableInfoMapper.insert(tableInfo);
        return tableInfo.getId();
    }
}
