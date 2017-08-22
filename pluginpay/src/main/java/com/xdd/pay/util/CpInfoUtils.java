package com.xdd.pay.util;

import android.content.Context;

import com.xdd.pay.config.Config;
import com.xdd.pay.db.StorageConfig;
import com.xdd.pay.db.StorageUtils;
import com.xdd.pay.network.object.CpInfo;

/**
 * 文件名称: CpInfoUtils.java<br>
 * 作者: 刘晔 <br>
 * 邮箱: ye.liu@ocean-info.com <br>
 * 创建时间：2014-1-21 下午2:40:15<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class CpInfoUtils {
  
  private static CpInfo       mCpInfo;
  private static String		  mAppId;
  private static String		  mChannelId;

  public static CpInfo getCpInfo(Context context) {
    if (mCpInfo == null) {
      mCpInfo = new CpInfo();
      mCpInfo.setChannelId(getChannelId(context));
      mCpInfo.setAppId(getAppId(context));
      mCpInfo.setSdkVerCode(Config.SDK_VERSION_CODE);
      mCpInfo.setSdkVerName(Config.SDK_VERSION_NAME);
      mCpInfo.setClientVerCode(PackageInfoUtils.getVersionCode(context));
      mCpInfo.setClientVerName(PackageInfoUtils.getVersionName(context));
      mCpInfo.setPackageName(PackageInfoUtils.getPackageName(context));
    }
    return mCpInfo;
  }

  /**
   * 获取本地配置的APPID
   * 
   * @param context
   * @return
   * @throws Exception
   */
  public static String getAppId(Context context) {
	if (null == mAppId) {
	  mAppId = StorageUtils.getConfig4String(context, StorageConfig.PAY_APP_KEY);
		if (null == mAppId) {
			QYLog.e("app id is null!");
		}
	}

	return mAppId;
  }

  /**
   * 获取渠道号
   * 
   * @param context
   * @return
   */
  public static String getChannelId(Context context) {
    
	if (null == mChannelId) {
	  mChannelId = StorageUtils.getConfig4String(context, StorageConfig.PAY_CHANNEL_ID);
		if (null == mChannelId) {
			QYLog.e("channel id is null!");
		}
	}

	return mChannelId;
  }
}
