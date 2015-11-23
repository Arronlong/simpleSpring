package com.tgb.ccl.simplespring.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tgb.ccl.simplespring.basic.enums.FieldTypeEnum;
import com.tgb.ccl.simplespring.basic.enums.GenerateStrategyEnum;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    
	//数据库字段名
	public String value() default "";    
	//单词间是否添加下划线，当table中设置了isForce4Column=true,该设定失效
    public boolean isLineSplit() default false;
    //字段名的生成策略,当table中设置了isForce4Column=true,该设定失效
    public GenerateStrategyEnum FieldGeneratorStrategy() default GenerateStrategyEnum.NORMAL;
    
    //字段存储类型
    public FieldTypeEnum type() default FieldTypeEnum.VARCHAR;
    
    //字段长度
    public int length() default 255;
    
    //小数位长度
    public int sLength() default -1;
    
    //字段描述
    public String comment() default "";
    
    //用于view创建，表示多个表存在相同字段时，使用哪个表来标记该字段,如：a.id
    public Class<?> from() default Object.class;
    
    //用于view创建，表示多表查询的关联关系
    public Class<?>[] on() default  {};
    
    //列描述
    public String columnDefinition() default "";
    
}