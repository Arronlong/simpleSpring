package com.tgb.ccl.simplespring.demo.test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tgb.ccl.simplespring.basic.define.Condition;
import com.tgb.ccl.simplespring.basic.define.ConditionDef;
import com.tgb.ccl.simplespring.basic.enums.ConditionLinkEnum;
import com.tgb.ccl.simplespring.basic.map.ConditionLinkMap;
import com.tgb.ccl.simplespring.basic.map.PageQueryMap;
import com.tgb.ccl.simplespring.demo.entity.WorkLog;
import com.tgb.ccl.simplespring.demo.service.IWorkLogService;
import com.tgb.ccl.simplespring.demo.service.impl.WorkLogServiceImpl;
import com.tgb.ccl.simplespring.exception.ServiceException;
import com.tgb.ccl.simplespring.support.PageModel;
import com.tgb.ccl.simplespring.util.JSONUtils;

public class WorkLogTest {
	
	private  IWorkLogService service ;
	private ApplicationContext context ; 
	
	
	@Before
	public void init(){
		context = new ClassPathXmlApplicationContext("config/spring-basic.xml");
		if(context!=null){
//			service=(IWorkLogService) context.getBean("workLogService");
			service=(IWorkLogService) context.getBean(WorkLogServiceImpl.class);
		}
	}

	
	@Test
	public void testQueryById() {
		WorkLog workLog = null;
		try {
			// 通过统一的id查找单一对象
			workLog = service.getEntityById("537a5e38-b875-4e99-8119-e49d6f84a4e4");
			System.out.println("通过统一的id查找单一对象——"+workLog.getContent());
		} catch (ServiceException e) {
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void testQueryMap() {
		WorkLog workLog = null;
		try {

			// 根据map单条件查找单一对象
			LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("projectId", "basp1");
			workLog = service.getEntity(map);
			System.out.println("根据map单条件查找单一对象——"+workLog.getContent());

			// 根据map多条件查找单一对象
			LinkedHashMap<String, Object> map1 = new LinkedHashMap<String, Object>();
			map1.putAll(map);
			map1.put("content", "更新测试");
			workLog = service.getEntity(map1);
			System.out.println("根据map多条件查找单一对象——"+workLog.getContent());
			
			
			//根据ConditionDef 查找单一对象
			ConditionDef def = new ConditionDef(new Object[][] { { "id = :id" } , { "content = :content"}});
			
			LinkedHashMap<String, Object> map2 = new LinkedHashMap<String, Object>();
			map2.put("content", "更新测试1");
			map2.put("id", "700ffbaa-747d-4256-a9a5-b6831f36bb7f");
			workLog = service.getEntity(def,map2);
			System.out.println("根据ConditionDef查找单一对象——"+workLog.getContent());
			
			//根据Condition查找单一对象
			Condition condition = new Condition(def,map2,ConditionLinkEnum.OR);
			workLog = service.getEntity(condition,map2);
			System.out.println("根据Condition查找单一对象——"+workLog.getContent());
			
			//简单方法：根据map，自定义条件关联关系，查找单一对象
			workLog = service.getEntity(map2,ConditionLinkEnum.OR);
			System.out.println("根据map，自定义条件关联关系，查找单一对象——"+workLog.getContent());
			
			
		} catch (Exception e) {
			System.out.println("出错了："+e.getMessage());
		}
	}
	
	@Test
    public void testEasyComplexQuery(){
		ConditionDef def = new ConditionDef(new Object[][] {
				{ "logDate > :start" },
				{ "logDate < :end" },
				{ "content like :content" },
		});
		
		ConditionLinkMap<String, Object> map = new ConditionLinkMap<String, Object>();
		map.put("start", "2015-02-14");
		map.put("end", "2015-12-29");
		//第1个是上述条件之间的关联关系，第2个是多条件的连接关系
		map.put(Condition.CONDITION_LIKE, new String[]{ConditionLinkEnum.AND.getCode(),ConditionLinkEnum.AND.getCode()});
		map.put("content", "测试%");
		map.put(Condition.CONDITION_LIKE, new String[]{Condition.LIKE_MODE,ConditionLinkEnum.OR.getCode()});
		map.put("projectId", "basp1");
		map.put("workTime", "1000");
			
		Condition condition = Condition.buildCondition(def,map);
		
		List<WorkLog> list= service.findList(condition,map);
		System.out.println("获取条数："+list.size());
		System.out.println("根据Condition查找list——"+list.get(0).getContent());
		
		
		map.put("workTime", "1111");
		map.put(Condition.CONDITION_LIKE, new String[]{ConditionLinkEnum.OR.getCode(),null});

		Condition condition1 = Condition.buildCondition(def,map);
		
		List<WorkLog> list1= service.findList(condition1,map);
		System.out.println("获取条数："+list1.size());
		System.out.println("根据Condition查找list——"+list1.get(0).getContent());
		
	}
	
	@Test
    public void testQueryDate(){
		
		//日期段 + like 多条件混合查询
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		LinkedHashMap<String, Object> map1 = new LinkedHashMap<String, Object>();
		map1.put("content", "%内容");
		map.put("start", "2015-02-14");
		map.put("end", "2015-02-29");
		
		try {
			
			//根据ConditionDef查找list
			ConditionDef def = new ConditionDef(new Object[][] { { "logDate >= :start" } ,  { "logDate <= :end" }});
			ConditionDef def1 = new ConditionDef(new Object[][] { { "content like :content" }});
			System.out.println(JSONUtils.getJSONString(def));
			
			//根据Condition查找list
			Condition condition = new Condition(def,map);
			Condition condition1 = new Condition(def1,map1,Condition.LIKE_MODE);
			condition.appendCondition(condition1,ConditionLinkEnum.OR);
			map.putAll(map1);
			
			List<WorkLog> list = service.findList(condition,map);
			
			System.out.println("获取条数："+list.size());
			System.out.println("根据map，自定义条件关联关系，查找单一对象——"+list.get(0).getContent());
			
		} catch (Exception e) {
			System.out.println("出错了："+e.getMessage());
		}
	}

	@Test
    public void testQueryList(){
		try {

			LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("content", "更新测试%");
			
			//简单方法：根据map，自定义条件关联关系，查找单一对象
			List<WorkLog> list0= service.findList(map);
			System.out.println("获取条数："+list0.size());
			System.out.println("根据map，自定义条件关联关系，查找单一对象——"+list0.get(0).getContent());
			
			
			//根据map单条件查找list
			List<WorkLog> list = service.findList(map);
			System.out.println("获取条数："+list.size());
			System.out.println("根据map单条件查找list——"+list.get(0).getContent());
			
			map.put("id", "700ffbaa-747d-4256-a9a5-b6831f36bb7f");
			//根据map多条件（and）查找list
			List<WorkLog> list1= service.findList(map);
			if (list1!=null && list1.size()>0){
				System.out.println("获取条数："+list1.size());
				System.out.println("根据map单条件查找list——"+list1.get(0).getContent());
			}
			
			//根据ConditionDef查找list
			ConditionDef def = new ConditionDef(new Object[][] { { "id = :id" } , { "content = :content"}});
			List<WorkLog> list2= service.findList(def,map);
			if (list1!=null && list1.size()>0){
				System.out.println("获取条数："+list2.size());
				System.out.println("根据ConditionDef查找list——"+list2.get(0).getContent());
			}
			
			//根据Condition查找list
			Condition condition = new Condition(def,map,ConditionLinkEnum.OR);
			List<WorkLog> list3= service.findList(condition,map);
			System.out.println("获取条数："+list3.size());
			System.out.println("根据Condition查找list——"+list3.get(0).getContent());
			
			
			//简单方法：根据map，自定义条件关联关系，查找单一对象
			List<WorkLog> list4= service.findList(map,ConditionLinkEnum.OR);
			System.out.println("获取条数："+list4.size());
			System.out.println("根据map，自定义条件关联关系，查找单一对象——"+list4.get(0).getContent());
			
		} catch (Exception e) {
			System.out.println("出错了："+e.getMessage());
		}
    }
    
	@Test
    public void testSave(){
		if(service==null){
			System.out.println("service未注入成功");
		}
		
    	WorkLog work = new WorkLog();
    	work.setLogId(UUID.randomUUID().toString());
    	work.setLogDate(new Date());
    	work.setProjectId("basp");
    	work.setJobTypeId("互联网金融");
    	work.setContent("测试内容");
    	work.setWorkTime(1000);
    	work.setFillTime(new Timestamp(new Date().getTime()));
    	work.setEmployeeId("a0001");
    	work.setArchivingState("ing");
    	try {
			service.add(work);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
    }

	@Test
    public void testUpdate() throws Exception{
		LinkedHashMap<String,Object> paramMap = new LinkedHashMap<String,Object>();
        paramMap.put("logId", "bd34bed0-2a32-42f7-b964-76b7af064e7a");
        
        ConditionDef conditionDef =new ConditionDef(new Object[][] { 
        		{ "id = :logId" } 
        });
        Condition condition = new Condition(conditionDef, paramMap,ConditionLinkEnum.OR);
        WorkLog workLog  = service.getEntity(condition,paramMap);
        
        System.out.println(workLog.getArchivingState());
		workLog.setArchivingState("end");
		workLog.setContent("更新测试");
		 service.update(workLog);
    }

	@Test
    public void testDelete() throws ServiceException{
        WorkLog work = new WorkLog();
        work.setId("c8ebbc05-aaf7-4aa2-9bf7-bf7ada8f6143");
        service.del(work);
    }
	
	@Test
	public void testQueryPageModel(){
		ConditionLinkMap<String,Object> paramMap = new PageQueryMap<String,Object>();
		paramMap.put("content", "更新测试%");
		paramMap.put("id", "1");
//		paramMap.put(Condition.ORDER_ASC, "logDate");
//		Condition condition = Condition.buildCondition(paramMap);
//		Condition condition = Condition.buildCondition(paramMap,ConditionLinkEnum.OR.getCode());
		PageModel<WorkLog> pageModel =new PageModel<WorkLog>();
		pageModel.setPageNum(1);
		pageModel.setNumPerPage(8);
		
//		pageModel = service.queryPageModel(condition,paramMap, pageModel);
//		pageModel = service.queryPageModel(paramMap, pageModel);
		pageModel = service.queryPageModel(paramMap, ConditionLinkEnum.OR.getCode() , pageModel);
        System.out.println("第"+pageModel.getPageNum()+"页，获取数据条数为："+pageModel.getList().size());
        if(pageModel.getList().size()>0){
        	System.out.println("第一条数时间为："+pageModel.getList().get(0).getLogDate());
        }
        System.out.println("pageNum："+pageModel.getPageNum());
        System.out.println("pageSize："+pageModel.getNumPerPage());
        System.out.println("count："+pageModel.getTotalRecords());
	}
	
	
}
