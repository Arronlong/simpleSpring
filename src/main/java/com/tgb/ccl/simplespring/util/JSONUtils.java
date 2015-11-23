/**
 * 
 */
package com.tgb.ccl.simplespring.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Allen
 *
 */
public class JSONUtils {
	
	public static String getJSONString(Object obj){  
        ObjectMapper objectMapper = new ObjectMapper();  
        try {  
            return objectMapper.writeValueAsString(obj);  
            // 格式化打印  
//          return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);  
        } catch (JsonProcessingException e) {  
            throw new RuntimeException(e);  
        }  
    }  
      
    public static <T> T getObjectFromJSONString(String json,Class<T> clazz){  
        ObjectMapper objectMapper = new ObjectMapper();  
        try {  
            return objectMapper.readValue(json, clazz);  
        } catch (JsonParseException e) {  
            throw new RuntimeException(e);  
        } catch (JsonMappingException e) {  
            throw new RuntimeException(e);  
        } catch (IOException e) {  
            throw new RuntimeException(e);  
        }  
    }  

}
