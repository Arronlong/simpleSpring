package com.tgb.ccl.simplespring.basic.map;

import java.util.List;

import com.tgb.ccl.simplespring.basic.enums.ConditionEnum;

/**
 * 可组装条件的map
 * 
 * 格式如下k-v，k-{k-v}，k-{k-list}<br/>
 * 	具体为：<br/>
 * 	  			k-v  举例：id - 1111<br/>
 * 				k-{k-v}  举例：id - {> - 1111}<br/>
 * 				k-{k-list}  举例：id - {in - {1111,2222}} 或者 date - {between - {yesteday,today}}<br/>
 * 
 * @author arron
 * @date 2015年3月5日 下午1:03:07 
 * @version 1.0
 */
public class ConditionsMap extends ConditionLinkMap<String, Object> {
	private static final long serialVersionUID = -5680666714821294923L;
	
	public ConditionsMap put(String key,Object value){
		super.put(key, value);
		return this;
	}
	
	public ConditionsMap put(ConditionEnum conditonEnum){
		return put(conditonEnum, "");
	}
	
	@SuppressWarnings("rawtypes")
	public ConditionsMap put(ConditionEnum conditonEnum,Object value){
		if(conditonEnum==ConditionEnum.BETWEEN){
			if(value instanceof List){
				if(((List)value).size()==2){
				}else{
					throw new RuntimeException("设定BETWEEN时，必须设定2个值！");
				}
			}else{
				throw new RuntimeException("设定BETWEEN时，值必须是List类型！");
			}
		}
		super.put(conditonEnum.getCode(), value);
		return this;
	}
}
