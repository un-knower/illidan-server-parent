package cn.whaley.datawarehouse.illidan.common.service.authorize;

import cn.whaley.datawarehouse.illidan.common.domain.authorize.Authorize;
import cn.whaley.datawarehouse.illidan.common.enums.AuthorityTypeEnum;

import java.util.List;

public interface AuthorizeService {
    Authorize get(final Long id);

    Authorize getByParentId(final Long parentId, final AuthorityTypeEnum authorityTypeEnum);

    Long insert(final Authorize authorize);

    List<Authorize> findByAuthorize(final Authorize authorize);

    Authorize getByAuthorize(final Authorize authorize);

    void deleteById(Long id);

}
