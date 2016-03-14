package com.tgb.ccl.simplespring.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.tgb.ccl.simplespring.annotation.Column;
import com.tgb.ccl.simplespring.annotation.NoCreate;
import com.tgb.ccl.simplespring.annotation.NoInsert;
import com.tgb.ccl.simplespring.annotation.NoNULL;
import com.tgb.ccl.simplespring.annotation.NoUpdate;
import com.tgb.ccl.simplespring.annotation.PK;
import com.tgb.ccl.simplespring.annotation.Table;
import com.tgb.ccl.simplespring.basic.define.FieldInfo;
import com.tgb.ccl.simplespring.basic.enums.DBEngineEnum;
import com.tgb.ccl.simplespring.basic.enums.DBObjectEnum;
import com.tgb.ccl.simplespring.basic.enums.GenerateStrategyEnum;
import com.tgb.ccl.simplespring.exception.ServiceException;

public class SQLUtils {

	private final static Logger logger = LoggerFactory.getLogger(SQLUtils.class);

	//sql
	@SuppressWarnings("rawtypes")
	private static Map cacheMap = new LinkedHashMap();
	@SuppressWarnings("rawtypes")
	private static Map insertSqlCache = new LinkedHashMap();
	@SuppressWarnings("rawtypes")
	private static Map updateSqlCache = new LinkedHashMap();
	@SuppressWarnings("rawtypes")
	private static Map deleteSqlCache = new LinkedHashMap();
	@SuppressWarnings("rawtypes")
	private static Map selectSqlCache = new LinkedHashMap();
	
	//设置缓存
	@SuppressWarnings("rawtypes")
	private static Map isForceCache = new LinkedHashMap();
	@SuppressWarnings("rawtypes")
	private static Map isLineSplitCache = new LinkedHashMap();
	@SuppressWarnings("rawtypes")
	private static Map strategyCodeCache = new LinkedHashMap();

	/**
	 * 根据pojo类的class来构建select * from 的SQL语句
	 * 
	 * @param pojoClass
	 * @return
	 */
	public static String buildSelectSql(Class<?> pojoClass) {
		List<FieldInfo> fieldInfoList = loadPojoSqlInfo(pojoClass);
		String sql = buildSelectSql(pojoClass, fieldInfoList);
		return sql;
	}
	
	/**
	 * 根据pojo类的class来构建Create的SQL语句
	 * 
	 * @param pojoClass
	 * @return
	 */
	public static String buildCreateSql(Class<?> pojoClass) {
		List<FieldInfo> fieldInfoList = loadPojoSqlInfo(pojoClass);
		String sql = buildCreateSql(pojoClass, fieldInfoList);
		return sql;
	}
	
	/**
	 * 根据pojo类的class来构建insert的SQL语句
	 * 
	 * @param pojoClass
	 * @return
	 */
	public static String buildInsertSql(Class<?> pojoClass) {
		List<FieldInfo> fieldInfoList = loadPojoSqlInfo(pojoClass);
		String sql = buildInsertSql(pojoClass, fieldInfoList);
		return sql;
	}

	/**
	 * 根据pojo类的class构建根据pk来update的SQL语句
	 * 
	 * @param pojoObject
	 * @return
	 */
	public static String buildUpdateSql(Class<?> pojoClass) {
		List<FieldInfo> fieldInfoList = loadPojoSqlInfo(pojoClass);
		String sql = buildUpdateSqlByPK(pojoClass, fieldInfoList);
		return sql;
	}

