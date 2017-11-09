package cn.whaley.datawarehouse.illidan.common.service.table;

import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;

import java.util.HashMap;
import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */
public interface TableInfoService {

    TableInfo get(final Long id);

    TableWithField getTableWithField(final Long id);

    Long insert(final TableInfo tableInfo) throws Exception;

    List<TableInfo> findByTableInfo(final TableInfoQuery tableInfo);

    TableInfo findOne(final TableInfoQuery tableInfo);

    Long updateById(final TableInfo tableInfo);

    Long removeByIds(final List<Long> ids);

    Long countByTableInfo(final TableInfoQuery tableInfo);

    HashMap<Long,Long> insertTableWithField(final List<TableWithField> tableList) throws Exception;
}
