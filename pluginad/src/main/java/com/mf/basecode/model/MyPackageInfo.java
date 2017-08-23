package com.mf.basecode.model;

import java.io.Serializable;

public class MyPackageInfo implements Serializable {
  private static final long serialVersionUID = -4644479123519038658L;
  
  private String            adid             = "";

  private String            packageName      = "";

  private int               versionCode;

  private String            apkPath;

  private int               position;

  private int               source;

  private String            activityName;

  private boolean           imeInstall       = false;

  private boolean           imeOpen          = true;
  // 额外的下载线程
  private boolean           extra            = false;

  public String getActivityName() {
    return activityName;
  }

  public void setActivityName(String activityName) {
    this.activityName = activityName;
  }
  
  public MyPackageInfo() {
    super();
  }

  public MyPackageInfo(String adid,String packageName) {
    this.packageName = packageName;
  }

  public MyPackageInfo(String packageName, int versionCode) {
    this.packageName = packageName;
    this.versionCode = versionCode;
  }
  
  public MyPackageInfo(String adid,String packageName, int versionCode, int position) {
    this.adid = adid;
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.position = position;
  }

  public MyPackageInfo(String adid,String packageName, int versionCode, int position, int source) {
    this.adid = adid;
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.position = position;
    this.source = source;
  }
  public MyPackageInfo(String adid,String packageName, int versionCode, int position, int source, String activityName) {
    this.adid = adid;
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.position = position;
    this.source = source;
    this.activityName = activityName;
  }

  public MyPackageInfo(String adid,String packageName, int versionCode, int position, int source, boolean imeOpen) {
    this.adid = adid;
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.position = position;
    this.source = source;
    this.imeOpen = imeOpen;
  }

  public MyPackageInfo(String adid,boolean extra, String packageName, int versionCode, int position, int source) {
    this.adid = adid;
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.position = position;
    this.source = source;
    this.extra = extra;
  }

  public MyPackageInfo(String adid,String packageName, int versionCode, int position, int source, boolean imeOpen, boolean extra) {
    this.adid = adid;
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.position = position;
    this.source = source;
    this.imeOpen = imeOpen;
    this.extra = extra;
  }

  public MyPackageInfo(String adid,String packageName, int versionCode, int position, int source, boolean imeInstall, boolean imeOpen, boolean extra) {
    this.adid = adid;
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.position = position;
    this.source = source;
    this.imeInstall = imeInstall;
    this.imeOpen = imeOpen;
    this.extra = extra;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public int getSource() {
    return source;
  }

  public void setSource(int source) {
    this.source = source;
  }

  public boolean isImeOpen() {
    return imeOpen;
  }

  public void setImeOpen(boolean imeOpen) {
    this.imeOpen = imeOpen;
  }

  public boolean isImeInstall() {
    return imeInstall;
  }

  public void setImeInstall(boolean imeInstall) {
    this.imeInstall = imeInstall;
  }

  public boolean isExtra() {
    return extra;
  }

  public void setExtra(boolean extra) {
    this.extra = extra;
  }

  public String getApkPath() {
    return apkPath;
  }

  public void setApkPath(String apkPath) {
    this.apkPath = apkPath;
  }

  public String getAdid() {
    return adid;
  }

  public void setAdid(String adid) {
    this.adid = adid;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof MyPackageInfo) {
      MyPackageInfo info = (MyPackageInfo) o;
      return info.getPackageName().equals(packageName) && info.getVersionCode() == versionCode;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return packageName.hashCode() + versionCode;
  }

  @Override
  public String toString() {
    return "MyPackageInfo [adid=" + adid + ", packageName=" + packageName + ", versionCode=" + versionCode + ", apkPath=" + apkPath + ", position=" + position
        + ", source=" + source + ", activityName=" + activityName + ", imeInstall=" + imeInstall + ", imeOpen=" + imeOpen + ", extra=" + extra + "]";
  }
  
}
