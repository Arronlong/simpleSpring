package com.tgb.ccl.simplespring.daosupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.tgb.ccl.simplespring.basic.Entity;
import com.tgb.ccl.simplespring.basic.define.Condition;
import com.tgb.ccl.simplespring.basic.define.ConditionDef;
import com.tgb.ccl.simplespring.exception.ServiceException;
import com.tgb.ccl.simplespring.support.PageModel;
import com.tgb.ccl.simplespring.util.PagingUtils;
import com.tgb.ccl.simplespring.util.ReflectUtils;
import com.tgb.ccl.simplespring.util.SQLUtils;

@Repository("dao")
public class BaseDao<T extends Entity> {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	@Resource
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	/**
	 * 判断是否存在
	 * @param cls 实体类class
	 * @param conditionDef 条件定义
	 * @param paramMap  参数map
	 * @return
	 */
	public boolean exist(Class<T> cls,ConditionDef conditionDef,LinkedHashMap<String, Object> paramMap) {
		List<T> list = baseQueryForList(cls, conditionDef, paramMap);
		if (list==null || list.size()==0) {
			return false;
		}
		return true;
	}
	
	/**
	 * 通过原生sql，判断是否存在
	 * @param cls  实体类class
	 * @param sql  原生sql
	 * @return
	 */
	public boolean existBySql(Class<T> cls,String sql) {
		List<T> list = baseQueryForListBySql(cls, sql, null);
		if (list==null || list.size()==0) {
			return false;
		}
		return true;
	}

	/**
	 * 保存新增的实体对象
	 * 
	 * @param bean 实体对象
	 * @return
	 */
	public boolean baseSave(T bean) {
		String sql = SQLUtils.buildInsertSql(bean.getClass());
		return baseSaveBySql(bean,sql);
	}

	/**
	 * 保存新增的实体对象
	 * 
	 * @param bean 操作的对象
	 * @param sql 原生sql
	 * @return
	 */
	public boolean baseSaveBySql(T bean,String sql) {
		SqlParameterSource sps = new BeanPropertySqlParameterSource(bean);
		return this.namedParameterJdbcTemplate.update(sql, sps) > 0 ? true : false;
	}

	/**
	 * 根据主键保存修改的实体对象
	 * 
	 * @param bean 实体对象
	 * @return
	 */
	public boolean baseUpdate(T bean) {
		String sql = SQLUtils.buildUpdateSql(bean.getClass());
		return baseUpdateBySql(bean,sql);
	}
	
	/**
	 * 根据主键保存修改的实体对象
	 * 
	 * @param bean 实体对象
	 * @param sql  原生sql
	 * @return
	 */
	public boolean baseUpdateBySql(T bean,String sql) {
		SqlParameterSource sps = new BeanPropertySqlParameterSource(bean);
		return this.namedParameterJdbcTemplate.update(sql, sps) > 0 ? true : false;
	}

	/**
	 * 根据bean的部分字段的条件来更新bean的信息
	 * 
	 * @param bean 实体对象
	 * @param fileds 条件字段
	 * @return
	 * @throws ServiceException
	 */
	public boolean baseUpdateWithColumn(T bean, String[] fileds)
			throws ServiceException {
		String sql = SQLUtils.buildUpdateSqlByColumns(bean.getClass(), fileds);
		return baseUpdateBySql(bean, sql);
	}
	
	/**
	 * 根据bean的pk来删除bean
	 * 
	 * @param bean  实体对象
	 * @return
	 */
	public boolean baseDelete(T bean) {
		String sql = SQLUtils.buildDeleteSql(bean.getClass());
		return baseDeleteBySql(bean, sql);
	}
	
	/**
	 * 根据bean的pk来删除bean
	 * 
	 * @param bean  实体对象
	 * @param sql  原生sql
	 * @return
	 */
	public boolean baseDeleteBySql(T bean,String sql) {
		SqlParameterSource sps = new BeanPropertySqlParameterSource(bean);
		return this.namedParameterJdbcTemplate.update(sql, sps) > 0 ? true : false;
	}

	/**
	 * 根据bean的部分字段的条件来删除bean
	 * 
	 * @param bean 实体对象
	 * @param fileds 条件字段
	 * @return
	 * @throws Exception
	 */
	public boolean baseDeleteWithColumn(T bean, String[] fileds)
			throws Exception {
		String sql = SQLUtils.buildDeleteSqlByColumns(bean.getClass(), fileds);
		return baseDeleteBySql(bean, sql);
	}

