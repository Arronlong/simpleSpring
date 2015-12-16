/*********************************************************
 * 2012-2013 (c) IHARM Corporation. All rights reserved. *
 *********************************************************/
package com.tgb.ccl.simplespring.util;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tgb.ccl.simplespring.annotation.txt.TxtElement;
import com.tgb.ccl.simplespring.annotation.txt.TxtElementComparator;

/**
 * 工具类【TXT对象（String类型）与Java对象之间转换】
 * 
 * @author GuoYF
 * @version 1.0
 */
public class Txt2JavaTool {

    /**
     * 分隔符
     */
    private String split = ",";

    /**
     * 构造函数
     */
    public Txt2JavaTool(String split) {
        this.split = split;
    }

    /**
     * 取得某个类中的所有的属性（包括父类中的属性）
     */
    public static Field[] getAllFields(Class<?> clazz) {

        List<Field> fs = new ArrayList<Field>();
        Class<?> temp = clazz;
        while(temp != null) {
            Field[] fsTemp = temp.getDeclaredFields();
            if(fsTemp!=null && fsTemp.length>0) {
                for(int i=0; i<fsTemp.length; i++) {
                    fs.add(fsTemp[i]);
                }
            }
            temp = temp.getSuperclass();
        }

        return fs.toArray(new Field[]{});
    }

    /**
     * 获取含有 TXT 注解的属性
     */
    private Field[] getTxtFields(Field[] fields) {

        List<Field> fs = new ArrayList<Field>();
        for (Field f : fields) {
        	TxtElement annotation = f.getAnnotation(TxtElement.class);
            if (annotation != null && annotation.required()) {
                fs.add(f);
            }
        }
        return fs.toArray(new Field[]{});
    }

    /**
     * java对象 转 TXT对象
     * @throws NoSuchMethodException 
     * @throws SecurityException 
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    @SuppressWarnings("unchecked")
	public void write(Object obj, Writer out) throws IOException {

        // 1、获取含有 TXT 注解的属性
        @SuppressWarnings("rawtypes")
		Class clazz = obj.getClass();
        Field[] fields = getTxtFields(getAllFields(clazz));
        // 2、按照注解index排序 TXT 列
        Arrays.sort(fields, new TxtElementComparator());
        //使用split链接起来
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
  			try {
  				out.append(String.valueOf(fields[i].get(obj)));
  			} catch (IllegalArgumentException | IllegalAccessException e) {
  				throw new IOException("java对象 转 TXT对象失败："+e.getMessage(), e);
  			}
  			if(i+1<fields.length){//不是最后一个，则添加分隔符
  				out.append(split);//添加分隔符
  			}
  		}
    }

    /**
     *  TXT对象 转 java对象
     * @throws IOException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    @SuppressWarnings("unchecked")
	public Object read(String txtStr, Class<?> clazz) throws IOException {
        
        if(txtStr==null || txtStr.length()==0) {
            throw new IOException("TXT对象 转 java对象失败：TXT不能为空！");
        }
        // 1、获取含有 TXT 注解的属性
        Field[] fields = getTxtFields(getAllFields(clazz));
        // 2、按照注解index排序 TXT 列
        Arrays.sort(fields, new TxtElementComparator());
        // 3、创建新对象
        Object res;
        try {
            res = clazz.newInstance();
        } catch (InstantiationException e1) {
            throw new IOException("TXT对象 转 java对象失败："+e1.getMessage(), e1);
        } catch (IllegalAccessException e1) {
            throw new IOException("TXT对象 转 java对象失败："+e1.getMessage(), e1);
        }
        // 4、按顺序填充
        String[] values = txtStr.split(split);
        for (int i = 0; i < values.length; i++) {
            String val = values[i];
            if(fields.length > i) {
                Field f = fields[i];
                try {
                    f.setAccessible(true);
                    f.set(res, ConvertUtil.convertGt(val, f.getType()));
                } catch (Exception e) {
                    throw new IOException("TXT对象 转 java对象失败："+e.getMessage(), e);
                }
            }
        }
        return res;
    }

    public String getSplit() {
        return split;
    }
}
