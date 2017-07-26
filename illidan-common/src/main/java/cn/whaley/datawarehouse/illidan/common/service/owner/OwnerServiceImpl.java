package cn.whaley.datawarehouse.illidan.common.service.owner;

import cn.whaley.datawarehouse.illidan.common.domain.owner.Owner;
import cn.whaley.datawarehouse.illidan.common.domain.owner.OwnerQuery;
import cn.whaley.datawarehouse.illidan.common.mapper.owner.OwnerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wujiulin on 2017/6/28.
 */

@Service
public class OwnerServiceImpl implements OwnerService {
    private Logger logger = LoggerFactory.getLogger(OwnerServiceImpl.class);
    @Autowired
    private OwnerMapper ownerMapper;

    public Owner get(final Long id) {
        if (id == null){
            logger.error("get: id is null.");
            return null;
        }
        return ownerMapper.get(id);
    }

    public List<Owner> findByOwner(final OwnerQuery owner) {
        if (owner == null){
            logger.error("findByOwner: owner is null.");
            return null;
        }
        return ownerMapper.findByOwner(owner);
    }
}