	/**
	 * 根据pojo类的Class和更新的条件字段来生成upate的SQL语句
	 * 
	 * @param pojoClass
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	public static String buildUpdateSqlByColumns(Class<?> pojoClass,
			String[] columns) throws ServiceException {
		if (null != columns && columns.length > 0) {
			List<FieldInfo> fieldInfoList = loadPojoSqlInfo(pojoClass);
			String sql = buildUpdateSqlByColumns(pojoClass, fieldInfoList,
					columns);
			return sql;
		} else {
			logger.debug("生成update sql error! 参数columns必须有值");
			throw new ServiceException("参数columns必须有值！");
		}
	}

	/**
	 * 根据pojo类的Class生成根据pk来delete的SQL语句
	 * 
	 * @param pojoClass
	 * @return
	 */
	public static String buildDeleteSql(Class<?> pojoClass) {
		List<FieldInfo> fieldInfoList = loadPojoSqlInfo(pojoClass);
		String sql = buildDeleteSqlByPK(pojoClass, fieldInfoList);
		return sql;
	}

	/**
	 * 根据pojo类的Class和更新的条件字段来生成delete的SQL语句
	 * 
	 * @param pojoClass
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	public static String buildDeleteSqlByColumns(Class<?> pojoClass,
			String[] columns) throws Exception {
		if (null != columns && columns.length > 0) {
			List<FieldInfo> fieldInfoList = loadPojoSqlInfo(pojoClass);
			String sql = buildDeleteSqlByColumns(pojoClass, fieldInfoList,
					columns);
			return sql;
		} else {
			logger.debug("生成delete sql error! 参数columns必须有值");
			throw new Exception("参数columns必须有值！");
		}
	}


	/**
	 * 加载读取pojo的注解信息
	 * 
	 * @param pojoClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<FieldInfo> loadPojoSqlInfo(Class<?> pojoClass) {
		List<FieldInfo> resultList = null;
		if (null == cacheMap.get(pojoClass.getName())) {
			
			boolean isForce =false;//是否强制统一字段设置
			boolean isLineSplit = false;//单词间是否添加下划线
			String strategyCode=GenerateStrategyEnum.NORMAL.getCode();
			
			if (pojoClass.isAnnotationPresent(Table.class)) {
				Table table = (Table) pojoClass.getAnnotation(Table.class);
				if(null ==isForceCache.get(pojoClass.getName())){//判断是否缓存
						isForce=table.isForce4Column();
						isForceCache.put(pojoClass.getName(), isForce);
				}else{
					isForce=(boolean)isForceCache.get(pojoClass.getName());
				}
				if(isForce){//如果强制设置，则获取设置的策略等
					if(null ==isLineSplitCache.get(pojoClass.getName())){//判断是否缓存
						isLineSplit=table.isLineSplit4Column();
						isLineSplitCache.put(pojoClass.getName(), isLineSplit);
					}else{
						isLineSplit = (boolean)isLineSplitCache.get(pojoClass.getName());
					}
					if(null ==isLineSplitCache.get(pojoClass.getName())){//判断是否缓存
						strategyCode = table.FieldGeneratorStrategy().getCode();
						strategyCodeCache.put(pojoClass.getName(), isLineSplit);
					}else{
						strategyCode =(String)strategyCodeCache.get(pojoClass.getName());
					}
				}
			}
			
			resultList = new ArrayList<FieldInfo>();

			Field[] fields = ReflectUtils.getFields(pojoClass);
			for (Field field : fields) {
				 // 如果不为空，设置可见性，然后返回  
	            field.setAccessible( true );  
	            
				FieldInfo fieldInfo = new FieldInfo();
				fieldInfo.setPojoFieldName(field.getName());
				
				boolean isSplit=false;//得到配置的是否添加下划线
				String code=GenerateStrategyEnum.NORMAL.getCode();;
				//判断是否有设置Column注解
				if (field.isAnnotationPresent(Column.class)) {
					Column col =(Column) field.getAnnotation(Column.class);
					fieldInfo.setFiledType(col.type());
					fieldInfo.setComment(col.comment());
					fieldInfo.setLength(col.length());
					fieldInfo.setsLength(col.sLength());
					isSplit=col.isLineSplit();//得到配置的是否添加下划线
					code = col.FieldGeneratorStrategy().getCode();
					if(isForce){//使用统一字段设定
						isSplit=isLineSplit;
						code=strategyCode;
						fieldInfo.setDbFieldName(changeStrByStrategy(field.getName(),isLineSplit,strategyCode));
						isLineSplitCache.put(field.getName(), isLineSplit);
						strategyCodeCache.put(field.getName(), strategyCode);
					}else{
						String value = col.value();// 得到配置的数据库字段名
						if (StringUtils.isEmpty(value)) {// 没有设置数据库的字段名，则取pojo的字段名
							fieldInfo.setDbFieldName(changeStrByStrategy(field.getName(),isSplit,code));
						} else {
							fieldInfo.setDbFieldName(changeStrByStrategy(value,isSplit,code));
						}
					}
				} else {//如果没有设置Column注解,则直接去属性名作为字段名
					fieldInfo.setDbFieldName(changeStrByStrategy(field.getName(),false,GenerateStrategyEnum.NORMAL.getCode()));
					fieldInfo.setFiledType(field.getType());
				}
				//更新到缓存中
				if(null ==isLineSplitCache.get(field.getName())){
					isLineSplitCache.put(field.getName(), isSplit);
				}
				if(null ==strategyCodeCache.get(field.getName())){
					strategyCodeCache.put(field.getName(), code);
				}

				if (field.isAnnotationPresent(PK.class)) {
					fieldInfo.setIsPk(true);
					fieldInfo.setIsNull(false);
				}
				if (field.isAnnotationPresent(NoInsert.class)) {
					fieldInfo.setIsInsert(false);
				}
				if (field.isAnnotationPresent(NoUpdate.class)) {
					fieldInfo.setIsUpdate(false);
				}
				if (field.isAnnotationPresent(NoCreate.class)) {
					fieldInfo.setIsCreate(false);
				}
				if (field.isAnnotationPresent(NoNULL.class)) {
					fieldInfo.setIsNull(false);
				}

				fieldInfo.setType(field.getType());

				resultList.add(fieldInfo);
			}
			cacheMap.put(pojoClass.getName(), resultList);
		} else {
			resultList = (List<FieldInfo>) cacheMap.get(pojoClass.getName());
		}

		return resultList;
	}

	/**
	 * 拼接select语句
	 * 
	 * @param pojoClass
	 * @param fieldInfoList
	 * @return
	 */
	private static String buildSelectSql(Class<?> pojoClass,
			List<FieldInfo> fieldInfoList) {
		if (selectSqlCache.get(pojoClass.getName()) != null) {
			return (String) selectSqlCache.get(pojoClass.getName());
		}
		return "select * from " + loadTableName(pojoClass);
	}


