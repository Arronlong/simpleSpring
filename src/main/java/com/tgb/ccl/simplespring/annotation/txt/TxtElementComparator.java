/*********************************************************
 * 2012-2013 (c) IHARM Corporation. All rights reserved. *
 *********************************************************/
package com.tgb.ccl.simplespring.annotation.txt;

import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * TXT 字段顺序比较器
 * 
 * @author GuoYF
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public class TxtElementComparator implements Comparator {

    @Override
    public int compare(Object arg0, Object arg1) {
        Field fieldOne = (Field)arg0;
        Field fieldTwo = (Field)arg1;
        TxtElement annoOne = fieldOne.getAnnotation(TxtElement.class);
        TxtElement annoTwo = fieldTwo.getAnnotation(TxtElement.class);
        if(annoOne == null || annoTwo == null){
            return 0;
        }
        return annoOne.index() - annoTwo.index();
    }

}
