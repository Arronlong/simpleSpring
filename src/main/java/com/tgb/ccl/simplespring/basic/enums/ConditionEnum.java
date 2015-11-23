package com.tgb.ccl.simplespring.basic.enums;

/**
 * 条件关系
 * 
 * @author arron
 * @date 2015年3月5日 下午4:11:22 
 * @version 1.0
 */
public enum ConditionEnum {
	/**
	 * 小于
	 */
    LT ( " < "),
    /**
     * 大于
     */
    GT ( " > "),
    /**
     * 等于
     */
    EQ ( " = "),
    /**
     * 大于等于
     */
    EGT ( " >= "),
    /**
     * 小于等于
     */
    ELT ( " <="),
    /**
     * 不等于
     */
    NEQ ( " <> "),
    /**
     *  in
     */
    IN ( " in "),
    /**
     * like
     */
    LIKE ( " like "),
    /**
     * is null
     */
    NULL ( " is null "),
    /**
     * is not null
     */
    NOTNULL ( " is not null "),
    /**
     * between
     */
    BETWEEN ( " between ");
    
    private final String code ;
    
    private ConditionEnum(String code){
    	this.code=code;
    }

	public String getCode() {
		return code;
	}
	
	public String toString(){
		return getCode();
	}
}
