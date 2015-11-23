/*********************************************************
 * 2012-2013 (c) IHARM Corporation. All rights reserved. *
 *********************************************************/
package com.tgb.ccl.simplespring.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/**
 * 工具类【TXT对象（String类型）与Java对象之间转换】
 * 
 * @author GuoYF
 * @version 1.0
 */
public class Txt2JavaUtil {

    /**
     * 需要进行转义的字符（针对String.split()方法）
     */
    private final static List<String> SPECIAL_CHAR_LIST = Arrays.asList(new String[]{
           "|",
           "^"
           });
    
    /**
     * Java对象 转 TXT 对象（String类型）
     */
    public static String java2TXT(Object obj, String split) throws IOException {

        Txt2JavaTool tool = new Txt2JavaTool(split);
        Writer out = new StringWriter();
        tool.write(obj, out);
        String txt = out.toString();
        return txt;
    }

    /**
     * TXT对象（String类型） 转 Java对象
     */
    @SuppressWarnings("rawtypes")
	public static Object TXT2Java(String txtStr, Class clazz, String split) throws IOException {

        if(SPECIAL_CHAR_LIST.contains(split)) {
            split = "\\"+split;
        }
        Txt2JavaTool tool = new Txt2JavaTool(split);
        Object res = tool.read(txtStr, clazz);
        return res;
    }
}
