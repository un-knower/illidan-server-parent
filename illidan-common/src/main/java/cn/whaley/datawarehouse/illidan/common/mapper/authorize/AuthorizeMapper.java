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

    Authorize getByParentId(@Param("parentId") final Long parentId, @Param("type") final int type);

    Long insert(@Param("authorize") final Authorize authorize);

    List<Authorize> findByAuthorize(@Param("authorize") final Authorize Authorize);

    Authorize getByAuthorize(@Param("authorize") final Authorize Authorize);

    void deleteById(@Param("id") final Long id);

}