	/**
	 * 自动分页/不分页查询返回list
	 * 
	 * @param cls  实体类class
	 * @param sql	原生sql
	 * @param paramMap 参数
	 * @return
	 */
	public List<T> baseQueryForListBySql(Class<T> cls, String sql,LinkedHashMap<String, Object> paramMap){
		List<T> listResult = new ArrayList<T>();
		List<Map<String, Object>> list = this.namedParameterJdbcTemplate.queryForList(sql, paramMap);
		if (null == list || list.size() == 0) {
			return null;
		} else {
			for (int i =0 ;i<list.size();i++){
				try {
					listResult.add(ReflectUtils.convertMap2Bean(list.get(i), cls));
				} catch (Exception e) {
					logger.warn("转换对象时出错，方法：baseQueryForListBySql，具体信息如下：{}",e);
				}
			}
		}
		return listResult;
	}

	/**
	 * 自动分页/不分页查询返回list
	 * 
	 * @param cls  实体类class
	 * @param conditionDef 条件定义 
	 * @param paramMap    参数
	 * @return
	 */
	public List<T> baseQueryForList(Class<T> cls, ConditionDef conditionDef, LinkedHashMap<String, Object> paramMap) {
		Condition condition = new Condition(conditionDef, paramMap);
		return baseQueryForList(cls, condition, paramMap);
	}
	
	/**
	 * 自动分页/不分页查询返回list
	 * 
	 * @param cls  实体类class
	 * @param PK_conditionDef 条件定义 
	 * @param paramMap    参数
	 * @return
	 */
	public List<T> baseQueryForList(Class<T> cls, Condition condition, LinkedHashMap<String, Object> paramMap) {
		String limitSql = "";
		if(PagingUtils.isPagingSearchRequest(paramMap)) {
			int pageNum=1;
			int pageSize = 10;
			if(paramMap.containsKey(PagingUtils.PAGE_NUM)){
				pageNum = paramMap.get(PagingUtils.PAGE_NUM)==null ? 1 : (int)paramMap.get(PagingUtils.PAGE_NUM);
				paramMap.remove(PagingUtils.PAGE_NUM);
			}
			if(paramMap.containsKey(PagingUtils.PAGE_SIZE)){
				pageSize = paramMap.get(PagingUtils.PAGE_SIZE)==null ? 1 : (int)paramMap.get(PagingUtils.PAGE_SIZE);
				paramMap.remove(PagingUtils.PAGE_SIZE);
			}
			int first = (pageNum - 1) * pageSize;
			if(first<0){
				first = 0;
			}
			limitSql=" limit "+first+" , "+pageSize;
			paramMap.remove(PagingUtils.PAGE_SEARCH);
		}
		String sql = SQLUtils.buildSelectSql(cls) + condition.getConditionClauseWithWhere() + condition.getOrderStr() + limitSql;
		return baseQueryForListBySql(cls, sql, paramMap);
	}
	

	/**
	 * 查询满足条件的单条记录的实体对象，如果超过1条则抛出异常，没查询到则返回null
	 * 
	 * @param cls  实体class
	 * @param sql  原生sql
	 * @param paramMap  参数
	 * @return
	 * @throws Exception
	 */
	public T baseQueryForEntityBySql(Class<T> cls, String sql, LinkedHashMap<String,Object> paramMap) throws Exception {
		List<Map<String, Object>> list = this.namedParameterJdbcTemplate.queryForList(sql, paramMap);

		if (null == list || list.size() == 0) {
			return null;
		} else if (list.size() > 1) {
			throw new ServiceException("query return record more then one!");
		} else {
			return ReflectUtils.convertMap2Bean(list.get(0), cls);
		}
	}
	/**
	 * 查询满足条件的单条记录的实体对象，如果超过1条则抛出异常，没查询到则返回null
	 * 
	 * @param cls  实体class
	 * @param conditionDef  条件定义
	 * @param paramMap  参数
	 * @return
	 * @throws Exception
	 */
	public T baseQueryForEntityById(Class<T> cls, LinkedHashMap<String,Object> paramMap) throws Exception {
		String sql = SQLUtils.buildSelectSql(cls);
		return baseQueryForEntityBySql(cls, sql, paramMap);
	}
	/**
	 * 查询满足条件的单条记录的实体对象，如果超过1条则抛出异常，没查询到则返回null
	 * 
	 * @param cls  实体class
	 * @param conditionDef  条件定义
	 * @param paramMap  参数
	 * @return
	 * @throws Exception
	 */
	public T baseQueryForEntity(Class<T> cls, ConditionDef conditionDef, LinkedHashMap<String,Object> paramMap) throws Exception {
		Condition condition = new Condition(conditionDef, paramMap);
		return baseQueryForEntity(cls, condition, paramMap);
	}
	