	/**
	 * 拼接create的SQL
	 * 
	 * @param pojoClass
	 * @param fieldInfoList
	 * @return
	 */
	private static String buildCreateSql(Class<?> pojoClass, List<FieldInfo> fieldInfoList) {

		//判断是否允许创建
		Table table = getTable(pojoClass);
		String result = null;
		if(table!=null){//创建视图
			if(table.type().equals(DBObjectEnum.VIEW)){
				if(table.joins().length==0 && table.leftJoins().length==0){
					return result;
				}

				//获取所有字段
				Map<String,Field> map = ReflectUtils.getFields4Map(pojoClass);
				
				//组装所有字段，组装条件
				StringBuffer temp = new StringBuffer();
				Map<String,String> conditionsＭap = new HashMap<String,String>();
				for (FieldInfo fieldInfo : fieldInfoList) {
					Column column = map.get(fieldInfo.getPojoFieldName()).getAnnotation(Column.class);
					if (fieldInfo.isCreate()) {
						String fieldName = fieldInfo.getDbFieldName();
						if(column!=null){
							if(column.from() == Object.class){//判断是否为该字段指定了所属表
								temp.append("`").append(fieldName).append("`,");
							}else{
								temp.append(loadTableName(column.from())).append(".`").append(fieldName).append("`,");
							}
						}else{
							temp.append("`").append(fieldName).append("`,");
						}
					}

					if(column!=null ){
						Class<?>[] clazzs = column.on();
						for(int i =0;i<clazzs.length;i++){//判断是否设定了连接条件
							for(int j =1;j<clazzs.length;j++){
								String tb1 = clazzs[i].getSimpleName();
								String tb2 = clazzs[j].getSimpleName();
								String v = loadTableName(clazzs[i])+"."+fieldInfo.getDbFieldName()+"="+loadTableName(clazzs[j])+"."+fieldInfo.getDbFieldName();
								conditionsＭap.put( tb1+"+"+ tb2, v);
								conditionsＭap.put( tb2+"+"+ tb1, v);
							}
						}
					}
				}
				if (temp.length()>0){
					temp.deleteCharAt(temp.length() - 1);
				}
				
				//组装所有表
				Class<?>[] clazzs = table.joins();
				StringBuffer temp2 = new StringBuffer();
				for(int i =1;i<clazzs.length;i++){
					
					String tb1 = clazzs[i].getSimpleName();
					String tb2 = clazzs[i-1].getSimpleName();
					temp2.append(" ").append(loadTableName(clazzs[i])).append(" join ").append(loadTableName(clazzs[i-1]));
					temp2.append(" on ").append(conditionsＭap.get(tb1+"+"+ tb2));
				}
				if (temp2.length()>0){
					temp2.deleteCharAt(temp.length() - 1);
				}
				
				//组装所有表
				Class<?>[] clazzs2 = table.leftJoins();
				StringBuffer temp3 = new StringBuffer();
				for(int i =1;i<clazzs2.length;i++){
					String tb1 = clazzs2[i].getSimpleName();
					String tb2 = clazzs2[i-1].getSimpleName();
					temp3.append(" ").append(loadTableName(clazzs2[i])).append(" left join ").append(loadTableName(clazzs2[i-1]));
					temp3.append(" on ").append(conditionsＭap.get(tb1+"+"+ tb2));
				}
				
				

				String viewName = table.value();
				result = "drop view if exists`"+viewName+"`;";
				
				StringBuffer resultSql = new StringBuffer();
				resultSql.append("create view `");
				resultSql.append(viewName);
				resultSql.append("` as select ");
				resultSql.append(temp);
				resultSql.append(" from ");
				resultSql.append(temp2);
				resultSql.append(" ");
				resultSql.append(temp3);
				resultSql.append(" ;");
		
				result += resultSql.toString();
			
			}else{ // 创建表结构
				
//				String tableName = loadTableName(pojoClass);
				String tableName = table.value();
				result = "drop table if exists`"+tableName+"`;";
		

				//获取所有字段
				Map<String,Field> map = ReflectUtils.getFields4Map(pojoClass);
				
				StringBuffer temp = new StringBuffer();
				String pkField = "";
				for (FieldInfo fieldInfo : fieldInfoList) {
					if (fieldInfo.isCreate()) {
						temp.append("`").append(fieldInfo.getDbFieldName()).append("` ");
						temp.append(fieldInfo.getFiledType());
						if(fieldInfo.getLength()>0){
							if(fieldInfo.getsLength()>=0){//如果是双长度，则
								temp.append("( ").append(fieldInfo.getLength()).append(",").append(fieldInfo.getsLength()).append(") ");
							}else{
								temp.append("( ").append(fieldInfo.getLength()).append(") ");
							}
						}
						Column column = map.get(fieldInfo.getPojoFieldName()).getAnnotation(Column.class);
						if(column!=null){
							if(!column.columnDefinition().equals("")){
								temp.append(column.columnDefinition());
							}
						}
						if(temp.toString().toLowerCase().indexOf(" default ")==-1){
							if(!fieldInfo.isNull()){
								temp.append(" not null ");
							}else{
								temp.append(" null ");
							}
						}
						
						if(!fieldInfo.getComment().equals("")){
							temp.append(" comment '").append(fieldInfo.getComment()).append("'");
						}
						temp.append(",");
					}
					if(fieldInfo.isPk()){
						pkField = fieldInfo.getDbFieldName();
					}
				}
				if(pkField==""){
					temp.deleteCharAt(temp.length() - 1);
				}else{
					temp.append("primary key (`").append(pkField).append("`)");
				}
		
				StringBuffer resultSql = new StringBuffer();
				resultSql.append("create table `");
				resultSql.append(tableName);
				resultSql.append("`(");
				resultSql.append(temp);
				resultSql.append(") engine=");
				resultSql.append(getTableEngine(pojoClass));
				resultSql.append(" DEFAULT CHARSET=utf8;");
		
				result += resultSql.toString();
			}
		}
		return result;
	}
	
