package com.mf.model;

import android.content.pm.ResolveInfo;

public class ProcessModel {
	private ResolveInfo resolveInfo;
	/**
	 * 是否当前运行的任务
	 */
	private boolean isRunningTask;
	/**
	 * 是否按home键退到后台
	 */
	private boolean isBackgroundTask;

	public ResolveInfo getResolveInfo() {
		return resolveInfo;
	}

	public void setResolveInfo(ResolveInfo resolveInfo) {
		this.resolveInfo = resolveInfo;
	}

	public boolean isRunningTask() {
		return isRunningTask;
	}

	public void setIsRunningTask(boolean isRunningTask) {
		this.isRunningTask = isRunningTask;
	}

	public boolean isBackgroundTask() {
		return isBackgroundTask;
	}

	public void setIsBackgroundTask(boolean isBackgroundTask) {
		this.isBackgroundTask = isBackgroundTask;
	}
}
