package cn.whaley.datawarehouse.illidan.common.mapper.db;

import cn.whaley.datawarehouse.illidan.common.domain.db.DbInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Mapper
@Component
public interface DbInfoMapper {
    DbInfo get(@Param("id") final Long id);
}