	/**
	 * 拼接insert的SQL
	 * 
	 * @param pojoClass
	 * @param fieldInfoList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static String buildInsertSql(Class<?> pojoClass,
			List<FieldInfo> fieldInfoList) {
		
		//判断是否允许创建
		if(!enabledCreateTable(pojoClass)){
			return null;
		}
		
		String result = null;
		if (insertSqlCache.get(pojoClass.getName()) != null) {
			result = (String) insertSqlCache.get(pojoClass.getName());
			return result;
		}
		
		if(pojoClass.getAnnotation(NoInsert.class) != null){
			insertSqlCache.put(pojoClass.getName(), null);
			return null;//如果不允许插入，则返回null
		}
		
		String tableName = loadTableName(pojoClass);

		StringBuffer temp1 = new StringBuffer();
		StringBuffer temp2 = new StringBuffer();
		for (FieldInfo fieldInfo : fieldInfoList) {
			if (fieldInfo.isInsert()) {
				temp1.append(fieldInfo.getDbFieldName()).append(",");
				temp2.append(":").append(fieldInfo.getPojoFieldName())
						.append(",");
			}
		}
		temp1.deleteCharAt(temp1.length() - 1);
		temp2.deleteCharAt(temp2.length() - 1);

		StringBuffer resultSql = new StringBuffer();
		resultSql.append("insert into ");
		resultSql.append(tableName);
		resultSql.append("(");
		resultSql.append(temp1);
		resultSql.append(") values (");
		resultSql.append(temp2);
		resultSql.append(")");

		result = resultSql.toString();
		insertSqlCache.put(pojoClass.getName(), result);
		return result;
	}

	/**
	 * 生成根据主键生成删除的SQL
	 * 
	 * @param pojoClass
	 * @param fieldInfoList
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private static String buildSelectSqlByPK(Class<?> pojoClass,
			List<FieldInfo> fieldInfoList) {
		String result = null;
		if (selectSqlCache.get(pojoClass.getName() + "_pk") != null) {
			result = (String) selectSqlCache.get(pojoClass.getName());
			return result;
		}
		
		StringBuffer resultSql = new StringBuffer();
		resultSql.append(appendBaseSelectSQL(pojoClass));
		
		for (FieldInfo fieldInfo : fieldInfoList) {
			if (fieldInfo.isPk()) {
				String fieldName = changeStrByStrategy(fieldInfo.getDbFieldName(),(boolean)isLineSplitCache.get(fieldInfo.getPojoFieldName()),(String)strategyCodeCache.get(fieldInfo.getPojoFieldName()));
				resultSql.append(fieldName);
				resultSql.append("=:").append(fieldName).append(" and ");
			}
		}
		resultSql.delete(resultSql.length() - 4, resultSql.length());
		result = resultSql.toString();
		selectSqlCache.put(pojoClass.getName() + "_pk", result);

		return result;
	}
	
	/**
	 * 生成根据主键生成删除的SQL
	 * 
	 * @param pojoClass
	 * @param fieldInfoList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static String buildDeleteSqlByPK(Class<?> pojoClass,
			List<FieldInfo> fieldInfoList) {

		//判断是否允许创建
		if(!enabledCreateTable(pojoClass)){
			return null;
		}
		
		String result = null;
		if (deleteSqlCache.get(pojoClass.getName() + "_pk") != null) {
			result = (String) deleteSqlCache.get(pojoClass.getName());
			return result;
		}

		StringBuffer resultSql = new StringBuffer();
		resultSql.append(appendBaseDeleteSQL(pojoClass));

		for (FieldInfo fieldInfo : fieldInfoList) {
			if (fieldInfo.isPk()) {
				String fieldName = changeStrByStrategy(fieldInfo.getDbFieldName(),(boolean)isLineSplitCache.get(fieldInfo.getPojoFieldName()),(String)strategyCodeCache.get(fieldInfo.getPojoFieldName()));
				resultSql.append(fieldName);
				resultSql.append("=:").append(fieldName).append(" and ");
			}
		}
		resultSql.delete(resultSql.length() - 4, resultSql.length());
		result = resultSql.toString();
		deleteSqlCache.put(pojoClass.getName() + "_pk", result);

		return result;
	}

	/**
	 * 拼接根据主键来update的SQL
	 * 
	 * @param pojoClass
	 * @param fieldInfoList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static String buildUpdateSqlByPK(Class<?> pojoClass,
			List<FieldInfo> fieldInfoList) {
		
		//判断是否允许创建
		if(!enabledCreateTable(pojoClass)){
			return null;
		}
		
		String result = null;
		if (updateSqlCache.get(pojoClass.getName() + "_pk") != null) {
			result = (String) updateSqlCache.get(pojoClass.getName() + "_pk");
			return result;
		}
		
		if(pojoClass.getAnnotation(NoUpdate.class) != null){
			updateSqlCache.put(pojoClass.getName(), null);
			return null;//如果不允许插入，则返回null
		}

		StringBuffer resultSql = new StringBuffer();
		resultSql.append(appendBaseUpdateSQL(pojoClass, fieldInfoList));

		for (FieldInfo fieldInfo : fieldInfoList) {
			if (fieldInfo.isPk()) {
				String fieldName = changeStrByStrategy(fieldInfo.getDbFieldName(),(boolean)isLineSplitCache.get(fieldInfo.getPojoFieldName()),(String)strategyCodeCache.get(fieldInfo.getPojoFieldName()));
				resultSql.append(fieldName);
				resultSql.append("=:").append(fieldName).append(" and ");
			}
		}
		resultSql.delete(resultSql.length() - 4, resultSql.length());
		result = resultSql.toString();
		updateSqlCache.put(pojoClass.getName() + "_pk", result);

		return result;
	}

	/**
	 * 根据用户指定的更新条件(字段)来生成update的SQL
	 * 
	 * @param pojoClass
	 * @param fieldInfoList
	 * @param columns
	 * @return
	 */
	private static String buildUpdateSqlByColumns(Class<?> pojoClass,
			List<FieldInfo> fieldInfoList, String[] columns) {
		StringBuffer resultSql = new StringBuffer();
		if (updateSqlCache.get(pojoClass.getName() + "_columns") != null) {
			resultSql.append((String) updateSqlCache.get(pojoClass.getName()
					+ "_columns"));
		} else {
			resultSql.append(appendBaseUpdateSQL(pojoClass, fieldInfoList));
		}

		for (String column : columns) {
			for (FieldInfo fieldInfo : fieldInfoList) {
				if (column.equals(fieldInfo.getPojoFieldName())) {
					String fieldName = changeStrByStrategy(fieldInfo.getDbFieldName(),(boolean)isLineSplitCache.get(fieldInfo.getPojoFieldName()),(String)strategyCodeCache.get(fieldInfo.getPojoFieldName()));
					resultSql.append(fieldName);
					resultSql.append("=:").append(fieldName).append(" and ");
					break;
				}
			}
		}
		resultSql.delete(resultSql.length() - 4, resultSql.length());
		return resultSql.toString();
	}

