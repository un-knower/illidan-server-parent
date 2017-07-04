package cn.whaley.datawarehouse.illidan.common.service.table;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.field.FieldInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;
import cn.whaley.datawarehouse.illidan.common.mapper.table.TableInfoMapper;
import cn.whaley.datawarehouse.illidan.common.service.db.DbInfoService;
import cn.whaley.datawarehouse.illidan.common.service.field.FieldInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Service
public class TableInfoServiceImpl implements TableInfoService {
    @Autowired
    private TableInfoMapper tableInfoMapper;

    @Autowired
    private DbInfoService dbInfoService;

    @Autowired
    private FieldInfoService fieldInfoService;

    public TableInfo get(final Long id) {
        return tableInfoMapper.get(id);
    }

    @Override
    public Long insert(TableInfo tableInfo) throws Exception {
        Long count = tableInfoMapper.isExistTableInfo(tableInfo.getTableCode());
        if (count > 0) {
            throw new Exception("表已经存在不能重复新增");
        }
        tableInfoMapper.insert(tableInfo);
        return tableInfo.getId();
    }

    public TableWithField getTableWithField(final Long id) {

        //目标表实体
        TableInfo tableInfo = tableInfoMapper.get(id);
        if (id == null) {
            return null;
        }
        //目标数据库实体
        DbInfo dbInfo = dbInfoService.get(tableInfo.getDbId());
        //列名实体
        List<FieldInfo> fieldInfoList = fieldInfoService.getByTableId(tableInfo.getId());
        TableWithField tableWithField = new TableWithField();
        BeanUtils.copyProperties(tableInfo, tableWithField);
        tableWithField.setDbInfo(dbInfo);
        tableWithField.setFieldList(fieldInfoList);

        return tableWithField;
    }

    public List<TableInfo> findByTableInfo(final TableInfoQuery tableInfo){
        return tableInfoMapper.findByTableInfo(tableInfo);
    }

    public TableInfo findOne(final TableInfoQuery tableInfo) {
        tableInfo.setLimitStart(0);
        tableInfo.setLimitEnd(1);
        List<TableInfo> datas = tableInfoMapper.findByTableInfo(tableInfo);
        if (datas != null && datas.size() > 0) {
            return datas.get(0);
        }
        return null;
    }

    public Long updateById(final TableInfo tableInfo){
        return tableInfoMapper.updateById(tableInfo);
    }
}