	/**
	 * 查询满足条件的单条记录的实体对象，如果超过1条则抛出异常，没查询到则返回null
	 * 
	 * @param cls  实体class
	 * @param PK_conditionDef  条件定义
	 * @param paramMap  参数
	 * @return
	 * @throws Exception
	 */
	public T baseQueryForEntity(Class<T> cls, Condition condition, LinkedHashMap<String,Object> paramMap) throws Exception {
		String sql = SQLUtils.buildSelectSql(cls) + condition.getConditionClauseWithWhere() + condition.getOrderStr();
		return baseQueryForEntityBySql(cls, sql, paramMap);
	}
	
	/**
	 * 自动分页查询返回分页信息
	 * 
	 * @param cls  实体类class
	 * @param PK_conditionDef 条件定义 
	 * @param paramMap    参数
	 * @return
	 */
	public PageModel<T> baseQueryForPageModel(Class<T> cls, Condition condition, LinkedHashMap<String, Object> paramMap) {
		PageModel<T> pageModel = new PageModel<T>();
		int pageNum=1;
		int pageSize = 10;
		if(paramMap.containsKey(PagingUtils.PAGE_NUM)){
			pageNum = paramMap.get(PagingUtils.PAGE_NUM)==null ? 1 : (int)paramMap.get(PagingUtils.PAGE_NUM);
		}
		if(paramMap.containsKey(PagingUtils.PAGE_SIZE)){
			pageSize = paramMap.get(PagingUtils.PAGE_SIZE)==null ? 1 : (int)paramMap.get(PagingUtils.PAGE_SIZE);
		}
		
		//设置页码和页容量
		pageModel.setPageNum(pageNum);
		pageModel.setNumPerPage(pageSize);
		
		//获取结果list，并设定到pageModel中
		List<T> list = baseQueryForList(cls, condition, paramMap);
		if(list == null){
			list = new ArrayList<T>();
		}
		pageModel.setList(list);
		
		//获取总记录数，并设定到pageModel中
		String sql = "select count(*) from "+SQLUtils.getTable(cls).value() +" " + condition.getConditionClauseWithWhere();
		Long totalRecords = this.namedParameterJdbcTemplate.queryForObject(sql, paramMap, Long.class);
		pageModel.setTotalRecords(totalRecords);
		
		return pageModel;
	}
	

	/**
	 * 根据条件定义，查询分页信息
	 * 
	 * @param cls  实体类class
	 * @param conditionDef 条件定义 
	 * @param paramMap    参数
	 * @return
	 */
	public PageModel<T> baseQueryForPageModel(Class<T> cls, ConditionDef conditionDef, LinkedHashMap<String, Object> paramMap) {
		Condition condition = new Condition(conditionDef, paramMap);
		return baseQueryForPageModel(cls, condition, paramMap);
	}


	/** 
	 * 根据原生sql，查询分页信息
	 * 
	 * @param cls  实体类class
	 * @param sql	原生sql
	 * @param paramMap 参数
	 * @return
	 */
	public PageModel<T> baseQueryForPageModelBySql(Class<T> cls, String sql,LinkedHashMap<String, Object> paramMap){
		PageModel<T> pageModel = new PageModel<T>();
		int pageNum=1;
		int pageSize = 10;
		if(paramMap.containsKey(PagingUtils.PAGE_NUM)){
			pageNum = paramMap.get(PagingUtils.PAGE_NUM)==null ? 1 : (int)paramMap.get(PagingUtils.PAGE_NUM);
			paramMap.remove(PagingUtils.PAGE_NUM);
		}
		if(paramMap.containsKey(PagingUtils.PAGE_SIZE)){
			pageSize = paramMap.get(PagingUtils.PAGE_SIZE)==null ? 1 : (int)paramMap.get(PagingUtils.PAGE_SIZE);
			paramMap.remove(PagingUtils.PAGE_SIZE);
		}
		paramMap.remove(PagingUtils.PAGE_SEARCH);
		
		//设置页码和页容量
		pageModel.setPageNum(pageNum);
		pageModel.setNumPerPage(pageSize);
		
		//获取结果list，并设定到pageModel中
		List<T> list = baseQueryForListBySql(cls, sql, paramMap);
		if(list == null){
			list = new ArrayList<T>();
		}
		pageModel.setList(list);
		
		//获取总记录数，并设定到pageModel中
		String countSql ="select count(*)"+sql.substring(sql.indexOf(" from ")+" from ".length()+1);
		Long totalRecords = this.namedParameterJdbcTemplate.queryForObject(countSql, paramMap, Long.class);
		pageModel.setTotalRecords(totalRecords);
		
		return pageModel;
	 }

	
	/**
	 * 执行sql命令
	 * @param sql
	 * @return
	 */
	public boolean baseExecuteSql(String sql){
		boolean result = true;
		try{
			jdbcTemplate.execute(sql);
		}catch(Exception e){
			result = false;
			logger.warn("执行sql时发生错误：{}",e);			
		}
		return result; 
	}
	
}
