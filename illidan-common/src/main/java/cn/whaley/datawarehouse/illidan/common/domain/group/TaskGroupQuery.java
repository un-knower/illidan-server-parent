package cn.whaley.datawarehouse.illidan.common.domain.group;

import cn.whaley.datawarehouse.illidan.common.domain.BaseQueryEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * Created by wujiulin on 2017/6/26.
 */
public class TaskGroupQuery extends BaseQueryEntity {

    private static final long serialVersionUID = 1L;
    private Long id;
    /**
     * 任务组code
     */
    private String groupCode;
    /**
     * 任务住描述
     */
    private String groupDes;
    /**
     * 调度策略（0：不需要调度，crontab）
     */
    private String schedule;
    /**
     * 任务失败邮件发送人（xx@whaley.cn,yy@whaley.cn）
     */
    private String email;
    /**
     * 状态（1：有效，0无效）
     */
    private String status;
    /**
     * 工程主键
     */
    private Long projectId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 调度的id号，设置调度成功后，生成
     */
    private String scheduleId;
    private String createTimeBegin;
    private String createTimeEnd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupDes() {
        return groupDes;
    }

    public void setGroupDes(String groupDes) {
        this.groupDes = groupDes;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