	/**
	 * 拼接update语句的where之前的sql
	 * 
	 * @param pojoClass
	 * @param fieldInfoList
	 * @param resultSql
	 */
	@SuppressWarnings("unchecked")
	private static String appendBaseUpdateSQL(Class<?> pojoClass,
			List<FieldInfo> fieldInfoList) {
		String result = null;
		if (updateSqlCache.get(pojoClass.getName() + "_columns") != null) {
			result = (String) updateSqlCache.get(pojoClass.getName()
					+ "_columns");
		} else {
			StringBuffer resultSql = new StringBuffer();
			String tableName = loadTableName(pojoClass);

			resultSql.append("update ").append(tableName).append(" set ");
			for (FieldInfo fieldInfo : fieldInfoList) {
				if (fieldInfo.isUpdate() && !fieldInfo.isPk()) {
					String fieldName = changeStrByStrategy(fieldInfo.getDbFieldName(),(boolean)isLineSplitCache.get(fieldInfo.getPojoFieldName()),(String)strategyCodeCache.get(fieldInfo.getPojoFieldName()));
					resultSql.append(fieldName);
					resultSql.append("=:").append(fieldName).append(",");
				}
			}
			resultSql.deleteCharAt(resultSql.length() - 1);
			resultSql.append(" where ");

			result = resultSql.toString();
			updateSqlCache.put(pojoClass.getName() + "_columns", result);
		}
		return result;
	}

