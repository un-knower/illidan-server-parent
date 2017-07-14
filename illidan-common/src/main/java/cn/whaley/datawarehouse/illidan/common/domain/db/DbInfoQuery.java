package cn.whaley.datawarehouse.illidan.common.domain.db;

import cn.whaley.datawarehouse.illidan.common.domain.BaseQueryEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * Created by wujiulin on 2017/6/27.
 */
public class DbInfoQuery extends BaseQueryEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    /**
     * 数据库名（英文）
     **/
    private String dbCode;
    /**
     * 数据库描述
     **/
    private String dbDes;
    /**
     * 状态（1：有效，0无效）
     **/
    private String status;
    /**
     * 创建时间
     **/
    private Date createTime;
    /**
     * 修改时间
     **/
    private Date updateTime;
    /**
     * 存储信息
     **/
    private Long storageId;
    private String createTimeBegin;
    private String createTimeEnd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDbCode() {
        return dbCode;
    }

    public void setDbCode(String dbCode) {
        this.dbCode = dbCode;
    }

    public String getDbDes() {
        return dbDes;
    }

    public void setDbDes(String dbDes) {
        this.dbDes = dbDes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTimeBegin() {
        return createTimeBegin;
    }

    public void setCreateTimeBegin(String createTimeBegin) {
        this.createTimeBegin = createTimeBegin;
    }

    public String getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public Long getStorageId() {
        return storageId;
    }

    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
