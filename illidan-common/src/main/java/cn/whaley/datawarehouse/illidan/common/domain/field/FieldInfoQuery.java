package cn.whaley.datawarehouse.illidan.common.domain.field;

import cn.whaley.datawarehouse.illidan.common.domain.BaseQueryEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * Created by wujiulin on 2017/6/27.
 */
public class FieldInfoQuery extends BaseQueryEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    /**
     * 字段名（英文）
     */
    private String colName;
    /**
     * 字段类型（如 string,int等）
     */
    private String colType;
    /**
     * 字段描述
     */
    private String colDes;
    /**
     * 字段位置（从1开始）
     */
    private int colIndex;
    /**
     * 是否为分区字段（1：分区字段，0：不是）
     */
    private String isPartitionCol;
    /**
     * 字段所属表
     */
    private Long tableId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    private String createTimeBegin;
    private String createTimeEnd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColType() {
        return colType;
    }

    public void setColType(String colType) {
        this.colType = colType;
    }

    public String getColDes() {
        return colDes;
    }

    public void setColDes(String colDes) {
        this.colDes = colDes;
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    public String getIsPartitionCol() {
        return isPartitionCol;
    }

    public void setIsPartitionCol(String isPartitionCol) {
        this.isPartitionCol = isPartitionCol;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
