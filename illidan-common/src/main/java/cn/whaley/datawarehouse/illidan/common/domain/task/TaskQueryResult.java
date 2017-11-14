package cn.whaley.datawarehouse.illidan.common.domain.task;

/**
 * Created by lituo on 2017/11/14.
 */
public class TaskQueryResult extends Task {

    private String groupCode;

    private Boolean isExport2Mysql;

    private Long mysqlTableId;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Boolean getIsExport2Mysql() {
        return isExport2Mysql;
    }

    public void setIsExport2Mysql(Boolean export2Mysql) {
        isExport2Mysql = export2Mysql;
    }

    public Long getMysqlTableId() {
        return mysqlTableId;
    }

    public void setMysqlTableId(Long mysqlTableId) {
        this.mysqlTableId = mysqlTableId;
    }
}
