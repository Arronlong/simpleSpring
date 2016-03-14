package com.tgb.ccl.simplespring.basic.define;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tgb.ccl.simplespring.basic.enums.ConditionEnum;
import com.tgb.ccl.simplespring.basic.map.ConditionsMap;
import com.tgb.ccl.simplespring.util.PagingUtils;

public class ConditionDef {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, String> paraNameAndSubConditionClauseMap = new LinkedHashMap();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Class> paraNameAndClassTypeMap = new HashMap();
	@SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String, List<Character>> paraNameAndLikeMatchInfoMap = new HashMap();
    
    @SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public ConditionDef(Object[][] defineArr) {
        for (Object[] subDefine : defineArr) {
        	if(subDefine[0]==null) continue;

            Pattern pattern = Pattern.compile(":([\\w\\d_]+)");
            String currDefClause = (String) subDefine[0];
            int currDefClauseLen = currDefClause.length();
            Matcher matcher = pattern.matcher(currDefClause);
            //System.out.println(currDefClause);
            Set<String> varNameSet = new HashSet<String>();
            int varNameCount = 0;
            
            char clauseMode = Condition.STANDARD_MODE;
            String oneVarName = null;
            boolean isUsedSameMatchMode=true;
            List<Character> matchModeList=new ArrayList();
            while (matcher.find()) {
                String varName = matcher.group(1);
                
                oneVarName = varName;
                int start = matcher.start();
                int end = matcher.end();
                char prefix = currDefClause.charAt(start - 1);

                char suffix = end >= currDefClauseLen ? ' ' : currDefClause.charAt(end);
                char subConditionMatchMode = Condition.STANDARD_MODE;
                if (prefix == '%' && suffix == '%') {
                    clauseMode = subConditionMatchMode = Condition.GLOBAL_MATCH.charAt(0);
                    matchModeList.add(clauseMode);
                    
                } else if (prefix == '%') {
                    clauseMode = subConditionMatchMode = Condition.PREFIX_MATCH.charAt(0);
                    matchModeList.add(clauseMode);
                } else if (suffix == '%') {
                    clauseMode = subConditionMatchMode = Condition.SUFFIX_MATCH.charAt(0);
                    matchModeList.add(clauseMode);
                }

                varNameSet.add(varName);
                varNameCount++;
                if(varNameCount>1&&matchModeList.size()>=2) {
                    int size=matchModeList.size();
                    if(!matchModeList.get(size-1).equals(matchModeList.get(size-2))) {
                        isUsedSameMatchMode=false;
                    }
                }
            }

            if (varNameSet.size() != 1) {
            	if(varNameSet.size() ==2 && currDefClause.indexOf(" between ")>=0 && currDefClause.indexOf(" and ")>=0){
            		oneVarName = oneVarName.substring(0,oneVarName.indexOf("__"));
            	}else if(varNameSet.size() ==0 && currDefClause.indexOf(" is ")>=0 && currDefClause.indexOf(" null ")>=0){
            		oneVarName = currDefClause.substring(0,currDefClause.indexOf(" "));
            	}else{
            		throw new RuntimeException("One sub condition clause must only have one var name ! clause :"
                        + currDefClause);
            	}
            }
            if (oneVarName == null) {
                throw new RuntimeException("Sub condition is not have any var name ! clause :" + currDefClause);
            }
            
            if (subDefine.length > 1) {

                paraNameAndClassTypeMap.put(oneVarName, (Class) subDefine[1]);
                //System.out.println("save var type : " + oneVarName + "," + ((Class) subDefine[1]).getSimpleName());
            }
            if (clauseMode != Condition.STANDARD_MODE) {
                if (isUsedSameMatchMode&&varNameCount==matchModeList.size()) {

                    paraNameAndLikeMatchInfoMap.put(oneVarName, matchModeList.subList(0,1));
                } else {
                    currDefClause=currDefClause.replaceAll("%:"+oneVarName+"%", ":"+oneVarName+"_globalMatch");
                    currDefClause=currDefClause.replaceAll("%:"+oneVarName, ":"+oneVarName+"_suffixMatch");
                    currDefClause=currDefClause.replaceAll(":"+oneVarName+"%", ":"+oneVarName+"_prefixMatch");
                    paraNameAndLikeMatchInfoMap.put(oneVarName, matchModeList);
                }
                currDefClause = currDefClause.replaceAll("'\\%", "");
                currDefClause = currDefClause.replaceAll("\\%'", "");
                currDefClause = currDefClause.replaceAll("\\%", "");
                currDefClause = currDefClause.replaceAll("'", "");
                //System.out.println("processed clause : " + currDefClause);
            }
            String tempClause=currDefClause.toUpperCase();
            if(tempClause.indexOf(" AND ")!=-1||tempClause.indexOf(" OR ")!=-1) {
                currDefClause="("+currDefClause+")";
            }
            paraNameAndSubConditionClauseMap.put(oneVarName, currDefClause);

        }

    }

