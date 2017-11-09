package cn.whaley.datawarehouse.illidan.common.service.db;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoQuery;
import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfoWithStorage;
import cn.whaley.datawarehouse.illidan.common.domain.storage.StorageInfo;
import cn.whaley.datawarehouse.illidan.common.domain.storage.StorageInfoQuery;
import cn.whaley.datawarehouse.illidan.common.mapper.db.DbInfoMapper;
import cn.whaley.datawarehouse.illidan.common.mapper.storage.StorageInfoMapper;
import cn.whaley.datawarehouse.illidan.common.service.storage.StorageInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Service
public class DbInfoServiceImpl implements DbInfoService {
    private Logger logger = LoggerFactory.getLogger(DbInfoServiceImpl.class);
    @Autowired
    private DbInfoMapper dbInfoMapper;
    @Autowired
    private StorageInfoMapper storageInfoMapper;
    @Autowired
    private StorageInfoService storageInfoService;

    public DbInfo get(final Long id) {
        if (id == null){
            logger.error("get: dbId is null.");
            return null;
        }
        return dbInfoMapper.get(id);
    }

    @Override
    public DbInfoWithStorage getDbWithStorage(Long id) {
        if (id == null) {
            logger.error("getDbWithStorage: dbId is null.");
            return null;
        }
        //目标数据库实体
        DbInfo dbInfo = dbInfoMapper.get(id);
        if (dbInfo == null){
            logger.error("getDbWithStorage: dbInfo is null. dbId: " + id);
            return null;
        }
        //storage实体
        StorageInfo storageInfo = storageInfoMapper.get(dbInfo.getStorageId());
        if (storageInfo == null){
            logger.error("getDbWithStorage: storageInfo is null. dbId: "+id+" ,storageId: "+dbInfo.getStorageId());
            return null;
        }
        DbInfoWithStorage dbInfoWithStorage = new DbInfoWithStorage();
        BeanUtils.copyProperties(dbInfo, dbInfoWithStorage);
        BeanUtils.copyProperties(storageInfo, dbInfoWithStorage);
        return dbInfoWithStorage;
    }

    @Override
    public DbInfoWithStorage getDbWithStorageByCode(String dbCode) {
        if (dbCode == null || dbCode.equals("")) {
            logger.error("getDbWithStorageByCode: dbCode is null.");
            return null;
        }
        DbInfoQuery dbInfoQuery = new DbInfoQuery();
        dbInfoQuery.setDbCode(dbCode.trim());
        //目标数据库实体
        DbInfo dbInfo = findOne(dbInfoQuery);
        if (dbInfo == null){
            logger.error("getDbWithStorageByCode: dbInfo is null. dbCode: '" + dbCode +"'");
            return null;
        }
        //storage实体
        StorageInfo storageInfo = storageInfoMapper.get(dbInfo.getStorageId());
        if (storageInfo == null){
            logger.error("getDbWithStorageByCode: storageInfo is null. dbCode: "+ dbCode + ", storageId: "+ dbInfo.getStorageId());
            return null;
        }
        DbInfoWithStorage dbInfoWithStorage = new DbInfoWithStorage();
        BeanUtils.copyProperties(dbInfo, dbInfoWithStorage);
        BeanUtils.copyProperties(storageInfo, dbInfoWithStorage);
        return dbInfoWithStorage;
    }

    public List<DbInfo> findByDbInfo(final DbInfoQuery dbInfo){
        if (dbInfo == null){
            logger.error("findByDbInfo: dbInfo is null.");
            return null;
        }
        return dbInfoMapper.findByDbInfo(dbInfo);
    }

    @Override
    public List<DbInfo> getDbInfo(Long storageType) {
        StorageInfoQuery storageInfo = new StorageInfoQuery();
        storageInfo.setStorageType(storageType);
        List<StorageInfo> storageInfos = storageInfoService.findByStorageInfo(storageInfo);

        List<DbInfo> dbInfoList = new ArrayList<DbInfo>();
        DbInfoQuery dbInfo = new DbInfoQuery();
        for (StorageInfo s : storageInfos){
            dbInfo.setStorageId(s.getId());
            List<DbInfo> dbInfos = findByDbInfo(dbInfo);
            dbInfoList.addAll(dbInfos);
        }
        return dbInfoList;
    }

    public DbInfo findOne(final DbInfoQuery dbInfo) {
        if (dbInfo == null){
            logger.error("findOne :dbInfo is null.");
            return null;
        }
        dbInfo.setLimitStart(0);
        dbInfo.setLimitEnd(1);
        List<DbInfo> datas = dbInfoMapper.findByDbInfo(dbInfo);
        if (datas != null && datas.size() > 0) {
            return datas.get(0);
        }
        logger.error("findOne: data list is null. dbInfo: "+dbInfo.toString());
        return null;
    }
}
