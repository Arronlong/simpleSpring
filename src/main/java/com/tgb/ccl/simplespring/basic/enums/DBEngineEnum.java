package com.tgb.ccl.simplespring.basic.enums;

/**
 * mysql存储引擎
 * 
 * @author arron
 * @date 2015年3月1日 下午11:03:38 
 * @version 1.0
 */
public enum DBEngineEnum {
	
	//MyISAM适合：(1)做很多count 的计算；(2)插入不频繁，查询非常频繁；(3)没有事务。
	//InnoDB适合：(1)可靠性要求比较高，或者要求事务；(2)表更新和查询都相当的频繁，并且表锁定的机会比较大的情况。
	
	INNODB("InnoDB"),
	ISAM("ISAM"),
	MYISAM("MyISAM");
	
	private String name;

	public String getName() {
		return name;
	}

	private DBEngineEnum(String name) {
		this.name = name;
	}
	
	
}
