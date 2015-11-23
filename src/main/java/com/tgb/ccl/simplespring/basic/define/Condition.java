package com.tgb.ccl.simplespring.basic.define;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.tgb.ccl.simplespring.basic.enums.ConditionEnum;
import com.tgb.ccl.simplespring.basic.enums.ConditionLinkEnum;
import com.tgb.ccl.simplespring.basic.map.ConditionsMap;

/**
 * 查询条件
 * 
 * @author arron
 * @date 2015年2月28日 下午2:08:52 
 * @version 1.0
 */
public class Condition {
    private static final String AND = ConditionLinkEnum.AND.getCode();
	@SuppressWarnings("unused")
	private static final String OR = ConditionLinkEnum.OR.getCode();
    public static final String PREFIX_MATCH = "P";
    public static final String SUFFIX_MATCH = "S";
    public static final String GLOBAL_MATCH = "G";
    public static final String LIKE_MODE = "L";
    public static final String CONDITION_LIKE = "CONDITION_LINK_";
    public static final String ORDER_DESC = "ORDER_DESC_";
    public static final String ORDER_ASC = "ORDER_ASC_";
    public static final char STANDARD_MODE = 0;

    List<Integer> varTypesList = new ArrayList<Integer>();
    private String conditionClauseStr = "";
    private String orderStr = "";
    private String relateOperate = AND;

    /**
     * 根据条件格式和查询参数来组装查询条件（条件之间默认用AND）
     * @param def 条件格式设定
     * @param valueMap 查询参数
     */
    @SuppressWarnings("rawtypes")
	public Condition(ConditionDef def, Map valueMap) {
        this(def, valueMap, AND);
    }
    
    /**
     * 根据条件格式和查询参数来组装查询条件，条件之间使用指定的逻辑关系
     * 
     * @param def 条件格式设定
     * @param valueMap 查询参数
     * @param relateOperate 条件之间的逻辑关系
     */
    @SuppressWarnings("rawtypes")
    public Condition(ConditionDef def, Map valueMap, ConditionLinkEnum conditionLinkEnum) {
    	this(def,valueMap,conditionLinkEnum.getCode());
    }
    
    /**
     * 根据条件格式和查询参数来组装查询条件，条件之间使用指定的逻辑关系
     * 
     * @param def 条件格式设定
     * @param valueMap 查询参数
     * @param relateOperate 条件之间的逻辑关系
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Condition(ConditionDef def, Map valueMap, String relateOperate) {
    	relateOperate = relateOperate.indexOf(" ")==-1? " "+relateOperate+" " :relateOperate;
        this.relateOperate =relateOperate; 
        String[] varNameArr = def.getConditionVarNames();
        List<String> usedSubConditionClauseList = new ArrayList();

        for (String varName : varNameArr) {
        	if(!valueMap.containsKey(varName)) {//如果不存在，则跳过
        		continue;
        	}
            if (!StringUtils.isEmpty(valueMap.get(varName).toString())) {
                usedSubConditionClauseList.add(def.getSubConditionClause(varName));
                Object priValue =  valueMap.get(varName);
                if (def.isExistClassTypeInfo(varName)) {
                    Class targetClass = def.getClassTypeInfo(varName);
                    Object newValue = null;
                    if (targetClass == java.sql.Date.class) {
                        newValue = java.sql.Date.valueOf((String)priValue);
                        valueMap.put(varName, newValue);
                    } else if (targetClass == java.sql.Timestamp.class) {
                        newValue = java.sql.Timestamp.valueOf((String)priValue);
                        valueMap.put(varName, newValue);
                    } else if (targetClass == java.sql.Time.class) {
                        newValue = java.sql.Time.valueOf((String)priValue);
                        valueMap.put(varName, newValue);
                    } else if (targetClass == java.util.List.class) {
                        List valueList=new ArrayList();
                        if (priValue.getClass().isArray()){
                            String[] valueArr=(String[])priValue;
                            for (String string : valueArr) {
                                valueList.add(string);
                            }
                        }else{
                            valueList.add(priValue);
                        }
                        valueMap.put(varName, valueList);
                    }
                }
                if (def.isExistMatchModeInfo(varName)) {
                    List<Character> matchModeList = def.getMatchModeInfo(varName);
                    if (matchModeList.size() == 1) {
                        if (matchModeList.get(0) == Condition.GLOBAL_MATCH.charAt(0)) {
                            priValue = "%" + priValue + "%";
                        } else if (matchModeList.get(0) == Condition.PREFIX_MATCH.charAt(0)) {
                            priValue = "%"+priValue ;
                        } else if (matchModeList.get(0) == Condition.SUFFIX_MATCH.charAt(0)) {
                            priValue = priValue+ "%";
                        }
                        valueMap.put(varName , priValue);
                    } else {
                        for (char currMatchMode : matchModeList) {
                            if (currMatchMode == Condition.GLOBAL_MATCH.charAt(0)) {
                                String newValue = "%" + priValue + "%";
                                valueMap.put(varName + "_globalMatch", newValue);
                            } else if (currMatchMode == Condition.PREFIX_MATCH.charAt(0)) {
                                String newValue = priValue + "%";
                                valueMap.put(varName + "_prefixMatch", newValue);
                            } else if (currMatchMode == Condition.SUFFIX_MATCH.charAt(0)) {
                                String newValue = "%" + priValue;
                                valueMap.put(varName + "_suffixMatch", newValue);
                            }
                        }
                    }
                }
            }
        }
        this.conditionClauseStr = StringUtils.join(usedSubConditionClauseList, relateOperate);
        
    }
    
    
    /**
     * 根据条件格式和查询参数来，智能拼装查询条件
     * 
     * @param def 条件格式设定
     * @param allMap 查询参数
     * @return
     */
    public static Condition buildCondition(Map<String,Object> valueMap) {
    	return buildCondition(new ConditionDef(new Object[0][0]),valueMap);
    }
    
