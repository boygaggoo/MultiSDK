package com.mf.download.model;

import android.os.Handler;

import com.mf.basecode.model.MyPackageInfo;

public class DownloadInfo extends MyPackageInfo {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Handler           handler;

  private String            url;

  private String            md5;

  private boolean           isDownloadNext;
  
  private boolean           resume = false; 
  
  private boolean           saveDb = true;
  
  private boolean           pre = false;
  
  public DownloadInfo() {
    super();
  }

  public DownloadInfo(String packageName, int versionCode) {
    super(packageName, versionCode);
  }

  
//  public DownloadInfo(String adid,String packageName, int versionCode, int position, int source, Handler handler, String url, String md5) {
//    super(adid,packageName, versionCode, position, source);
//    this.handler = handler;
//    this.url = url;
//    this.md5 = md5;
//  }

  public DownloadInfo(Handler handler, String adid,String packageName, int versionCode, int position, int source, String url, String md5, boolean isDownloadNext,boolean pre) {
    super(adid,packageName, versionCode, position, source);
    this.handler = handler;
    this.url = url;
    this.md5 = md5;
    this.isDownloadNext = isDownloadNext;
    this.pre = pre;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public Handler getHandler() {
    return handler;
  }

  public void setHandler(Handler handler) {
    this.handler = handler;
  }

  public boolean isDownloadNext() {
    return isDownloadNext;
  }

  public void setDownloadNext(boolean isDownloadNext) {
    this.isDownloadNext = isDownloadNext;
  }

  public boolean isResume() {
    return resume;
  }

  public void setResume(boolean resume) {
    this.resume = resume;
  }

  public boolean isSaveDb() {
    return saveDb;
  }

  public void setSaveDb(boolean saveDb) {
    this.saveDb = saveDb;
  }

  public boolean isPre() {
    return pre;
  }

  public void setPre(boolean pre) {
    this.pre = pre;
  }

  @Override
  public String toString() {
    return "DownloadInfo [handler=" + handler + ", url=" + url + ", md5=" + md5 + ", isDownloadNext=" + isDownloadNext + ", resume=" + resume + ", saveDb="
        + saveDb + ", pre=" + pre + "]";
  }
}
