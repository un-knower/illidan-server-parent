package cn.whaley.datawarehouse.illidan.common.domain.task;

import cn.whaley.datawarehouse.illidan.common.domain.BaseQueryEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by wujiulin on 2017/6/26.
 */
public class TaskQuery extends BaseQueryEntity {
    private static final long serialVersionUID = 1L;
    private Long id;
    /**
     * 任务code
     */
    private String taskCode;
    /**
     * 任务描述
     */
    private String taskDes;
    /**
     * 任务添加用户
     */
    private String addUser;

    /**
     * 任务所属group的id
     */
    private Long groupId;

    /**
     * 任务所属group的code
     */
    private String groupCode;

    /**
     * 任务所属项目的id
     */
    private Long projectId;

    private Long hiveTableId;
    /**
     * 目标表名
     */
    private String hiveTableCode;

    /**
     * 目标表描述
     */
    private String hiveTableDesc;

    private String sourceTableCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskDes() {
        return taskDes;
    }

    public void setTaskDes(String taskDes) {
        this.taskDes = taskDes;
    }

    public String getAddUser() {
        return addUser;
    }

    public void setAddUser(String addUser) {
        this.addUser = addUser;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getHiveTableId() {
        return hiveTableId;
    }

    public void setHiveTableId(Long hiveTableId) {
        this.hiveTableId = hiveTableId;
    }

    public String getHiveTableCode() {
        return hiveTableCode;
    }

    public void setHiveTableCode(String hiveTableCode) {
        this.hiveTableCode = hiveTableCode;
    }

    public String getHiveTableDesc() {
        return hiveTableDesc;
    }

    public void setHiveTableDesc(String hiveTableDesc) {
        this.hiveTableDesc = hiveTableDesc;
    }

    public String getSourceTableCode() {
        return sourceTableCode;
    }

    public void setSourceTableCode(String sourceTableCode) {
        this.sourceTableCode = sourceTableCode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
