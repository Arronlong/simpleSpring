package com.tgb.ccl.simplespring.basic.map;

import com.tgb.ccl.simplespring.util.PagingUtils;

/**
 * 分页查询专用map
 * 
 * @author arron
 * @date 2015年3月2日 下午6:21:44 
 * @version 1.0
 * @param <K>
 * @param <V>
 */
public class PageQueryMap<K, V> extends ConditionLinkMap<K, V> {
	
	private static final long serialVersionUID = -8733891143438136948L;

	@SuppressWarnings("unchecked")
	public PageQueryMap(){
		this.put((K)PagingUtils.PAGE_SEARCH, (V)"true");
	}

}
