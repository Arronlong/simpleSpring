package com.tgb.ccl.simplespring.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tgb.ccl.simplespring.basic.enums.DBEngineEnum;
import com.tgb.ccl.simplespring.basic.enums.DBObjectEnum;
import com.tgb.ccl.simplespring.basic.enums.GenerateStrategyEnum;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
	
	//数据库表名
    public String value() default "";
    //类型：表或者视图
    public DBObjectEnum type() default DBObjectEnum.TABLE;

    //内连接
    public Class<?> [] joins() default {};
    
    //做连接
    public Class<?> [] leftJoins() default {};
    
    //表名：单词间是否添加下划线
    public boolean isLineSplit() default false;
    //表名的生成策略
    public GenerateStrategyEnum TableGeneratorStrategy() default GenerateStrategyEnum.NORMAL; 
    
    //是否强制所有字段使用统一设置
    public boolean isForce4Column() default false;
    //字段名：单词间是否添加下划线
    public boolean isLineSplit4Column() default false;
    
    //字段名的生成策略
    public GenerateStrategyEnum FieldGeneratorStrategy() default GenerateStrategyEnum.NORMAL;
    
    //mysql引擎
    public DBEngineEnum engine() default DBEngineEnum.INNODB;
    
}