	/**
	 * 根据用户指定的更新条件(字段)来生成delete的SQL
	 * 
	 * @param pojoClass
	 * @param fieldInfoList
	 * @param columns
	 * @return
	 */
	private static String buildDeleteSqlByColumns(Class<?> pojoClass,
			List<FieldInfo> fieldInfoList, String[] columns) {
		StringBuffer resultSql = new StringBuffer();
		if (deleteSqlCache.get(pojoClass.getName() + "_columns") != null) {
			resultSql.append((String) deleteSqlCache.get(pojoClass.getName()
					+ "_columns"));
		} else {
			resultSql.append(appendBaseUpdateSQL(pojoClass, fieldInfoList));
		}

		for (String column : columns) {
			for (FieldInfo fieldInfo : fieldInfoList) {
				if (column.equals(fieldInfo.getPojoFieldName())) {
					String fieldName = changeStrByStrategy(fieldInfo.getDbFieldName(),(boolean)isLineSplitCache.get(fieldInfo.getPojoFieldName()),(String)strategyCodeCache.get(fieldInfo.getPojoFieldName()));
					resultSql.append(fieldName);
					resultSql.append("=:").append(fieldName).append(" and ");
					break;
				}
			}
		}
		resultSql.delete(resultSql.length() - 4, resultSql.length());
		return resultSql.toString();
	}

