package com.tgb.ccl.simplespring.demo.test;

import com.tgb.ccl.simplespring.demo.entity.WorkLog;
import com.tgb.ccl.simplespring.util.SQLUtils;

public class SqlTest {
	

	public static void main(String[] args){
		String sql = SQLUtils.buildCreateSql(WorkLog.class);
		System.out.println(sql);
	}

}
