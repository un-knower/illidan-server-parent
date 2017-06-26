package cn.whaley.datawarehouse.illidan.common.service.user;

import cn.whaley.datawarehouse.illidan.common.domain.user.UserInfo;
import cn.whaley.datawarehouse.illidan.common.mapper.user.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.management.RuntimeErrorException;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    public List<UserInfo> findAll() {
        logger.info("find All ====================");
        return userMapper.findAll();
    }

    public UserInfo findOne(int id) {
        return userMapper.findOne(id);
    }

    public void addUser(UserInfo userInfo) {
        try {
            userMapper.addUser(userInfo);
        } catch (RuntimeErrorException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    public UserInfo findByName(String name) {
        return userMapper.findByName(name);
    }
}
