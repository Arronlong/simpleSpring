package com.tgb.ccl.simplespring.basic.enums;

public enum GenerateStrategyEnum {

	/**
	 * 全部大写
	 */
	AU("AU","ALL_UPPER","全部大写"),
	/**
	 * 全部小写
	 */
	AL("AL","ALL_LOWER","全部小写"),
	/**
	 * 首字母大写
	 */
	FU("FU","FIRST_UPPER","首字母大写"),
	/**
	 * 首字母小写
	 */
	FL("FL","FIRST_LOWER","首字母小写"),
	/**
	 * 大小写不变
	 */
	NORMAL("NORMAL","NORMAL","大小写不变");
	
	private String code;
	private String name;
	private String desc;
	
	private GenerateStrategyEnum(String code,String name,String desc){
		this.code=code;
		this.name=name;
		this.desc=desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
