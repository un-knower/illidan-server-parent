package cn.whaley.datawarehouse.illidan.common.service.storage;

import cn.whaley.datawarehouse.illidan.common.domain.storage.StorageInfo;
import cn.whaley.datawarehouse.illidan.common.domain.storage.StorageInfoQuery;
import cn.whaley.datawarehouse.illidan.common.mapper.storage.StorageInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wujiulin on 2017/7/12.
 */
@Service
public class StorageInfoServiceImpl implements StorageInfoService {
    @Autowired
    private StorageInfoMapper storageInfoMapper;

    public StorageInfo get(final Long id){
        return storageInfoMapper.get(id);
    }

    public List<StorageInfo> findByStorageInfo(final StorageInfoQuery storageInfo){
        return storageInfoMapper.findByStorageInfo(storageInfo);
    }
}
