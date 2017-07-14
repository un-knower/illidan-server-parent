package cn.whaley.datawarehouse.illidan.common.service.db;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;

import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */
public interface DbInfoService {
    DbInfo get(final Long id);

    DbInfoWithStorage getDbWithStorage(final Long id);

    DbInfoWithStorage getDbWithStorageByCode(final String dbCode);

    List<DbInfo> findByDbInfo(final DbInfoQuery dbInfo);
}
