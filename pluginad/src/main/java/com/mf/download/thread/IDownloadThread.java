package com.mf.download.thread;

public interface IDownloadThread {

	public void sendProgressMsg();

	public void sendStopMsg(int status);
}