	/**
	 * 拼接delete语句的where之前的sql
	 * 
	 * @param pojoClass
	 * @param fieldInfoList
	 * @param resultSql
	 */
	@SuppressWarnings("unchecked")
	private static String appendBaseSelectSQL(Class<?> pojoClass) {
		if (selectSqlCache.get(pojoClass.getName() + "_columns") != null) {
			return (String) selectSqlCache
					.get(pojoClass.getName() + "_columns");
		} else {
			String result = "select * from " + loadTableName(pojoClass)
					+ " where ";
			selectSqlCache.put(pojoClass.getName() + "_columns", result);
			return result;
		}
	}
	
	/**
	 * 拼接delete语句的where之前的sql
	 * 
	 * @param pojoClass
	 * @param fieldInfoList
	 * @param resultSql
	 */
	@SuppressWarnings("unchecked")
	private static String appendBaseDeleteSQL(Class<?> pojoClass) {
		if (deleteSqlCache.get(pojoClass.getName() + "_columns") != null) {
			return (String) deleteSqlCache
					.get(pojoClass.getName() + "_columns");
		} else {
			String result = "delete from " + loadTableName(pojoClass)
					+ " where ";
			deleteSqlCache.put(pojoClass.getName() + "_columns", result);
			return result;
		}
	}

