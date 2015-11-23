package com.tgb.ccl.simplespring.servicesupport;

import java.util.LinkedHashMap;
import java.util.List;

import com.tgb.ccl.simplespring.basic.Entity;
import com.tgb.ccl.simplespring.basic.define.Condition;
import com.tgb.ccl.simplespring.basic.define.ConditionDef;
import com.tgb.ccl.simplespring.basic.enums.ConditionLinkEnum;
import com.tgb.ccl.simplespring.exception.ServiceException;
import com.tgb.ccl.simplespring.support.PageModel;

public interface IBaseService<T extends Entity> {
	
	/**
	 * 初始化主键的条件定义
	 * @return
	 */
	ConditionDef initPKConditionDef();
	
	/**
	 * 判断是否存在
	 * @param map
	 * @return
	 */
	boolean existById(Object id);
	
	/**
	 * 判断是否存在
	 * @param map
	 * @return
	 */
	boolean exist(LinkedHashMap< String,Object > map);
	
	/**
	 * 判断是否存在
	 * @param conditionDef
	 * @param map
	 * @return
	 */
	boolean exist(ConditionDef conditionDef,LinkedHashMap< String,Object > map);
	
	/**
	 * 添加信息
	 * 
	 * @param t
	 *            信息
	 * @throws ServiceException
	 */
	boolean add(T t) throws ServiceException;

	/**
	 * 根据信息id，更新信息
	 * 
	 * @param t
	 *            信息
	 * @throws ServiceException
	 */
	boolean update(T t) throws ServiceException;
	
	/**
	 * 删除信息
	 * 
	 * @param t
	 *            信息
	 * @throws ServiceException
	 */
	boolean del(Object id) throws ServiceException;
	
	/**
	 * 删除信息
	 * 
	 * @param t
	 *            信息
	 * @throws ServiceException
	 */
	boolean del(T t) throws ServiceException;

	/**
	 * 根据id，获取实体对象
	 * 
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	T getEntityById(Object id) throws ServiceException ;
	
	/**
	 * 根据map，获取唯一对象，如果map中有多个条件，则条件之间用and相连
	 * 
	 * @param paramMap 查询条件数据
	 * @return 如果查得结果不唯一，则报错
	 */
	T getEntity(LinkedHashMap<String,Object> paramMap) throws ServiceException ;
	
	/**
	 * 根据map，获取唯一对象，如果map中有多个条件，则条件之间用指定的操作符相连
	 * 
	 * @param paramMap  查询条件数据
	 * @param conditionLinkEnum 关联关系 AND，OR 
	 * @return 如果查得结果不唯一，则报错
	 * @throws ServiceException
	 */
	T getEntity(LinkedHashMap<String,Object> paramMap,ConditionLinkEnum conditionLinkEnum) throws ServiceException ;
	
	/**
	 * 根据map，获取唯一对象，如果map中有多个条件，则条件之间用指定的操作符相连
	 * 
	 * @param paramMap  查询条件数据
	 * @param relateOperate 关联关系 AND，OR
	 * @return 如果查得结果不唯一，则报错
	 * @throws ServiceException
	 */
	T getEntity(LinkedHashMap<String,Object> paramMap,String relateOperate) throws ServiceException ;
	
	/**
	 * 根据条件定义，获取实体对象，条件之间是and关系
	 * 
	 * @param conditionDef 条件定义（根据那些字段查询，关系是=<> in 等）
	 * @param paramMap 查询条件数据
	 * @return 如果查得结果不唯一，则报错
	 * @throws ServiceException
	 */
	T getEntity(ConditionDef conditionDef,LinkedHashMap<String,Object> paramMap) throws ServiceException ;
	
	/**
	 * 根据条件，获取实体对象
	 * 
	 * @param condition   查询条件
	 * @param paramMap 查询条件数据
	 * @return 如果查得结果不唯一，则报错
	 * @throws ServiceException
	 */
	T getEntity(Condition condition,LinkedHashMap<String, Object> paramMap) throws ServiceException;
	
	
	/**
	 * 根据map，获取实体list，如果map中有多个条件，则条件之间用and相连
	 * 
	 * @param paramMap 查询条件数据
	 * @return 
	 */
	List<T> findList(LinkedHashMap< String,Object > paramMap);
	
	/**
	 * 根据map，获取实体list，如果map中有多个条件，则条件之间用指定的操作符相连
	 * 
	 * @param paramMap  查询条件数据
	 * @param ConditionLinkEnum 关联关系 AND，OR 
	 * @return 
	 */
	List<T> findList(LinkedHashMap< String,Object > paramMap,ConditionLinkEnum conditionLinkEnum);
	
	/**
	 * 根据map，获取实体list，如果map中有多个条件，则条件之间用指定的操作符相连
	 * 
	 * @param paramMap  查询条件数据
	 * @param relateOperate 关联关系 AND，OR
	 * @return 
	 */
	List<T> findList(LinkedHashMap< String,Object > paramMap,String relateOperate);
	
	/**
	 * 根据条件定义，获取实体list，条件之间是and关系
	 * 
	 * @param conditionDef 条件定义（根据那些字段查询，关系是=<> in 等）
	 * @param paramMap 查询条件数据
	 * @return 
	 */
	List<T> findList(ConditionDef conditionDef,LinkedHashMap< String,Object > paramMap);
	
	/**
	 * 根据条件，获取实体list
	 * 
	 * @param condition   查询条件
	 * @param paramMap 查询条件数据
	 * @return 
	 */
	List<T> findList(Condition condition,LinkedHashMap< String,Object > paramMap);
	
	/**
	 * 根据map和分页参数，获取分页信息，如果map中有多个条件，则条件之间用and相连
	 * 
	 * @param paramMap 查询条件数据
	 * @param pageModel 分页参数
	 * @return
	 */
	PageModel<T> queryPageModel(LinkedHashMap<String,Object> paramMap,PageModel<T> pageModel);
	
	/**
	 * 根据map和分页参数，获取分页信息，如果map中有多个条件，则条件之间用指定的操作符相连
	 * 
	 * @param paramMap 查询条件数据
	 * @param relateOperate 关联关系 AND，OR ，L（like）
	 * @param pageModel 分页参数
	 * @return
	 */
	PageModel<T> queryPageModel(LinkedHashMap<String,Object> paramMap,String relateOperate,PageModel<T> pageModel);
	
	/**
	 * 根据条件定义和分页参数，获取分页信息，条件之间是and关系
	 * 
	 * @param conditionDef   查询条件定义
	 * @param paramMap 查询条件数据
	 * @param pageModel 分页参数
	 * @return
	 */
	PageModel<T> queryPageModel(ConditionDef conditionDef,LinkedHashMap<String,Object> paramMap, 
			LinkedHashMap<String, String> orderByFields,PageModel<T> pageModel);
	
	/**
	 * 根据条件和分页参数，获取分页信息
	 * 
	 * @param condition   查询条件
	 * @param paramMap 查询条件数据
	 * @param pageModel 分页参数
	 * @return
	 */
	PageModel<T> queryPageModel(Condition condition,LinkedHashMap<String,Object> paramMap,PageModel<T> pageModel);
	
	/**
	 * 执行sql
	 * 
	 * @param sql
	 * @return
	 */
	boolean executeSql(String sql);
}