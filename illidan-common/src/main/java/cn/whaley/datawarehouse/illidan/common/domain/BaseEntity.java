/**  
 * @Title: BaseEntity.java
 * @Package com.core.entity
 * @date 2015�?10�?10�? 下午2:14:46
 * @version V1.0  
 */
package cn.whaley.datawarehouse.illidan.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;

/**
 * ����Ĺ�����; ��Ŀ����: [yfx]<br/>
 * ������: [BaseEntity]<br/>
 * ������: [GaoNan]<br/>
 * ����ʱ��: [2015��10��10�� ����2:14:46]<br/>
 * �޸���: [GaoNan]<br/>
 * �޸�ʱ��: [2015��10��10�� ����2:14:46]<br/>
 * �޸ı�ע: [˵�������޸�����]<br/>
 * �汾: [v1.0]<br/>
 */
public abstract class BaseEntity<PK, T> extends ObjectSupport implements Serializable {

    private static final long serialVersionUID = 1L;

    protected PK id;
    @JsonIgnore
    protected boolean sqldecode=true;
    @JsonIgnore
    protected Integer pageIndex;
    @JsonIgnore
    protected Integer pageSize = 10;
    @JsonIgnore
    protected Integer limitStart;
    @JsonIgnore
    protected Integer limitEnd;
    @JsonIgnore
    protected String orderBy;
    @JsonIgnore
    protected Page<T> page;

    private List<Long> authIds;

    public List<Long> getAuthIds() {
        return authIds;
    }

    public void setAuthIds(List<Long> authIds) {
        this.authIds = authIds;
    }

    public Page<T> getPage() {
        return page;
    }

    public void setPage(Page<T> page) {
        this.page = page;
    }
    
    public boolean isSqldecode() {
		return sqldecode;
	}

	public void setSqldecode(boolean sqldecode) {
		this.sqldecode = sqldecode;
	}

	public Integer getLimitStart() {
        if(limitStart != null){
            return limitStart;
        }
        if (pageIndex != null && pageIndex >=1 && pageSize != null && pageSize > 0) {
            limitStart = (pageIndex-1) * pageSize;
        }
        return limitStart;
    }

    public void setLimitStart(Integer limitStart) {
        this.limitStart = limitStart;
    }

    public Integer getLimitEnd() {
        if(pageSize != null && pageSize > 0){
            limitEnd = pageSize;
        }
//        if (pageIndex != null && pageIndex >= 0 && pageSize != null && pageSize > 0) {
//            limitEnd = pageSize;
//        }
        return limitEnd;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setLimitEnd(Integer limitEnd) {
        this.limitEnd = limitEnd;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public PK getId() {
        return id;
    }

    public void setId(PK id) {
        this.id = id;
    }





}