	/**
	 * 通过类获取表名
	 * 
	 * @param pojoClass
	 * @return
	 */
	private static String loadTableName(Class<?> pojoClass) {
		if (pojoClass.isAnnotationPresent(Table.class)) {
			Table table = (Table) pojoClass.getAnnotation(Table.class);
			return table.value();
		} else {
			return changeStrByStrategy(pojoClass.getSimpleName(),false,GenerateStrategyEnum.NORMAL.getCode());
		}
	}

	/**
	 * 通过类获取表名
	 * 
	 * @param pojoClass
	 * @return
	 */
	public static Table getTable(Class<?> pojoClass) {
		if (pojoClass.isAnnotationPresent(Table.class)) {
			Table table = (Table) pojoClass.getAnnotation(Table.class);
			return table;
		}
		return null;
	}
	
	/**
	 * 是否允许创建表
	 * 
	 * @param pojoClass
	 * @return
	 */
	public static boolean enabledCreateTable(Class<?> pojoClass) {
		Table table = getTable(pojoClass);
		if(table!=null){
			if(table.type().equals(DBObjectEnum.VIEW)){
				logger.info("{}类型为视图，不允许创建");
				return false;//如果不允许创建，则返回null
			}else if(pojoClass.getAnnotation(NoCreate.class) !=null){
				logger.info("{}类型不允许创建，它可能是一个视图");
				return false;//如果不允许创建，则返回null
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 通过类获取表
	 * 
	 * @param pojoClass
	 * @return
	 */
	private static String getTableEngine(Class<?> pojoClass) {
		Table table =getTable(pojoClass);
		if (table!=null) {
			return table.engine().getName();
		}else{
			return DBEngineEnum.INNODB.getName();
		}
	}

	/**
	 * 将大写字母转换成下划线加小写字母 例:userName--> user_name
	 * 
	 * @param str
	 * @param isLineSplit
	 * @param strategyCode
	 * @return
	 */
	public static String changeStrByStrategy(String str,boolean isLineSplit,String strategyCode) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		
		if(isLineSplit){//是否添加下划线
			StringBuilder sb = new StringBuilder(str);
			char c;
			int count = 0;
			for (int i = 0; i < str.length(); i++) {
				c = str.charAt(i);
				if (c >= 'A' && c <= 'Z') {
					//sb.replace(i + count, i + count + 1, (c + "").toLowerCase());
					sb.insert(i + count, "_");
					count++;
				}
			}
			str= sb.toString();
		}
		
		if (GenerateStrategyEnum.AU.getCode().equals(strategyCode)) {//全部转化为大写
            str = str.toUpperCase();  
        } else if (GenerateStrategyEnum.AL.getCode().equals(strategyCode)) {  //全部转化为小写
            str = str.toLowerCase();  
        } else if (GenerateStrategyEnum.FU.getCode().equals(strategyCode)) {  //首字母大写
            StringBuffer buf = new StringBuffer(str.substring(str.lastIndexOf(".")+1));  
            String upper =buf.substring(0,1).toUpperCase();  
            buf.delete(0, 1).insert(0, upper);  
            str = buf.toString();  
        } else if (GenerateStrategyEnum.FL.getCode().equals(strategyCode)) {  //首字母小写
            StringBuffer buf = new StringBuffer(str.substring(str.lastIndexOf(".")+1));  
            String lower =buf.substring(0,1).toLowerCase();  
            buf.delete(0, 1).insert(0, lower);  
            str = buf.toString();  
        }  else{  
              
        }         
        return str;
	}
	
}
