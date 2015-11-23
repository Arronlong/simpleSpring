package com.tgb.ccl.simplespring.basic.enums;
import java.util.*;

public enum FieldTypeEnum {

	CHAR("char"),
	VARCHAR("varchar"),
	TINYTEXT ("tinytext"),
	TEXT ("text"),
	TINYINT  ("tinyint"),
	SMALLINT ("smallint"),
	MEDIUMINT("mediumint"),
	INT("int"),
	BIGINT ("bigint"),
	FLOAT ("float"),
	DOUBLE ("double"),
	DECIMAL ("decimal"),
	Date("date"),
	Time("time"),
	YEAR ("year"),
	DateTime("datetime"),
	TimeStamp("timestamp")
	;
	
	private String name;

	private FieldTypeEnum(String name ){
		this.name=name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取字段类型
	 * @param filetype
	 * @return
	 */
	public static FieldTypeEnum getFieldType(Class<?> filetype){
		if(filetype == String.class){
			return FieldTypeEnum.VARCHAR;
		}else if(filetype == Integer.class){
			return FieldTypeEnum.INT;
		}else if(filetype == Float.class){
			return FieldTypeEnum.FLOAT;
		}else if(filetype == Date.class){
			return FieldTypeEnum.TimeStamp;
		}else{
			return FieldTypeEnum.VARCHAR;
		}
	}
}
