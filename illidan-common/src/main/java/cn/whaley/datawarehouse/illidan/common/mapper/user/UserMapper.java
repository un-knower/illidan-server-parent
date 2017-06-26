package cn.whaley.datawarehouse.illidan.common.mapper.user;

import cn.whaley.datawarehouse.illidan.common.domain.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface UserMapper {

	UserInfo findOne(int id);

	UserInfo findByName(String name);

	List<UserInfo> findAll();

	void addUser(UserInfo userInfo);
}
