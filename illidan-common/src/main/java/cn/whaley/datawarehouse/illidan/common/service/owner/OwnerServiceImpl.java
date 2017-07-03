package cn.whaley.datawarehouse.illidan.common.service.owner;

import cn.whaley.datawarehouse.illidan.common.domain.owner.Owner;
import cn.whaley.datawarehouse.illidan.common.domain.owner.OwnerQuery;
import cn.whaley.datawarehouse.illidan.common.mapper.owner.OwnerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Service
public class OwnerServiceImpl implements OwnerService {
    @Autowired
    private OwnerMapper ownerMapper;

    public Owner get(final Long id) {
        return ownerMapper.get(id);
    }

    public List<Owner> findByOwner(final OwnerQuery owner) {
        return ownerMapper.findByOwner(owner);
    }
}