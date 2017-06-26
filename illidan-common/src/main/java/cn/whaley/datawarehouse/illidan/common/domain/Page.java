package cn.whaley.datawarehouse.illidan.common.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * �����ORMʵ���޹صķ�ҳ��������ѯ�����װ.
 * ע��������Ŵ�1��ʼ.
 *
 * @param <T>
 *            Page�м�¼������.
 */
public class Page<T> {

	protected int pageStart = 1;
	protected int pageSize = 0;
	protected List<T> data =new ArrayList<T>();
	protected long totalCount = 0;

	public Page() {
	}

	public Page(int pageSize) {
		this.pageSize = pageSize;
	}

	public Page(int pageStartPos, int pageSize) {
		this.pageStart = pageStartPos;
		this.pageSize = pageSize;
	}

	public int getPageStart() {
		return pageStart;
	}

	public void setPageStart(final int pageStartPos) {
		this.pageStart = pageStartPos;
		if (pageStartPos < 1) {
			this.pageStart = 1;
		}
	}

	public Page<T> pageStartPos(final int pageStartPos) {
		setPageStart(pageStartPos);
		return this;
	}

	public int getPageSize() {
		return pageSize;
	}

	/**
	 * ����ÿҳ�ļ�¼����.
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * ����Page���������setPageSize����,�������������á�
	 */
	public Page<T> pageSize(final int thePageSize) {
		setPageSize(thePageSize);
		return this;
	}

	/**
	 * ���ҳ�ڵļ�¼�б�.
	 */
	public List<T> getData() {
		return data;
	}

	/**
	 * ����ҳ�ڵļ�¼�б�.
	 */
	public void setData(final List<T> result) {
		this.data = result;
	}

	/**
	 * ����ܼ�¼��, Ĭ��ֵΪ-1.
	 */
	public long getTotalCount() {
		return totalCount;
	}

	/**
	 * �����ܼ�¼��.
	 */
	public void setTotalCount(final long totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * ����pageSize��totalCount������ҳ��, Ĭ��ֵΪ-1.
	 */
	public long getTotalPages() {
		if (totalCount < 0) {
			return -1;
		}
		long count = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			count++;
		}
		return count;
	}

	/**
	 * �Ƿ�����һҳ.
	 *
	 * @return boolean
	 */
	public boolean isHasNext() {
		return (pageStart + 1 <= getTotalPages());
	}

	/**
	 * ȡ����ҳ��ҳ��, ��Ŵ�1��ʼ. ��ǰҳΪβҳʱ�Է���βҳ���.
	 */
	public int getNextPage() {
		if (isHasNext()) {
			return pageStart + 1;
		} else {
			return pageStart;
		}
	}

	/**
	 * �Ƿ�����һҳ.
	 */
	public boolean isHasPre() {
		return (pageStart - 1 >= 1);
	}

	/**
	 * ȡ����ҳ��ҳ��, ��Ŵ�1��ʼ. ��ǰҳΪ��ҳʱ������ҳ���.
	 */
	public int getPrePage() {
		if (isHasPre()) {
			return pageStart - 1;
		} else {
			return pageStart;
		}
	}

}
