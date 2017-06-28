package cn.whaley.datawarehouse.illidan.common.service.db;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import cn.whaley.datawarehouse.illidan.common.mapper.db.DbInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Service
public class DbInfoServiceImpl implements DbInfoService {
    @Autowired
    private DbInfoMapper dbInfoMapper;

    public DbInfo get(final Long id) {
        return dbInfoMapper.get(id);
    }
}
