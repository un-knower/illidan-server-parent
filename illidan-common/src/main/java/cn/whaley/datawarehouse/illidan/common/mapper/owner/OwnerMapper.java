package cn.whaley.datawarehouse.illidan.common.mapper.owner;

import cn.whaley.datawarehouse.illidan.common.domain.owner.Owner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Mapper
@Component
public interface OwnerMapper {
    Owner get(@Param("id") final Long id);
}
