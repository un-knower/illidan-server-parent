package cn.whaley.datawarehouse.illidan.common.domain.storage;

import cn.whaley.datawarehouse.illidan.common.domain.BaseQueryEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Created by wujiulin on 2017/7/12.
 */
public class StorageInfoQuery extends BaseQueryEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    /**
     * 1.hive,2.mysql
     * */
    private Long storageType;
    private String address;
    private String user;
    private String password;
    private String driver;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStorageType() {
        return storageType;
    }

    public void setStorageType(Long storageType) {
        this.storageType = storageType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
