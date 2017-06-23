package cn.whaley.datawarehouse.illidan.common.mapper;

import cn.whaley.datawarehouse.illidan.common.domain.UserInfo;
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
