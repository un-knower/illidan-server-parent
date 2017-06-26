package cn.whaley.datawarehouse.illidan.common.service.user;

import cn.whaley.datawarehouse.illidan.common.domain.user.UserInfo;

import java.util.List;

public interface UserService {

	/**
	 * findAll
	 * @return
	 */
	List<UserInfo> findAll();
	
	UserInfo findOne(int id);

	UserInfo findByName(String name);

	void addUser(UserInfo userInfo);
}
