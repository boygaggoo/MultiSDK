package com.mf.utils;

import android.content.res.Resources;

import com.mf.basecode.utils.Logger;

public class ResourceIdUtils {
  private static ResourceIdUtils mInstance;
  private Resources              mResources;
  private static final String    TAG = "ResourceIdUtils";

  private ResourceIdUtils() {
    mResources = getResources();
  }
  public static ResourceIdUtils getInstance() {
    if (mInstance == null) {
      synchronized (ResourceIdUtils.class) {
        if (mInstance == null) {
          mInstance = new ResourceIdUtils();
        }
      }
    }
    return mInstance;
  }

  public Resources getResources() {
    return mResources;
  }
  public void setResources(Resources mResources) {
    this.mResources = mResources;
  }
  /**
   * 根据资源名获取资源id
   * 
   * @param name
   *          如R.layout.aa
   * @return
   */
  public int getResourceId(String name) {
    int id = 0;
    try {
      if (mResources != null) {
        String[] strs = name.split("\\.");
        id = mResources.getIdentifier(strs[2], strs[1], "zjhz.xh");
      } else {
        Logger.debug(TAG, "mResources is null");
      }
    } catch (Exception e) {
      Logger.p(e);
    }
    Logger.d(TAG, "resource name = " + name + " resource id =" + id);
    return id;
  }
  public String getStringByResId(String name) {
    String str = "";
    try {
      if (mResources != null) {
        str = mResources.getString(getResourceId(name));
      } else {
        Logger.debug(TAG, "mResources is null");
      }
    } catch (Exception e) {
      Logger.p(e);
    }
    Logger.debug(TAG, "resource name:" + name + " id=" + str);
    return str;
  }

}
