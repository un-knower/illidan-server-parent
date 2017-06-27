package cn.whaley.datawarehouse.illidan.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;

public abstract class BaseQueryEntity extends ObjectSupport implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    protected boolean sqldecode = true;
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

    private List<Long> authIds;

    public List<Long> getAuthIds() {
        return authIds;
    }

    public void setAuthIds(List<Long> authIds) {
        this.authIds = authIds;
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
}
