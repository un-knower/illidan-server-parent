package cn.whaley.datawarehouse.illidan.common.domain.table;

import cn.whaley.datawarehouse.illidan.common.domain.BaseQueryEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;

/**
 * Created by wujiulin on 2017/6/27.
 */
public class TableInfoQuery extends BaseQueryEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    /**
     * 表英文名称
     */
    private String tableCode;
    /**
     * 表描述
     */
    private String tableDes;
    /**
     * 储存数据类型（如：parquet,textfile等）
     */
//    private String dataType;
    /**
     * 所属数据库
     */
    private List<Long> dbIdList;
    /**
     * 创建时间
     */
//    private Date createTime;
    /**
     * 修改时间
     */
//    private Date updateTime;
//    private String createTimeBegin;
//    private String createTimeEnd;
    /**
     * 状态（1：有效，0无效）
     */
//    private String status;
    /**
     * 对应的mysql输出表
     */
//    private Long mysqlTableId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public String getTableDes() {
        return tableDes;
    }

    public void setTableDes(String tableDes) {
        this.tableDes = tableDes;
    }

    public List<Long> getDbIdList() {
        return dbIdList;
    }

    public void setDbIdList(List<Long> dbIdList) {
        this.dbIdList = dbIdList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
