package cn.whaley.datawarehouse.illidan.common.service;

import cn.whaley.datawarehouse.illidan.common.domain.UserInfo;

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