    /**
     * 根据条件格式和查询参数来，智能拼装查询条件
     * 
     * @param def 条件格式设定
     * @param allMap 查询参数
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static Condition buildCondition(ConditionDef def,Map<String,Object> allMap) {
    	Condition condition = null;
    	
    	int countBracket =0;
    	String leftStr = "";
    	//记录所有的参数
    	Map<String,Object> valueMap = new LinkedHashMap<String, Object>();
    	//记录排序字段
    	Map<String,Object> orderMap = new LinkedHashMap<String, Object>();
    	String relationOperator = AND;
    	Map<String, Object> entryMap = new LinkedHashMap<String, Object>();
    	entryMap.putAll(allMap);
    	for(Entry<String, Object> entry :entryMap.entrySet()){
    		//System.out.println(entry.getKey()+"------"+entry.getValue());
    		
    		//如果value是ConditionsMap类型
    		if(entry.getValue() instanceof ConditionsMap){
    			ConditionsMap cmap = (ConditionsMap)entry.getValue();
    			for(Entry<String, Object> e :cmap.entrySet()){
    				if(e.getKey().equals(ConditionEnum.BETWEEN.getCode())){//自动添加2个参数
    					allMap.put(entry.getKey().trim()+"__1", ((List)e.getValue()).get(0));
    					allMap.put(entry.getKey().trim()+"__2", ((List)e.getValue()).get(1));
    				} else{
    					allMap.put(entry.getKey().trim(),e.getValue());
    				}
					valueMap.put(entry.getKey(),entry.getValue());
    			}
    		}
    		//第1个是上述条件之间的关联关系，第2个是多条件的连接关系
    		else if(entry.getKey().indexOf(CONDITION_LIKE)>=0){
    			ConditionDef newDef  = ConditionDef.buildConditionDef(def,valueMap);
    			String[] v =null;
    			if(entry.getValue() instanceof ConditionLinkEnum){//只设置了条件之间的关联关系
    				v = new String[2];
    				v[0]=null;
    				v[1]=((ConditionLinkEnum)entry.getValue()).getCode();
    				if(v[1].equals(ConditionLinkEnum.L_BRACKET.getCode())){
    					countBracket++;
    					if(countBracket>0 && condition!=null){
    						leftStr = condition.conditionClauseStr;
    						condition.conditionClauseStr="";
    					}
    					continue;
    				}
    			}else{
    				v =(String[])entry.getValue();
    			}
    			if(condition==null){
    				if(v[0]!=null){//条件关联
    					condition = new Condition(newDef, valueMap,v[0]);
        			}else{
        				condition = new Condition(newDef, valueMap);
        			}
    			}else{
    				if(v[0]!=null ){//条件关联&&连接关系
    					condition.appendCondition(new Condition(newDef, valueMap,v[0]) ,relationOperator);
        			}else{
        				condition.appendCondition(new Condition(newDef, valueMap) ,relationOperator);
        			}
    			}
    			if(v[1]!=null){//连接关联
    				 if(v[1].equals(ConditionLinkEnum.R_BRACKET.getCode())){
     					condition.appendBracket(leftStr,relationOperator);
     					relationOperator=AND;//重新设定为and
     					countBracket--;
    				 }else{
    					 relationOperator=v[1];
    				 }
				}
    			valueMap = new LinkedHashMap<String, Object>();
    			continue;
    		}else if(entry.getKey().indexOf(ORDER_ASC)>=0){
    			orderMap.put(entry.getValue().toString(),"asc");
    		}else if(entry.getKey().indexOf(ORDER_DESC)>=0){
    			orderMap.put(entry.getValue().toString(),"desc");
    		}else{
    			valueMap.put(entry.getKey(),entry.getValue());
    		}
    	}
    	
    	if(valueMap.size()>0){
	    	ConditionDef newDef  = ConditionDef.buildConditionDef(def,valueMap);
	    	if(condition==null){//之后的条件
	    		condition = new Condition(newDef, valueMap);
			}else{
				condition.appendCondition(new Condition(newDef, valueMap) ,relationOperator);
			}
    	}
    	
    	if(orderMap.size()>0){
    		StringBuffer order = new StringBuffer();
    		order.append(" order by ");
    		for(Entry<String, Object> entry :orderMap.entrySet()){
    			order.append(entry.getKey());
    			order.append(" ");
    			order.append(entry.getValue());
    			order.append(",");
    		}
    		order.deleteCharAt(order.length()-1);
    		condition.orderStr = order.toString();
    	}
		return condition;
    }
    
    /**
     * 为当前条件添加括号
     * 
     * @param leftStr 左括号前面的条件内容
     * @return
     */
    public Condition appendBracket(String leftStr,String relateOperate) {
    	if(leftStr.equals("")){
    		relateOperate = "";
    	}
    	this.conditionClauseStr = leftStr + relateOperate + "("+this.conditionClauseStr+")";
		return this;
    }
    
    
    /**
     * 2个条件关联
     * 
     * @param condition	新的条件
     * @param conditionLinkEnum	条件关联关系
     * @return
     */
    public Condition appendCondition(Condition condition,ConditionLinkEnum conditionLinkEnum) {
    	return appendCondition(condition, conditionLinkEnum.getCode());
    }
    
