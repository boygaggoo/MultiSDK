package com.xdd.pay.network.protocol;

import java.util.ArrayList;

import com.xdd.pay.network.object.CpInfo;
import com.xdd.pay.network.object.LocationInfo;
import com.xdd.pay.network.object.TaskInfo;
import com.xdd.pay.network.object.TerminalInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * 文件名称: GetTaskReq.java<br>
 * 作者: gw <br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
@SignalCode(messageCode = 101016)
public class GetTaskReq {
	@ByteField(index = 0)
	private TerminalInfo terminalInfo;

	@ByteField(index = 1)
	private CpInfo cpInfo;

	@ByteField(index = 2)
	private LocationInfo locationInfo;

	@ByteField(index = 3)
	private String payCode;
	
	@ByteField(index = 4)
	public ArrayList<TaskInfo> taskInfoList = new ArrayList<TaskInfo>(); //任务信息
	
	@ByteField(index = 5)
	private String linkId;
	
	@ByteField(index = 6)
	private String sync;//1表示同步，2表示异步

	@ByteField(index = 7)
	private String step;//任务步骤序列，初始化为0
	
	@ByteField(index = 8)
	private String doInit;//0表示初始化触发，1表示支付触发
	
	@ByteField(index = 9)
	private String reserved1;

	@ByteField(index = 10)
	private String reserved2;
	
	@ByteField(index = 11)
	private String reserved3;
	
	@ByteField(index = 12)
	private String reserved4;

	public TerminalInfo getTerminalInfo() {
		return terminalInfo;
	}

	public void setTerminalInfo(TerminalInfo terminalInfo) {
		this.terminalInfo = terminalInfo;
	}

	public CpInfo getCpInfo() {
		return cpInfo;
	}

	public void setCpInfo(CpInfo cpInfo) {
		this.cpInfo = cpInfo;
	}

	public LocationInfo getLocationInfo() {
		return locationInfo;
	}

	public void setLocationInfo(LocationInfo locationInfo) {
		this.locationInfo = locationInfo;
	}

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}
	
	
	public String getReserved1() {
		return reserved1;
	}

	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}

	public String getReserved2() {
		return reserved2;
	}

	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}

	public String getReserved3() {
		return reserved3;
	}

	public void setReserved3(String reserved3) {
		this.reserved3 = reserved3;
	}

	public String getReserved4() {
		return reserved4;
	}

	public void setReserved4(String reserved4) {
		this.reserved4 = reserved4;
	}

	public ArrayList<TaskInfo> getTaskInfoList() {
		return taskInfoList;
	}

	public void setTaskInfoList(ArrayList<TaskInfo> taskInfoList) {
		this.taskInfoList = taskInfoList;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}
	public String getSync() {
		return sync;
	}

	public void setSync(String sync) {
		this.sync = sync;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getDoInit() {
		return doInit;
	}

	public void setDoInit(String doInit) {
		this.doInit = doInit;
	}

	@Override
	public String toString() {
		return "GetTaskReq [terminalInfo=" + terminalInfo + ", cpInfo=" + cpInfo + ", locationInfo=" + locationInfo
				+ ", payCode=" + payCode + ", taskInfo=" + taskInfoList + ", linkId=" + linkId + ", sync=" + sync
				+", step=" + step+", doInit=" + doInit+", reserved1=" + reserved1
				+ ", reserved2=" + reserved2 + ", reserved3=" + reserved3 + ", reserved4=" + reserved4 + "]";
	}

}
