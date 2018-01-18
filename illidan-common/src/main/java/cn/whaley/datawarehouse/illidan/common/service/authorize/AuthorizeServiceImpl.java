package cn.whaley.datawarehouse.illidan.common.service.authorize;

import cn.whaley.datawarehouse.illidan.common.domain.authorize.Authorize;
import cn.whaley.datawarehouse.illidan.common.mapper.authorize.AuthorizeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {
    private Logger logger = LoggerFactory.getLogger(AuthorizeServiceImpl.class);
    @Autowired
    private AuthorizeMapper authorizeMapper;

    @Override
    public Authorize get(Long id) {
        if (id == null){
            logger.error("get: id is null.");
            return null;
        }
        return authorizeMapper.get(id);
    }

    @Override
    public Long insert(Authorize authorize) {
        if (authorize == null){
            logger.error("insert: authorize is null.");
            return null;
        }
        authorizeMapper.insert(authorize) ;
        return authorize.getId();
    }

    @Override
    public List<Authorize> findByAuthorize(Authorize authorize) {
        if (authorize == null){
            logger.error("findByAuthorize: authorize is null.");
            return null;
        }
        return authorizeMapper.findByAuthorize(authorize);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null){
            logger.error("deleteById: id is null.");
            return;
        }
        authorizeMapper.deleteById(id);
    }
}
