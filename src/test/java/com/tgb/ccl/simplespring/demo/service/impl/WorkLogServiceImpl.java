package com.tgb.ccl.simplespring.demo.service.impl;

import org.springframework.stereotype.Service;

import com.tgb.ccl.simplespring.demo.entity.WorkLog;
import com.tgb.ccl.simplespring.demo.service.IWorkLogService;
import com.tgb.ccl.simplespring.servicesupport.impl.BaseServiceImpl;

@Service
public class WorkLogServiceImpl extends BaseServiceImpl<WorkLog>  implements IWorkLogService {
}