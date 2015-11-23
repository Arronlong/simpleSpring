package com.tgb.ccl.simplespring.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tgb.ccl.simplespring.basic.Entity;
import com.tgb.ccl.simplespring.support.PageModel;
import com.tgb.ccl.simplespring.util.JSONUtils;

/**
 * 公共controller
 * 
 * @author arron
 * @date 2015年4月9日 下午7:23:37 
 * @version 1.0
 */
public class BaseController{

    public Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 输出json格式
     * @return
     */
    public JsonNode valueToTree(PageModel<?> pageModel){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        SimpleModule simpleModule = new SimpleModule();
        objectMapper.registerModule(simpleModule);
        return objectMapper.valueToTree(pageModel);
    }
	
    /**
     * 输出Json字符串
     * 
     * @param entity
     * @param response
     */
    protected void writeJsonStr(Entity entity, HttpServletResponse response) {
        try {
            response.setCharacterEncoding("utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(JSONUtils.getJSONString(entity));
            writer.flush();
            writer.close();
        } catch (Exception e) {
            logger.info("页面回写json错误，请查证。。。。。。{}" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 输出文本字符串
     * 
     * @param entity
     * @param response
     */
    protected void writeTextStr(String message, HttpServletResponse response) {
        try {
            response.setCharacterEncoding("utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(message);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            logger.info("页面会写提示错误，请查证。。。。。。{}" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 输出成功，格式：{result:'true',msg:''}
     * 
     * @param entity
     * @param response
     */
    protected void writeOK(String msg,HttpServletResponse response) {
    	writeTextStr("{\"result\":\"true\",\"msg\":\""+msg+"\"}",response);
    }
    
    /**
     * 输出失败，格式：{result:'false',msg:''}
     * 
     * @param entity
     * @param response
     */
    protected void writeErr(String msg,HttpServletResponse response) {
    	writeTextStr("{\"result\":\"false\",\"msg\":\""+msg+"\"}",response);
    }
}