    /**
     * 2个条件关联
     * 
     * @param condition	新的条件
     * @param relateOperate	条件关联关系
     * @return
     */
    public Condition appendCondition(Condition condition,String relateOperate) {
    	if(this.getConditionClause().equals("")){
    		if(condition.getConditionClause().equals("")){//原条件为空 && 新条件为空
    			
    		}else{//原条件为空 ，但新条件不为空
    			this.conditionClauseStr = condition.getConditionClause();
    		}
    	}else{//原条件不为空
			if(condition.getConditionClause().equals("")){//原条件不为空 && 新条件为空
    			
    		}else{//原条件不为空 && 新条件不为空
    			this.conditionClauseStr ="("+this.getConditionClause()+")" + relateOperate + "("+condition.getConditionClause() +")" ;
    		}
    	}
		return this;
    }
    
	/**
	 * 根据map，构建Condition
	 * @param map
	 * @return
	 */
	public static Condition buildCondition(Map<String,Object> map,String relateOperate){
		return new Condition(ConditionDef.buildConditionDef(map),map,relateOperate);
	}
    
    public String getConditionClause() {
        return this.conditionClauseStr;
    }
    
    public String getConditionClauseWithWhere() {
        return "".equals(conditionClauseStr)?"":" WHERE " + conditionClauseStr;
    }
    
    public String getOrderStr() {
    	return this.orderStr;
    }
    
    public String getConditionClauseWithStartRelateOperatorIfNeeded() {
        if(conditionClauseStr.trim().equals("")) {
            return "";
        }else {
            return this.relateOperate + " " + conditionClauseStr;
        }
        
    }
    public String getConditionClauseWithRelateOperatorAtStart() {
        return this.relateOperate + " " + conditionClauseStr;
    }
    public Integer[] getConditionVarTypes() {
        return varTypesList.toArray(new Integer[]{});
    }
}