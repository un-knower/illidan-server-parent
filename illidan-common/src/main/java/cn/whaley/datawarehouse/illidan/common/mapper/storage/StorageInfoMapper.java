package cn.whaley.datawarehouse.illidan.common.mapper.storage;

import cn.whaley.datawarehouse.illidan.common.domain.storage.StorageInfo;
import cn.whaley.datawarehouse.illidan.common.domain.storage.StorageInfoQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Mapper
@Component
public interface StorageInfoMapper {
    StorageInfo get(@Param("id") final Long id);
    List<StorageInfo> findByStorageInfo(@Param("storageInfo") final StorageInfoQuery storageInfo);
}
