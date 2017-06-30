package cn.whaley.datawarehouse.illidan.common.service.table;

import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;
import cn.whaley.datawarehouse.illidan.common.domain.table.TableWithField;

/**
 * Created by wujiulin on 2017/6/28.
 */
public interface TableInfoService {

    TableInfo get(final Long id);

    TableWithField getTableWithField(final Long id);

    Long insert(final TableInfo tableInfo) throws Exception;
}
