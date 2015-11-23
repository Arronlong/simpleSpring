package com.tgb.ccl.simplespring.util;

import java.util.Map;

public class PagingUtils{
	
	/**
	 * 是否分页查询标记
	 */
	public final static String PAGE_SEARCH="isPageSearch";
	/**
	 * 分页页码标记
	 */
	public final static String PAGE_NUM="pageNum";
	/**
	 * 分页容量标记
	 */
	public final static String PAGE_SIZE="pageSize";

	public static boolean isPagingSearchRequest( Map<String,Object> paramMap) {
		if(paramMap.containsKey(PagingUtils.PAGE_SEARCH)){
			if(Boolean.parseBoolean(paramMap.get(PAGE_SEARCH).toString())){
				return true;
			}
		}
		return false;
	}
}
