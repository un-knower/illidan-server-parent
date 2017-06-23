package cn.whaley.datawarehouse.illidan.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class UserInfo implements Serializable {
	private static final long serialVersionUID = 6519997700281088880L;
	
	private int id;
	
	private String name;
	
	private String tel;
	
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	private String createTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}