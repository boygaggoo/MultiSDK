package com.mf.network.object;

import android.graphics.drawable.Drawable;

import com.mf.model.AdDbInfo;

public class FolderIconInfo {
  private String            adid;
  private String            iconName;
  private String            language;
  private String            iconUrl;
  private String            appName;
  private String            packageName;
  private String            downloadUrl;
  private String            fileVerifyCode;
  private int               adType;
  private int               ver;
  private String            verName;
  private long               fileSize;
  private int               downloadTimes;
  private Drawable          drawable;
  private boolean           system = false;
  
  public FolderIconInfo() {
  }

  public FolderIconInfo(AdDbInfo info) {
    this.adid = info.getAdId();
    this.iconName = info.getPicName();
    this.iconUrl = info.getAdPicUrl();
    this.appName = info.getAdName();
    this.packageName = info.getPackageName();
    this.downloadUrl = info.getAdDownUrl();
    this.fileVerifyCode = info.getFileMd5();
    this.ver = info.getVersionCode();
    this.adType = info.getAdType();
    this.fileSize = info.getFileSize();
    this.downloadTimes = info.getDownloadTimes();
    this.language = info.getAdLanguage();
  }
  
  public String getAdid() {
    return adid;
  }

  public void setAdid(String adid) {
    this.adid = adid;
  }

  public String getIconName() {
    return iconName;
  }

  public void setIconName(String iconName) {
    this.iconName = iconName;
  }

  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }

  public String getIconUrl() {
    return iconUrl;
  }
  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }
  public String getAppName() {
    return appName;
  }
  public void setAppName(String appName) {
    this.appName = appName;
  }
  public String getPackageName() {
    return packageName;
  }
  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }
  public String getDownloadUrl() {
    return downloadUrl;
  }
  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }
  public String getFileVerifyCode() {
    return fileVerifyCode;
  }
  public void setFileVerifyCode(String fileVerifyCode) {
    this.fileVerifyCode = fileVerifyCode;
  }
  public int getVer() {
    return ver;
  }
  public void setVer(int ver) {
    this.ver = ver;
  }
  public String getVerName() {
    return verName;
  }
  public void setVerName(String verName) {
    this.verName = verName;
  }

  public void setFileSize(int fileSize) {
    this.fileSize = fileSize;
  }
  public Drawable getDrawable() {
    return drawable;
  }
  public void setDrawable(Drawable drawable) {
    this.drawable = drawable;
  }
  public boolean isSystem() {
    return system;
  }
  public void setSystem(boolean system) {
    this.system = system;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public int getDownloadTimes() {
    return downloadTimes;
  }

  public void setDownloadTimes(int downloadTimes) {
    this.downloadTimes = downloadTimes;
  }

  public int getAdType() {
    return adType;
  }

  public void setAdType(int adType) {
    this.adType = adType;
  }

  @Override
  public String toString() {
    return "FolderIconInfo [adid=" + adid + ", iconName=" + iconName + ", language=" + language + ", iconUrl=" + iconUrl + ", appName=" + appName
        + ", packageName=" + packageName + ", downloadUrl=" + downloadUrl + ", fileVerifyCode=" + fileVerifyCode + ", adType=" + adType + ", ver=" + ver
        + ", verName=" + verName + ", fileSize=" + fileSize + ", downloadTimes=" + downloadTimes + ", drawable=" + drawable + ", system=" + system + "]";
  }
  
}


