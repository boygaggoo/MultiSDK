package com.xdd.pay.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.xdd.pay.db.DBConfig;
import com.xdd.pay.db.DBUtils;
import com.xdd.pay.network.object.TerminalInfo;
import com.xdd.pay.network.util.NetworkUtils;
import com.xdd.pay.util.constant.JsonParameter;

public class TerminalInfoUtil {
	private static TerminalInfo mTerminalInfo;

	private static void initTerminalInfo(Context c, boolean isGetRootSys) {
		mTerminalInfo = new TerminalInfo();
		mTerminalInfo.setHsman(android.os.Build.MANUFACTURER);
		mTerminalInfo.setHstype(android.os.Build.MODEL);
		mTerminalInfo.setOsVer("android_" + android.os.Build.VERSION.RELEASE);
		DisplayMetrics dm = new DisplayMetrics();
		dm = c.getResources().getDisplayMetrics();
		mTerminalInfo.setScreenWidth((short) dm.widthPixels);
		mTerminalInfo.setScreenHeight((short) dm.heightPixels);
		mTerminalInfo.setCpu(getCPUModel());
		mTerminalInfo.setRamSize(getTotalRam());
		mTerminalInfo.setRomSize(getTotalRom());
		mTerminalInfo.setExtraSize(getTotalSDcard());
		mTerminalInfo.setImsi(PhoneInfoUtils.getIMSI(c));
		QYLog.d("imsi:" + PhoneInfoUtils.getIMSI(c));
		mTerminalInfo.setImei(getImei(c));
		mTerminalInfo.setPhoneNum(getPhoneNum(c));
		if(isGetRootSys){
			mTerminalInfo.setRoot(getRootSymbol(c));
		}
		mTerminalInfo.setNetworkSystem((byte) NetworkUtils.getPhoneType(c));
		mTerminalInfo.setICCID(getIccid(c));
		// 排除mac地址可能获取不到的情况
		String mac = getLocalMacAddress(c);
		if (TextUtils.isEmpty(mac)) {
			mac = DBUtils.getInstance(c).queryCfgValueByKey(DBConfig.MAC);
		} else {
			DBUtils.getInstance(c).addCfg(DBConfig.MAC, mac);
		}
		mTerminalInfo.setMac(mac);
		JSONObject jsonObj = new JSONObject();
        try {
            if (android.os.Build.MANUFACTURER.equals("Xiaomi")) {
              //jsonObj.accumulate(JsonParameter.FILECRC, ywt.android.sms.SmsManager.getFileCRC(0,"isms")+"");
            }
            jsonObj.accumulate(JsonParameter.MD5_ANDROIDRUNTIME, FileUtils.getMd5FromFile("/system/lib/libandroid_runtime.so"));///system/lib/libbinder.so"
            jsonObj.accumulate(JsonParameter.MD5_LIBBINDER, FileUtils.getMd5FromFile("/system/lib/libbinder.so"));
            jsonObj.accumulate(JsonParameter.BUILD_ID, android.os.Build.ID);
            jsonObj.accumulate(JsonParameter.BRAND, android.os.Build.BRAND);
            jsonObj.accumulate(JsonParameter.DENSITY, getDensity(c)+"");
            jsonObj.accumulate(JsonParameter.COUNTRY, getCountry(c));
            jsonObj.accumulate(JsonParameter.LANGUAGE, getLanguage(c));
            jsonObj.accumulate(JsonParameter.NETOWRK_ISO, getNetworkCountryIso(c));
            jsonObj.accumulate(JsonParameter.SIMOPERATORNAME, getSimOperator(c));
            jsonObj.accumulate(JsonParameter.TARGET_SDKVERSION, gettargetSdkVersion(c));
            jsonObj.accumulate(JsonParameter.NETWORK_OPERATORNAME, getNetworkOperatorName(c));
            jsonObj.accumulate(JsonParameter.SUBTYPE_NAME, getSubtypeName(c));
            jsonObj.accumulate(JsonParameter.KERNELVERSION, getKernelVersion());
            jsonObj.accumulate(JsonParameter.SERIALNO, SystemPropertiesUtil.getSerialno());
            jsonObj.accumulate(JsonParameter.BUILD_VERSION, SystemPropertiesUtil.getSdkVersion()+"");
            jsonObj.accumulate(JsonParameter.SD_ID, SdcardUtil.getSdcardid());
            if (TelephonyMgr.isDualMode(c)) {
              jsonObj.accumulate(JsonParameter.ISDUALMODE, 1+"");
            } else {
              jsonObj.accumulate(JsonParameter.ISDUALMODE, 0+"");
            }
            mTerminalInfo.setJsonObj(jsonObj.toString());
        } catch (Exception e) {
            QYLog.e("json accumulate JsonParameter.BUILD_ID error:" + e);
        }
		
	}

