package cn.whaley.datawarehouse.illidan.common.domain.authorize;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class Authorize implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    /**
     * project_id or table_id
     */
    private Long parentId;
    /**
     * 权限节点(project节点或者table节点)
     */
    private String nodeId;
    /**
     * 读权限节点
     */
    private String readId;
    /**
     * 写权限节点
     */
    private String writeId;
    /**
     * 发布权限节点
     */
    private String publishId;
    /**
     * 1:project,2:table
     */
    private Integer type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getReadId() {
        return readId;
    }

    public void setReadId(String readId) {
        this.readId = readId;
    }

    public String getWriteId() {
        return writeId;
    }

    public void setWriteId(String writeId) {
        this.writeId = writeId;
    }

    public String getPublishId() {
        return publishId;
    }

    public void setPublishId(String publishId) {
        this.publishId = publishId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
