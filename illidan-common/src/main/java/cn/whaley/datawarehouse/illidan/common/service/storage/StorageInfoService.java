package cn.whaley.datawarehouse.illidan.common.service.storage;

import cn.whaley.datawarehouse.illidan.common.domain.storage.StorageInfo;
import cn.whaley.datawarehouse.illidan.common.domain.storage.StorageInfoQuery;

import java.util.List;

/**
 * Created by wujiulin on 2017/7/12.
 */
public interface StorageInfoService {
    StorageInfo get(final Long id);
    List<StorageInfo> findByStorageInfo(final StorageInfoQuery storageInfo);
}
