package com.tgb.ccl.simplespring.support;

import java.io.Serializable;
import java.util.List;

/**
 * 分页模型
 * @author     : 崔成龙
 * @group      : tgb8
 * @Date       : 2014-10-19 下午4:42:08
 * @Version    : 1.0.0
 * @param <E>
 */
public class PageModel<E> implements Serializable {

	/**
	 * @Fields serialVersionUID	:TODO
	 */
	private static final long serialVersionUID = 1L;
	public final static String ORDER_DIRECTION_ASC = "ASC"; 
	public final static String ORDER_DIRECTION_DESC = "DESC";
	
	/**
	 * 默认每页记录数
	 */
	public static final int DEFAULT_PAGE_SIZE = 8;
	
	/**
	 * 結果集
	 */
	private List<E> list;
	
	/**
	 * 查询记录数
	 */
	private Long totalRecords;
	
	
	/**
	 * 当前页码
	 */
	protected int pageNum = 1;
	
	/**
	 * 每页显示多少条
	 */
	protected int numPerPage = DEFAULT_PAGE_SIZE;
	
	// 默认按照id倒序排列
	private String orderField = "";
	private String orderDirection = "";
	
	
	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}

	public Long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getNumPerPage() {
		return numPerPage;
	}

	public void setNumPerPage(int numPerPage) {
		this.numPerPage = numPerPage;
	}

	public String getOrderField() {
		return orderField;
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	public String getOrderDirection() {
		return orderDirection;
	}

	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}
	
	@Override
	public String toString() {
		return pageNum + numPerPage +orderField+orderDirection;
	}
}

