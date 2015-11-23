/*********************************************************
 * 2012-2013 (c) IHARM Corporation. All rights reserved. *
 *********************************************************/
package com.tgb.ccl.simplespring.annotation.txt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Txt实体BEAN的属性注解
 * 
 * @author GuoYF
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TxtElement {

    /**
     * 字段是否必填项
     */
    public boolean required() default true; 

    /**
     * 字段排序顺序
     */
    int index() default 0;
}
