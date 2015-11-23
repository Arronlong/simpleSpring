package com.tgb.ccl.simplespring.basic;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tgb.ccl.simplespring.annotation.Column;
import com.tgb.ccl.simplespring.annotation.NoCreate;
import com.tgb.ccl.simplespring.annotation.NoInsert;
import com.tgb.ccl.simplespring.annotation.NoUpdate;
import com.tgb.ccl.simplespring.annotation.PK;
import com.tgb.ccl.simplespring.annotation.Table;
import com.tgb.ccl.simplespring.basic.enums.GenerateStrategyEnum;
import com.tgb.ccl.simplespring.util.ReflectUtils;
import com.tgb.ccl.simplespring.util.SQLUtils;

public abstract class Entity implements Serializable {

	@NoInsert
	@NoUpdate
	@NoCreate
	private static final long serialVersionUID = -768904040326420345L;
	
	@NoInsert
	@NoUpdate
	@NoCreate
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public abstract Entity setId(Object id);

	public abstract Object getId();

	/**
	 * 获取主键字段名
	 * 
	 * @return
	 */
	@JsonIgnore
	public String getPKName(){
		Field pkField =getPKField();
		
		String pkName = pkField.getName();

		boolean isForce =false;//是否强制统一字段设置
		boolean isLineSplit = false;//单词间是否添加下划线
		String strategyCode=GenerateStrategyEnum.NORMAL.getCode();
		if (getClass().isAnnotationPresent(Table.class)) {
			Table table = (Table) getClass().getAnnotation(Table.class);
			isForce=table.isForce4Column();
			if(isForce){//如果强制设置，则获取设置的策略等
				isLineSplit=table.isLineSplit4Column();
				strategyCode = table.FieldGeneratorStrategy().getCode();
			}else{
				if(pkField.isAnnotationPresent(Column.class)){//使用
					Column col =(Column) pkField.getAnnotation(Column.class);
					String value = col.value();// 得到配置的数据库字段名
					isLineSplit=col.isLineSplit();//得到配置的是否添加下划线
					strategyCode = col.FieldGeneratorStrategy().getCode();
					if (!StringUtils.isEmpty(value)) {// 没有设置数据库的字段名，则取pojo的字段名
						pkName = value;
					}
				}
			}
			pkName = SQLUtils.changeStrByStrategy(pkName,isLineSplit,strategyCode);
		}
		return pkName;
	}

	/**
	 * 获取主键属性值
	 * 
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@JsonIgnore
	public Object getPKValue() throws IllegalArgumentException, IllegalAccessException{
		Field pkField =getPKField();
		return pkField.get(this);
	}
	
	
	/**
	 * 获取主键字段
	 * @return
	 */
	@JsonIgnore
	public Field getPKField(){
		Field pkField =null;
//		Field[] fields = getClass().getDeclaredFields();
		Field[] fields = ReflectUtils.getFields(getClass());
		for(Field field:fields){
			field.setAccessible( true ); 
			if(field.isAnnotationPresent(PK.class)){//如果是主键
				pkField=field;
				break;
			}
		}
		
		if(pkField==null){//如果未设定了主键字段，则去第一个字段未默认的主键字段。
			pkField = fields[0];
		}
		return pkField;
	}

	@Override
	public String toString() {
		Field[] fields = ReflectUtils.getFields(getClass());
		StringBuffer result = new StringBuffer();
		for(Field field : fields){
			field.setAccessible( true ); 
			if(field.getName().equals("serialVersionUID")) continue;
			if(field.getName().equals("logger")) continue;
			result.append(field.getName());
			result.append("=");
			try  
			{  
				result.append(field.get(this));
			} catch (Exception e) {
				result.append("null");
			}
			result.append(",");
		}
		if(fields.length>0){
			result.deleteCharAt(result.length()-1);
		}
		return result.toString();
	}
}
