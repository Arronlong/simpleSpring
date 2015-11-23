package com.tgb.ccl.simplespring.demo.entity;

import java.sql.Timestamp;
import java.util.Date;

import com.tgb.ccl.simplespring.annotation.Column;
import com.tgb.ccl.simplespring.annotation.NoCreate;
import com.tgb.ccl.simplespring.annotation.NoInsert;
import com.tgb.ccl.simplespring.annotation.NoUpdate;
import com.tgb.ccl.simplespring.annotation.PK;
import com.tgb.ccl.simplespring.annotation.Table;
import com.tgb.ccl.simplespring.basic.Entity;

@Table(value="worklog")
public class WorkLog extends Entity {
	/**
	 * 
	 */
	@NoUpdate
	@NoInsert
	@NoCreate
	private static final long serialVersionUID = -6646894662876497580L;
	// id
	@PK
	@Column(value="id")
	private String logId;
	// 日志日期
	@NoUpdate
	private Date logDate; // log_date
	// 所属项目
	private String projectId;
	// 工作类型
	private String jobTypeId;
	// 日志内容
	private String content;
	// 工作时长
	private double workTime;
	// 填写时间
	private Timestamp fillTime;
	// 日志填写人
	@NoUpdate
	private String employeeId;
	// 状态
	@NoUpdate
	private String archivingState;

	@Override
	public Object getId() {
		return getLogId();
	}

	@Override
	public WorkLog setId(Object logId) {
		setLogId(logId.toString());
		return this;
	}

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getJobTypeId() {
		return jobTypeId;
	}

	public void setJobTypeId(String jobTypeId) {
		this.jobTypeId = jobTypeId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public double getWorkTime() {
		return workTime;
	}

	public void setWorkTime(double workTime) {
		this.workTime = workTime;
	}

	public Timestamp getFillTime() {
		return fillTime;
	}

	public void setFillTime(Timestamp fillTime) {
		this.fillTime = fillTime;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getArchivingState() {
		return archivingState;
	}

	public void setArchivingState(String archivingState) {
		this.archivingState = archivingState;
	}

}