package com.tgb.ccl.simplespring.basic.enums;

/**
 * 数据库对象：表、视图
 * 
 * @author arron
 * @date 2015年3月12日 下午2:56:57 
 * @version 1.0
 */
public enum DBObjectEnum {
	
	TABLE("Table"),
	VIEW("View");
	
	private String name;
	
	private DBObjectEnum(String name){
		this.name=name;
	}

	public String getName() {
		return name;
	}
}
