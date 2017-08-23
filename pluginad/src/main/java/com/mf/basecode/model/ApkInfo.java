package com.mf.basecode.model;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

public class ApkInfo {
	private PackageInfo packageInfo;

	private long fileSize;

	private String path;

	private String extraIconUrl;

	private int iconId;

	private String appName;

	private String iconUrl;

	private String url;

	private String md5;

	private Drawable drawable;

	private String label;
	
	private String fileName;

	public PackageInfo getPackageInfo() {
		return packageInfo;
	}

	public void setPackageInfo(PackageInfo packageInfo) {
		this.packageInfo = packageInfo;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getExtraIconUrl() {
		return extraIconUrl;
	}

	public void setExtraIconUrl(String extraIconUrl) {
		this.extraIconUrl = extraIconUrl;
	}

	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
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

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

//	public static ApkInfo switchSerApkInfoToThis(SerApkInfo serApkInfo) {
//		ApkInfo apkInfo = new ApkInfo();
//		PackageInfo pInfo = new PackageInfo();
//		pInfo.packageName = serApkInfo.getPackageName();
//		pInfo.versionCode = serApkInfo.getVer();
//		pInfo.versionName = serApkInfo.getVerName();
//		apkInfo.setPackageInfo(pInfo);
//		apkInfo.setFileSize(serApkInfo.getFileSize());
//		apkInfo.setAppName(serApkInfo.getAppName());
//		apkInfo.setIconId(serApkInfo.getIconId());
//		apkInfo.setIconUrl(serApkInfo.getIconUrl());
//		apkInfo.setUrl(serApkInfo.getDownloadUrl());
//		apkInfo.setMd5(serApkInfo.getFileVerifyCode());
//		apkInfo.setFileName(serApkInfo.getFileName());
//		return apkInfo;
//	}

  @Override
  public String toString() {
    return "ApkInfo [packageInfo=" + packageInfo + ", fileSize=" + fileSize + ", path=" + path + ", extraIconUrl=" + extraIconUrl + ", iconId=" + iconId
        + ", appName=" + appName + ", iconUrl=" + iconUrl + ", url=" + url + ", md5=" + md5 + ", drawable=" + drawable + ", label=" + label + ", fileName="
        + fileName + "]";
  }
}
