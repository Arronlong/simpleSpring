package com.tgb.ccl.simplespring.servicesupport.impl;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tgb.ccl.simplespring.basic.Entity;
import com.tgb.ccl.simplespring.basic.define.Condition;
import com.tgb.ccl.simplespring.basic.define.ConditionDef;
import com.tgb.ccl.simplespring.basic.enums.ConditionLinkEnum;
import com.tgb.ccl.simplespring.daosupport.BaseDao;
import com.tgb.ccl.simplespring.exception.ServiceException;
import com.tgb.ccl.simplespring.servicesupport.IBaseService;
import com.tgb.ccl.simplespring.support.PageModel;
import com.tgb.ccl.simplespring.util.PagingUtils;

public class BaseServiceImpl<T extends Entity> implements IBaseService<T> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 获取当前的操作的实体对象类
	 */
	@SuppressWarnings("unchecked")
	protected Class<T> clazz = (Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

	protected ConditionDef PK_conditionDef;

	@Resource
	protected BaseDao<T> dao;
	
	public BaseServiceImpl(){
		initPKConditionDef();
	}
	
	/**
	 * 获取实体对象
	 * @return
	 */
	private T getT(){
		try {
			T entity= clazz.newInstance();
			return entity;
		} catch (Exception e) {
			logger.warn("实例化【{}】实体对象时，发生错误：{}",clazz.getName(),e);
		}
		return null;
	}
	
	/**
	 * 初始化主键条件定义
	 */
	@Override
	public ConditionDef initPKConditionDef() {
		PK_conditionDef = new ConditionDef(new Object[][] { { "id = :id" }});
		return PK_conditionDef;
	}

	@Override
	public boolean existById(Object id) {
		LinkedHashMap<String, Object> map =new LinkedHashMap<String, Object>();
		map.put("id", id);
		return exist(PK_conditionDef, map);
	}
	
	@Override
	public boolean exist(LinkedHashMap<String, Object> map) {
		return dao.exist(clazz, ConditionDef.buildConditionDef(map), map);
	}
	
	@Override
	public boolean exist(ConditionDef conditionDef, LinkedHashMap<String, Object> map) {
		return dao.exist(clazz,  conditionDef, map);
	}

	@Override
	public boolean add(T t) throws ServiceException {
		return dao.baseSave(t);
	}

	@Override
	public boolean update(T t) throws ServiceException {
		return dao.baseUpdate(t);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean del(Object id) throws ServiceException {
		return dao.baseDelete((T)getT().setId(id));
	}
	
	@Override
	public 	boolean del(T t) throws ServiceException {
		return dao.baseDelete(t);
	}

	@Override
	public T getEntityById(Object id) throws ServiceException {
		LinkedHashMap<String, Object> paramMap = new LinkedHashMap<String, Object>();
		paramMap.put("id", id);
		try {
			return dao.baseQueryForEntity(clazz, PK_conditionDef, paramMap);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public T getEntity(LinkedHashMap<String, Object> paramMap) throws ServiceException {
		try {
			return dao.baseQueryForEntity(clazz, ConditionDef.buildConditionDef(paramMap), paramMap);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	@Override
	public T getEntity(LinkedHashMap<String,Object> paramMap,ConditionLinkEnum conditionLinkEnum) throws ServiceException {
		return getEntity(paramMap, conditionLinkEnum.getCode());
	}
	
	@Override
	public T getEntity(LinkedHashMap<String,Object> paramMap,String relateOperate) throws ServiceException {
		try {
			return dao.baseQueryForEntity(clazz, Condition.buildCondition(paramMap, relateOperate), paramMap);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	@Override
	public T getEntity(Condition condition,LinkedHashMap<String, Object> paramMap) throws ServiceException {
		try {
			return	dao.baseQueryForEntity(clazz, condition, paramMap);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	
	@Override
	public T getEntity(ConditionDef conditionDef, LinkedHashMap<String, Object> paramMap) throws ServiceException {
		try {
			return	dao.baseQueryForEntity(clazz, conditionDef, paramMap);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public List<T> findList(LinkedHashMap<String, Object> paramMap) {
		return dao.baseQueryForList(clazz, ConditionDef.buildConditionDef(paramMap), paramMap);
	}
	
	@Override
	public List<T> findList(LinkedHashMap<String, Object> paramMap,ConditionLinkEnum conditionLinkEnum) {
		return findList(paramMap, conditionLinkEnum.getCode());
	}
	
	@Override
	public List<T> findList(LinkedHashMap<String, Object> paramMap,String relateOperate) {
		return dao.baseQueryForList(clazz, Condition.buildCondition(paramMap,relateOperate), paramMap);
	}

	@Override
	public List<T> findList(ConditionDef conditionDef, LinkedHashMap<String, Object> paramMap) {
		return dao.baseQueryForList(clazz, conditionDef, paramMap);
	}
	
	@Override
	public List<T> findList(Condition condition, LinkedHashMap<String, Object> paramMap) {
		return dao.baseQueryForList(clazz, condition, paramMap);
	}

	@Override
	public boolean executeSql(String sql) {
		return dao.baseExecuteSql(sql);
	}

	@Override
	public PageModel<T> queryPageModel(LinkedHashMap<String, Object> paramMap,
			PageModel<T> pageModel) {
		Condition condition = Condition.buildCondition(paramMap);
		if(!paramMap.containsKey(PagingUtils.PAGE_NUM)){
			paramMap.put(PagingUtils.PAGE_NUM,pageModel.getPageNum());
		}
		if(!paramMap.containsKey(PagingUtils.PAGE_SIZE)){
			paramMap.put(PagingUtils.PAGE_SIZE,pageModel.getNumPerPage());
		}
		return dao.baseQueryForPageModel(clazz,condition, paramMap);
	}

	@Override
	public PageModel<T> queryPageModel(LinkedHashMap<String, Object> paramMap,
			String relateOperate, PageModel<T> pageModel) {
		if(!paramMap.containsKey(PagingUtils.PAGE_NUM)){
			paramMap.put(PagingUtils.PAGE_NUM,pageModel.getPageNum());
		}
		if(!paramMap.containsKey(PagingUtils.PAGE_SIZE)){
			paramMap.put(PagingUtils.PAGE_SIZE,pageModel.getNumPerPage());
		}
		return dao.baseQueryForPageModel(clazz, Condition.buildCondition(paramMap,relateOperate), paramMap);
	}

	@Override
	public PageModel<T> queryPageModel(ConditionDef conditionDef,
			LinkedHashMap<String, Object> paramMap,
			LinkedHashMap<String, String> orderByFields, PageModel<T> pageModel) {
		if(!paramMap.containsKey(PagingUtils.PAGE_NUM)){
			paramMap.put(PagingUtils.PAGE_NUM,pageModel.getPageNum());
		}
		if(!paramMap.containsKey(PagingUtils.PAGE_SIZE)){
			paramMap.put(PagingUtils.PAGE_SIZE,pageModel.getNumPerPage());
		}
		return dao.baseQueryForPageModel(clazz, conditionDef, paramMap);
	}

	@Override
	public PageModel<T> queryPageModel(Condition condition,
			LinkedHashMap<String, Object> paramMap,
			PageModel<T> pageModel) {
		if(!paramMap.containsKey(PagingUtils.PAGE_NUM)){
			paramMap.put(PagingUtils.PAGE_NUM,pageModel.getPageNum());
		}
		if(!paramMap.containsKey(PagingUtils.PAGE_SIZE)){
			paramMap.put(PagingUtils.PAGE_SIZE,pageModel.getNumPerPage());
		}
		return dao.baseQueryForPageModel(clazz, condition, paramMap);
	}
	
}