package com.tgb.ccl.simplespring.basic.enums;

/**
 * 	条件关联关系
 * 
 * @author arron
 * @date 2015年3月5日 下午4:10:59 
 * @version 1.0
 */
public enum ConditionLinkEnum {
    AND ( " AND "),
    OR ( " OR "),
    L_BRACKET  ( " ( "),
    R_BRACKET ( " ) ");
    
    private final String code ;
    
    private ConditionLinkEnum(String code){
    	this.code=code;
    }

	public String getCode() {
		return code;
	}
}
