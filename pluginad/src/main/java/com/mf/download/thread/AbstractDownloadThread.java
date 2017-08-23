package com.mf.download.thread;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.os.Handler;

import com.mf.basecode.network.util.NetworkUtils;
import com.mf.basecode.utils.Md5Util;
import com.mf.download.util.DownloadConstants;

public abstract class AbstractDownloadThread extends Thread implements IDownloadThread {

  protected Context          mContext;

  protected Handler          mHandler;

  protected boolean          isDownloading     = true;

  protected String           mSavePath;

  protected String           MD5;

  protected String           mURL;

  protected int              offset;

  protected int              closeConnectCount = 0;

  protected static final int RECONNECT_COUNT   = 3;

  protected int              downloadResult    = DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOADING;

  protected String           tmpFilePath;

  protected String           downloadFilePath;

  protected int              totalSize;

  protected int              downloadSize;

  protected float            disProgress;

  // 接受数据
  public void reciveData(HttpURLConnection httpConnection, String downloadFilePath, String apkFilePath) throws IOException {
    downloadSize = 0;
    totalSize = (int) NetworkUtils.getLengthByURLConnection(httpConnection);

    if (totalSize <= 0) {
      throw new IOException();
    }

    totalSize += offset;

    RandomAccessFile oSavedFile = new RandomAccessFile(downloadFilePath, "rw");
    oSavedFile.seek(offset);

    InputStream input = httpConnection.getInputStream();
    byte[] buffer = new byte[2048];
    int nRead = input.read(buffer, 0, 2048);
    disProgress = (float) offset * 100.0f / (float) totalSize;
    while (isDownloading && nRead > 0 && offset < totalSize) {
      oSavedFile.write(buffer, 0, nRead);
      offset += nRead;
      downloadSize += nRead;
      // 广播进度
      disProgress = (float) (offset) * 100.0f / (float) totalSize;
      sendProgressMsg();
      nRead = input.read(buffer, 0, 2048);
    }
    httpConnection.disconnect();

    // 关闭流
    try {
      oSavedFile.close();
    } catch (Exception e) {
    }
    // 下载完成
    if (offset == totalSize) {
      File downloadFile = new File(downloadFilePath);
      File apkFile = new File(apkFilePath);
      downloadFile.renameTo(apkFile);
      String md5 = "";
      try {
        md5 = Md5Util.getMd5FromFile(apkFilePath);
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
      // 根据MD5来检测包的完整性
      if (md5.equalsIgnoreCase(MD5)) {
        sendStopMsg(DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FINISH);
        downloadResult = DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FINISH;
      } else {
        apkFile.delete();
        sendStopMsg(DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FAIL);
      }
    } else {
      sendStopMsg(DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FAIL);
    }
  }

  public void connect() {
    HttpURLConnection httpConnection = null;
    try {
      URL url = new URL(mURL);
      httpConnection = (HttpURLConnection) url.openConnection();
      httpConnection.setReadTimeout(10000);
      httpConnection.setConnectTimeout(5000);
      httpConnection.setRequestProperty("User-Agent", DownloadConstants.UPDATE_SEVICE_USERAGENT);
      String fileRangeStart = "bytes=" + offset + "-";
      httpConnection.setRequestProperty("RANGE", fileRangeStart);
      int responseCode = httpConnection.getResponseCode();
      if (responseCode >= 200 && responseCode < 300) {
        reciveData(httpConnection, tmpFilePath, downloadFilePath);
      } else {
        reconnect();
      }
    } catch (IOException e) {
      reconnect();
    }
  }

  public void reconnect() {
    closeConnectCount++;
    if (closeConnectCount < RECONNECT_COUNT && isDownloading) {
      connect();
    } else {
      sendStopMsg(DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FAIL);
    }
  }

  public void onPause() {
    isDownloading = false;
    sendStopMsg(DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_PAUSE);
  }

  public boolean isDownloading() {
    return isDownloading;
  }
}
