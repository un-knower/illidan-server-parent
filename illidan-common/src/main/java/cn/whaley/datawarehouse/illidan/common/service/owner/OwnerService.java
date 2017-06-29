package cn.whaley.datawarehouse.illidan.common.service.owner;

import cn.whaley.datawarehouse.illidan.common.domain.owner.Owner;
import cn.whaley.datawarehouse.illidan.common.domain.owner.OwnerQuery;

import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */
public interface OwnerService {
    Owner get(final Long id);
    List<Owner> findByOwner(final OwnerQuery owner);
}