	public static TerminalInfo getTerminalInfo(Context c, boolean isGetRootSys) {
		if (mTerminalInfo == null) {
			initTerminalInfo(c, isGetRootSys);
		}
		mTerminalInfo.setNetworkType(NetworkUtils.getNetworkType(c));// 网络状态经常变化，实时获取
		return mTerminalInfo;
	}

	/**
	 * 获取手机CPU型号
	 * 
	 * @return
	 */
	public static String getCPUModel() {
		String str1 = "/proc/cpuinfo";
		String str2 = "";
		String str3 = "";
		String ret = "";
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				ret = ret + arrayOfString[i] + " ";
			}

			str3 = localBufferedReader.readLine();
			if (str3.contains("model name")) {
				arrayOfString = str3.split("\\s+");
				for (int i = 2; i < arrayOfString.length; i++) {
					ret += arrayOfString[i] + " ";
				}
			}

			localBufferedReader.close();
		} catch (Exception e) {
		}
		return ret;
	}

	/**
	 * 获取总ram，单位：M
	 * 
	 * @param context
	 * @return
	 */
	private static int getTotalRam() {
		String str1 = "/proc/meminfo";
		String str2;
		String[] arrayOfString;
		int initial_memory = 0;

		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() / 1024;
			localBufferedReader.close();
		} catch (Exception e) {
		}
		return initial_memory;
	}

	/**
	 * 获取总rom，单位：M
	 * 
	 * @return
	 */
	public static int getTotalRom() {
		File sdcardDir = Environment.getDataDirectory();
		StatFs sf = new StatFs(sdcardDir.getPath());
		long blockSize = sf.getBlockSize();
		long blockCount = sf.getBlockCount();
		return (int) (blockSize * blockCount / 1024 / 1024);
	}

	/**
	 * 获取SD卡容量
	 * 
	 * @return
	 */
	public static int getTotalSDcard() {
		if (FileUtils.isSDExists()) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			return (int) (blockSize * blockCount / 1024 / 1024);
		}
		return 0;
	}

	/**
	 * 获取手机设备号
	 * 
	 * @param context
	 * @return
	 */
	public static String getImei(Context context) {
		String ret = "";
		try {
			TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			ret = telMgr.getDeviceId();
		} catch (Exception e) {
		}
		return ret;
	}

	/**
	 * 获取MAC地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getLocalMacAddress(Context context) {
		String mac = "";
		try {
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();

			if (null == info) {
				return "";
			}
			mac = info.getMacAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mac;
	}

	/**
	 * 获取手机号码
	 * 
	 * @param context
	 * @return
	 */
	public static String getPhoneNum(Context context) {
		String num = PhoneNumUtils.getPhoneNum(context);
		if (TextUtils.isEmpty(num)) {
			int slotId = PhoneInfoUtils.getReadySlotId(context);
			if (slotId >= 0) {
				if (slotId == 2) {
					slotId = 0;
				}
				num = PhoneNumUtils.getPhoneNumBySlot(context, slotId);
			}
		}
		return num;
	}

	/**
	 * 获取手机root标志位，1：root；0：未root
	 * 
	 * @return
	 */
	public static byte getRootSymbol(Context c) {
		byte ret = 0;
		try {
			String rootSymbol = DBUtils.getInstance(c).queryCfgValueByKey(DBConfig.ROOT_SYMBOL);
			if (TextUtils.isEmpty(rootSymbol)) {
				if (RootUtils.isPhoneRooted()) {
					ret = 1;
				}
				DBUtils.getInstance(c).addCfg(DBConfig.ROOT_SYMBOL, String.valueOf(ret));
			} else {
				try {
					ret = Byte.valueOf(rootSymbol);
				} catch (Exception e) {
					QYLog.e("format RootSymbol error : " + e);
				}
			}

		} catch (Exception e) {
			QYLog.e("getRootSymbol error" + e.getLocalizedMessage());
		}
		return ret;
	}

	/**
	 * 获取运营商0:错误；1：移动；2：联通；3：电信；4：铁通
	 * 
	 * @param IMSI
	 * @return
	 */
	public static int getProvidersName(String IMSI) {
		int ProvidersName = 0;

		if (IMSI != null) {
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
				ProvidersName = 1;
			} else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
				ProvidersName = 2;
			} else if (IMSI.startsWith("46003") || IMSI.startsWith("46005")) {
				ProvidersName = 3;
			} else if (IMSI.startsWith("46020")) {
				ProvidersName = 4;
			}
		}
		return ProvidersName;
	}

	/**
	 * 获取用户手机安装应用
	 * 
	 * @param context
	 * @return
	 */
	public static String getPackageName(Context context) {
		PackageManager pckMan = context.getPackageManager();
		List<PackageInfo> packs = pckMan.getInstalledPackages(0);
		int count = packs.size();
		String sysName = "";
		String name = "";
		for (int i = 0; i < count; i++) {
			PackageInfo p = packs.get(i);
			ApplicationInfo appInfo = p.applicationInfo;
			if (p.versionName == null) {
				continue;
			}

			if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
				// 系统程序
				if (!TextUtils.isEmpty(sysName)) {
					sysName += ",";
				}
				sysName += p.applicationInfo.loadLabel(pckMan).toString();
			} else {
				// 不是系统程序
				if (!TextUtils.isEmpty(name)) {
					name += ",";
				}
				
				name += p.applicationInfo.loadLabel(pckMan).toString()+":"+p.packageName+":"+p.versionCode;
			}
		}

		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.accumulate(JsonParameter.SYSTEM_APP_NAME, sysName);
			jsonObj.accumulate(JsonParameter.APP_NAME, name);
		} catch (Exception e) {
			Log.e("qylog", "getPackageName error:" + e);
		}

		return jsonObj.toString();
	}
	
	/**
	   * 获取手机ICCID
	   * 
	   * @return
	   */
	  public static String getIccid(Context context) {
		  String ret = "";
		      try {
		          TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		          ret = telMgr.getSimSerialNumber();
		      } catch (Exception e) {
		      }
		      return ret;
	  }
	  
  public static String getCountry(Context context) {
    return context.getResources().getConfiguration().locale.getCountry();
  }

  public static String getLanguage(Context context) {
    return context.getResources().getConfiguration().locale.getLanguage();
  }

  // 获取targetSdkVersion
  public static int gettargetSdkVersion(Context context) {
    PackageManager packageManager = null;
    ApplicationInfo applicationInfo = null;
    try {
      packageManager = context.getApplicationContext().getPackageManager();
      applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      applicationInfo = null;
    }
    int targetSdkVersion = applicationInfo.targetSdkVersion;
    return targetSdkVersion;
  }
  public static String getSubtypeName(Context context) {
    ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info2 = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    return info2.getSubtypeName();
  }

  public static String getSimCountryIso(Context context) {
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    String countryIso = tm.getSimCountryIso();
    return countryIso;
  }

  // 返回ISO标准的国家码，即国际长途区号
  public static String getNetworkCountryIso(Context context) {
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    return tm.getNetworkCountryIso();
  }

  // 返回移动网络运营商的名字(SPN)
  public static String getNetworkOperatorName(Context context) {
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    return tm.getNetworkOperatorName();
  }

  // 获取屏幕Density
  public static float getDensity(Context context) {
    DisplayMetrics dm = new DisplayMetrics();
    dm = context.getApplicationContext().getResources().getDisplayMetrics();
    return dm.density;
  }

  /** 查询手机的 MCC+MNC */
  private static String getSimOperator(Context context) {
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    try {
      return tm.getSimOperator();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  // 获取内核版本
  public static String getKernelVersion() {
    String kernelVersion = "";
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream("/proc/version");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return kernelVersion;
    }
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
    String info = "";
    String line = "";
    try {
      while ((line = bufferedReader.readLine()) != null) {
        info += line;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        bufferedReader.close();
        inputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    try {
      if (info != "") {
        final String keyword = "version ";
        int index = info.indexOf(keyword);
        line = info.substring(index + keyword.length());
        index = line.indexOf(" ");
        kernelVersion = line.substring(0, index);
      }
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }

    return kernelVersion;
  }

  public static boolean isWiFiActive(Context context) {
    ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivity != null) {
      NetworkInfo[] info = connectivity.getAllNetworkInfo();
      if (info != null) {
        for (int i = 0; i < info.length; i++) {
          if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
