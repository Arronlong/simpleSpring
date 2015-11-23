package com.tgb.ccl.simplespring.basic.map;

import java.util.LinkedHashMap;

import com.tgb.ccl.simplespring.basic.define.Condition;
import com.tgb.ccl.simplespring.basic.enums.ConditionEnum;

/**
 * 专用于条件连接时自动转化key值的
 * 
 * @author arron
 * @date 2015年2月28日 上午11:12:40 
 * @version 1.0
 * @param <K>
 * @param <V>
 */
public class ConditionLinkMap<K, V> extends LinkedHashMap<K, V>{

	private static final long serialVersionUID = -4569102155964359373L;

	@SuppressWarnings("unchecked")
	@Override
	public V put(K key, V value) {
		if(value.equals(ConditionEnum.NULL) || value.equals(ConditionEnum.NOTNULL)){
			return put(key, value,"");
		}
		String k = key.toString();
		//对于条件连接、排序等，自动添加后缀，从1开始递增
		if(k.equals(Condition.CONDITION_LIKE )||k.equals(Condition.ORDER_ASC)||k.equals(Condition.ORDER_DESC)){
			int i=1;
			String newKey = key.toString();
			while(this.containsKey(newKey+i)){
				i++;
			}
			key=(K)(newKey+i);
		}
		return super.put(key, value);
	}
	
	
	/**
	 * 快速赋值
	 * <br/>
	 * 将&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;：
	 * 			map.put("id", new ConditionsMap().put(ConditionEnum.IN, "111,222"));<br/>
	 * 简化为：
	 * 			map.put("id", ConditionEnum.IN, "111,222");
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public V put(K key, Object... values ) {
		if(values.length==2){
			return put(key, (V)new ConditionsMap().put(values[0].toString(),values[1]));
		}
		return null;
	}

}
