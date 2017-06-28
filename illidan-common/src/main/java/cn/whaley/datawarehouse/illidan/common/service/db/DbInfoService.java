package cn.whaley.datawarehouse.illidan.common.service.db;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;

/**
 * Created by wujiulin on 2017/6/28.
 */
public interface DbInfoService {
    DbInfo get(final Long id);
}