    /**
     * 根据map，构建conditionDef
     * @param map
     * @return
     */
    @SuppressWarnings("unused")
	public static ConditionDef buildConditionDef(Map<String,Object> map){
    	Object[][] keys = new Object[map.size()][1];
    	int i=0;
		List<String> ignoreList = Arrays.asList(PagingUtils.PAGE_NUM,
				PagingUtils.PAGE_SEARCH, PagingUtils.PAGE_SIZE,"ORDER");

    	for (Entry<String,Object> entry :map.entrySet()) {
    		Object value = entry.getValue();
    		String key = entry.getKey();

    		//跳过分页、排序、分组等
    		if(ignoreList.contains(key)){
    			continue;
    		}else if (key.indexOf("_")>0){
    			if(ignoreList.indexOf(key.substring(0,key.indexOf("_")))>=0){
    				continue;
    			}
    		}
    		
    		if(value instanceof ConditionsMap){
    			ConditionsMap cmap = (ConditionsMap) value;
    			for (Entry<String,Object> e :cmap.entrySet()) {
    				if(e.getKey().equals(ConditionEnum.BETWEEN.getCode())){//自动添加2个参数
            			keys[i][0]= key+ e.getKey() + ":" + key+"__1 and :" + key +"__2";
    				} else if(e.getKey().equals(ConditionEnum.NOTNULL.getCode()) ||
    						e.getKey().equals(ConditionEnum.NULL.getCode()) ){//自动添加2个参数
    					keys[i][0]= key+ e.getKey() ;
    				} else if(e.getKey().equals(ConditionEnum.IN.getCode())){
    					keys[i][0]= key+ e.getKey() + "(:" + key +")";
            		}else{
            			keys[i][0]= key+ e.getKey() +":" + key;
            			map.put(key, e.getValue());
            		}
    			}
    		}else{
	    		if(value.toString().indexOf("%")==-1){
	    			keys[i][0]= key+" = :"+key;
	    		}else{
	    			keys[i][0]= key+" like :"+key;
	    		}
    		}
    		i++;
    	}
    	return new ConditionDef(keys);
    }

    /**
	 * 根据map，构建conditionDef
	 * @param map
	 * @return
	 */
	public static ConditionDef buildConditionDef(ConditionDef def ,Map<String,Object> map){
		Object[][] keys = new Object[map.size()][1];
		if(PagingUtils.isPagingSearchRequest(map)){
			keys = new Object[map.size()-1][1];
		}
		int i=0;
		for (String key :map.keySet()) {
			if(key.equals(PagingUtils.PAGE_SEARCH)){
				continue;
			}
			
			String value = map.get(key).toString();
			if(def.paraNameAndSubConditionClauseMap.containsKey(key)){
				keys[i][0] = def.getSubConditionClause(key);
			}else{
				if(map.get(key) instanceof ConditionsMap){
	    			ConditionsMap cmap = (ConditionsMap) map.get(key);
	    			for (Entry<String,Object> e :cmap.entrySet()) {
	    				if(e.getKey().equals(ConditionEnum.BETWEEN.getCode())){//自动添加2个参数
	            			keys[i][0]= key+ e.getKey() + ":" + key+"__1 and :" + key +"__2";
	    				} else if(e.getKey().equals(ConditionEnum.NOTNULL.getCode()) ||
	    						e.getKey().equals(ConditionEnum.NULL.getCode()) ){//自动添加2个参数
	    					keys[i][0]= key+ e.getKey() ;
	    				} else if(e.getKey().equals(ConditionEnum.IN.getCode())){
	    					keys[i][0]= key+ e.getKey() + "(:" + key +")";
	            		}else{
	            			keys[i][0]= key+ e.getKey() + ":" + key;
	            		}
	    			}
				}else{
					if(value.indexOf("%")==-1){
						keys[i][0]= key+" = :"+key;
					}else{
						keys[i][0]= key+" like :"+key;
					}
				}
			}
			i++;
		}
		return new ConditionDef(keys);
	}

    public String[] getConditionVarNames() {
        // TODO Auto-generated method stub
        return paraNameAndSubConditionClauseMap.keySet().toArray(new String[paraNameAndSubConditionClauseMap.keySet().size()]);
    }
    public String getSubConditionClause(String varName) {
        // TODO Auto-generated method stub
        return paraNameAndSubConditionClauseMap.get(varName);
    }
    public boolean isExistClassTypeInfo(String varName) {
        // TODO Auto-generated method stub
        return paraNameAndClassTypeMap.containsKey(varName);
    }
    @SuppressWarnings("rawtypes")
	public Class getClassTypeInfo(String varName) {
        // TODO Auto-generated method stub
        return paraNameAndClassTypeMap.get(varName);
    }
    public boolean isExistMatchModeInfo(String varName) {
        // TODO Auto-generated method stub
        return paraNameAndLikeMatchInfoMap.containsKey(varName);
    }
    public List<Character> getMatchModeInfo(String varName) {
        // TODO Auto-generated method stub
        return paraNameAndLikeMatchInfoMap.get(varName);
    }
}