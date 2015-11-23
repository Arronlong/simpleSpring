package com.tgb.ccl.simplespring.basic.define;

import com.tgb.ccl.simplespring.basic.enums.FieldTypeEnum;

public class FieldInfo {
    //java字段名
    private String pojoFieldName; 
    //数据库字段名
    private String dbFieldName;
    //是否是主键
    private boolean isPk = false;
    //update时是否需要更新
    private boolean isUpdate = true;
    //insert时是否需要插入
    private boolean isInsert = true;
    //create时是否需要创建
    private boolean isCreate=true;
    //存储类型
    private FieldTypeEnum filedType=FieldTypeEnum.VARCHAR;
    //字段注释
    private String comment="";
    //字段长度
    private int length=255;
    
    //小数位长度
    private int sLength=-1;
    
    //是否允许为null
    private boolean isNull = true;
    
    //类型
    private Class<?> type;
   
    public String getPojoFieldName() {
        return pojoFieldName;
    }
    public void setPojoFieldName(String pojoFieldName) {
        this.pojoFieldName = pojoFieldName;
    }
    public String getDbFieldName() {
        return dbFieldName;
    }
    public void setDbFieldName(String dbFieldName) {
        this.dbFieldName = dbFieldName;
    }
    public boolean isNull() {
        return isNull;
    }
	public void setIsNull(boolean isNull) {
		this.isNull = isNull;
	}
	public boolean isCreate() {
		return isCreate;
	}
	public void setIsCreate(boolean isCreate) {
		this.isCreate = isCreate;
	}
	public boolean isInsert() {
		return isInsert;
	}
	public void setIsInsert(boolean isInsert) {
		this.isInsert = isInsert;
	}
    public boolean isUpdate() {
        return isUpdate;
    } 
    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }
    public boolean isPk() {
        return isPk;
    }
    public void setIsPk(boolean isPk) {
        this.isPk = isPk;
    }
	public Class<?> getType() {
		return type;
	}
	public void setType(Class<?> type) {
		this.type = type;
	}
	public FieldTypeEnum getFiledType() {
		return filedType;
	}
	public void setFiledType(FieldTypeEnum filedType) {
		this.filedType = filedType;
	}
	public void setFiledType(Class<?> filetype) {
		this.filedType = FieldTypeEnum.getFieldType(filetype);
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getsLength() {
		return sLength;
	}
	public void setsLength(int sLength) {
		this.sLength = sLength;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
}