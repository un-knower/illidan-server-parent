package cn.whaley.datawarehouse.illidan.common.service.authorize;

import cn.whaley.datawarehouse.illidan.common.domain.authorize.Authorize;

import java.util.List;

public interface AuthorizeService {
    Authorize get(final Long id);

    Long insert(final Authorize authorize);

    List<Authorize> findByAuthorize(final Authorize authorize);

    void deleteById(Long id);

}
