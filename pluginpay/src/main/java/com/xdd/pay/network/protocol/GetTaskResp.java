package com.xdd.pay.network.protocol;

import java.util.ArrayList;

import com.xdd.pay.network.object.TaskInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.ResponseBody;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * 文件名称: GetTaskResp.java<br>
 * 作者:   gw<br>
 * 创建时间：2015-10-21 17:30:26<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
@SignalCode(messageCode = 201016)
public class GetTaskResp extends ResponseBody {

	private static final long serialVersionUID = 862846061134979574L;
	@ByteField(index = 2)
	private ArrayList<TaskInfo> taskInfoList = new ArrayList<TaskInfo>();
	
	@ByteField(index = 3)
	private String step;
	
	@ByteField(index = 4)
	private String doInit;
	
	@ByteField(index = 5)
	private String linkId;
	
	@ByteField(index = 6)
	private String reserved1;

	@ByteField(index = 7)
	private String reserved2;

	@ByteField(index = 8)
	private String reserved3;
	
	@ByteField(index = 9)
	private String reserved4;

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

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
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

	public ArrayList<TaskInfo> getTaskInfoList() {
		return taskInfoList;
	}

	public void setTaskInfoList(ArrayList<TaskInfo> taskInfoList) {
		this.taskInfoList = taskInfoList;
	}

	@Override
	public String toString() {
		return "GetTaskResp [step=" + step + ", linkId=" + linkId + ", doInit=" + doInit
				+ ", taskInfoList=" + taskInfoList + ", reserved1=" + reserved1 + ", reserved2=" + reserved2
				+ ", reserved3=" + reserved3 + ", reserved4=" + reserved4 + "]";
	}
}
