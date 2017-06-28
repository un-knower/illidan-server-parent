package cn.whaley.datawarehouse.illidan.common.service.table;

import cn.whaley.datawarehouse.illidan.common.domain.table.TableInfo;

/**
 * Created by wujiulin on 2017/6/28.
 */
public interface TableInfoService {
    TableInfo get(final Long id);
}
