package cn.whaley.datawarehouse.illidan.common.service.db;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.domain.storage.StorageInfo;
import cn.whaley.datawarehouse.illidan.common.mapper.db.DbInfoMapper;
import cn.whaley.datawarehouse.illidan.common.mapper.storage.StorageInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Service
public class DbInfoServiceImpl implements DbInfoService {
    @Autowired
    private DbInfoMapper dbInfoMapper;
    @Autowired
    private StorageInfoMapper storageInfoMapper;

    public DbInfo get(final Long id) {
        return dbInfoMapper.get(id);
    }

    @Override
    public DbInfoWithStorage getDbWithStorage(Long id) {
        if (id == null) {
            return null;
        }
        //目标数据库实体
        DbInfo dbInfo = dbInfoMapper.get(id);
        //storage实体
        StorageInfo storageInfo = storageInfoMapper.get(dbInfo.getStorageId());
        DbInfoWithStorage dbInfoWithStorage = new DbInfoWithStorage();
        BeanUtils.copyProperties(dbInfo, dbInfoWithStorage);
        BeanUtils.copyProperties(storageInfo, dbInfoWithStorage);
        return dbInfoWithStorage;
    }

    @Override
    public DbInfoWithStorage getDbWithStorageByCode(String dbCode) {
        if (dbCode == null || dbCode.equals("")) {
            return null;
        }
        DbInfoQuery dbInfoQuery = new DbInfoQuery();
        dbInfoQuery.setDbCode(dbCode);
        //目标数据库实体
        DbInfo dbInfo = findOne(dbInfoQuery);
        //storage实体
        StorageInfo storageInfo = storageInfoMapper.get(dbInfo.getStorageId());
        DbInfoWithStorage dbInfoWithStorage = new DbInfoWithStorage();
        BeanUtils.copyProperties(dbInfo, dbInfoWithStorage);
        BeanUtils.copyProperties(storageInfo, dbInfoWithStorage);
        return dbInfoWithStorage;
    }

    public List<DbInfo> findByDbInfo(final DbInfoQuery dbInfo){
        return dbInfoMapper.findByDbInfo(dbInfo);
    }

    public DbInfo findOne(final DbInfoQuery dbInfo) {
        dbInfo.setLimitStart(0);
        dbInfo.setLimitEnd(1);
        List<DbInfo> datas = dbInfoMapper.findByDbInfo(dbInfo);
        if (datas != null && datas.size() > 0) {
            return datas.get(0);
        }
        return null;
    }
}
