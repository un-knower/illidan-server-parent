package cn.whaley.datawarehouse.illidan.common.mapper.authorize;

import cn.whaley.datawarehouse.illidan.common.domain.authorize.Authorize;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface AuthorizeMapper {
    Authorize get(@Param("id") final Long id);

    Long insert(@Param("authorize") final Authorize authorize);

    List<Authorize> findByAuthorize(@Param("authorize") final Authorize Authorize);

    void deleteById(@Param("id") final Long id);

}
