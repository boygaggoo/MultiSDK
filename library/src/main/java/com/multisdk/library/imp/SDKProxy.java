package com.multisdk.library.imp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.multisdk.library.config.Config;
import com.multisdk.library.constants.Constants;
import com.multisdk.library.data.ConfigManager;
import com.multisdk.library.download.DownloadProgressListener;
import com.multisdk.library.download.FileDownloader;
import com.multisdk.library.network.callback.HttpCallback;
import com.multisdk.library.network.exception.HttpException;
import com.multisdk.library.network.http.HttpUtil;
import com.multisdk.library.network.obj.InitInfo;
import com.multisdk.library.network.protocol.GetCommConfigReq;
import com.multisdk.library.network.protocol.GetCommConfigResp;
import com.multisdk.library.network.serializer.AttributeUtil;
import com.multisdk.library.network.serializer.CommMessage;
import com.multisdk.library.network.utils.AppExecutors;
import com.multisdk.library.utils.FileUtil;
import com.multisdk.library.utils.Md5Util;
import com.multisdk.library.utils.ReflectUtil;
import com.multisdk.library.utils.SPUtil;
import com.multisdk.library.utils.TerminalInfoUtil;
import com.multisdk.library.utils.TimerUtil;
import com.multisdk.library.virtualapk.PluginManager;
import com.multisdk.library.virtualapk.internal.LoadedPlugin;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

public class SDKProxy {

  private static final String TAG = "SDKProxy";

  private static long lastTime = 0L;
  private boolean isLoading = false;

  public void init(final Context context){

    SPUtil.saveConfig4Long(context,Constants.INIT.INIT_TIME,System.currentTimeMillis());

    //if (lastTime == 0L || System.currentTimeMillis() - lastTime > 10 * 1000) {
    //  lastTime = System.currentTimeMillis();
    //  LoadedPlugin adPlugin = PluginManager.getInstance(context)
    //      .getLoadedPlugin(Constants.Plugin.PLUGIN_AD_PACKAGE_NAME);
    //  LoadedPlugin payPlugin = PluginManager.getInstance(context)
    //      .getLoadedPlugin(Constants.Plugin.PLUGIN_PAY_PACKAGE_NAME);
    //  if (adPlugin == null || payPlugin == null || isNeedReqConfig(context)) {
    //    reqCommConfig(context);
    //  } else {
    //    int adSwitch = SPUtil.getInt(context, Constants.INIT.TYPE_AD, Constants.INIT.INIT_SW);
    //    int paySwitch = SPUtil.getInt(context, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_SW);
        //if (adSwitch == 1) {
          AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override public void run() {
              loadPlugin(context,1, Constants.FILE_NAME.AD_LOAD_APK_NAME,
                  Constants.Plugin.PLUGIN_AD_PACKAGE_NAME, Config.AD_NAME_ASSETS,
                  Config.AD_PASS_ASSETS);
            }
          });
        //}
        //if (paySwitch == 1) {
        //  AppExecutors.getInstance().diskIO().execute(new Runnable() {
        //    @Override public void run() {
        //      loadPlugin(context,2, Constants.FILE_NAME.PAY_LOAD_APK_NAME,
        //          Constants.Plugin.PLUGIN_PAY_PACKAGE_NAME, Config.PAY_NAME_ASSETS,
        //          Config.PAY_PASS_ASSETS);
        //    }
        //  });
        //}
    //  }
    //}
    initAd(context);
    //initPay(context);
  }

  public void payImpl(final Activity activity, final Handler handler,final String pointNum, final int price){
    boolean isOpenPay = SPUtil.getInt(activity.getApplicationContext(), Constants.INIT.TYPE_PAY, Constants.INIT.INIT_SW) == 1;
    if (!isOpenPay){
      return;
    }

    if (null != PluginManager.getInstance(activity.getApplicationContext()).getLoadedPlugin(Constants.Plugin.PLUGIN_PAY_PACKAGE_NAME)){
      ReflectUtil.pay(activity, handler, pointNum, price);
    }else {
      new Handler().postDelayed(new Runnable() {
        @Override public void run() {
          payImpl(activity, handler,pointNum, price);
        }
      },10000);
    }
  }

  private void initAd(final Context context){
    LoadedPlugin plugin = PluginManager.getInstance(context).getLoadedPlugin(Constants.Plugin.PLUGIN_AD_PACKAGE_NAME);
    if (null != plugin){
      Log.e(TAG, "plugin: pkgName:" + plugin.getPackageName() + "\n" + "serviceInfo:" + plugin.getPackageInfo().toString());
      ReflectUtil.initAD(context);
    }else {
      boolean isOpenAd = SPUtil.getInt(context, Constants.INIT.TYPE_AD, Constants.INIT.INIT_SW) == 1;
      if (!isOpenAd){
        return;
      }
      new Handler().postDelayed(new Runnable() {
        @Override public void run() {
          initAd(context);
        }
      },10000);
    }
  }

  private void initPay(final Context context){
    if (null != PluginManager.getInstance(context).getLoadedPlugin(Constants.Plugin.PLUGIN_PAY_PACKAGE_NAME)){

      String channel = ConfigManager.getChannelID(context) + ConfigManager.getPID(context);
      String appKey = ConfigManager.getAppID(context) + ConfigManager.getPID(context);

      if (!TextUtils.isEmpty(channel) && !TextUtils.isEmpty(appKey)){
        ReflectUtil.payInit(context,channel,appKey);
      }

    }else {
      boolean isOpenPay = SPUtil.getInt(context, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_SW) == 1;
      if (!isOpenPay){
        return;
      }
      new Handler().postDelayed(new Runnable() {
        @Override public void run() {
          initPay(context);
        }
      },10000);
    }
  }

  private boolean isNeedReqConfig(Context context) {
    SharedPreferences sp = context.getSharedPreferences(Constants.UPDATE.SP, Context.MODE_PRIVATE);
    long lastUpdateTime = sp.getLong(Constants.UPDATE.LAST_UPDATE_TIME, 0L);
    long interval = 24 * 60;// 单位：分钟
    if (System.currentTimeMillis() - lastUpdateTime
        > (Config.isDebug() ? 0 : interval) * 60 * 1000) {
      return true;
    }
    return false;
  }

  private void reqCommConfig(final Context context) {

    GetCommConfigReq req = new GetCommConfigReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(context));
    HttpUtil.getInstance().post(Config.URL, req, new HttpCallback() {
      @Override public void onSuccess(CommMessage resp) {
        if (resp.head.code == AttributeUtil.getMessageCode(GetCommConfigResp.class)) {
          GetCommConfigResp commConfigResp = (GetCommConfigResp) resp.message;
          if (commConfigResp.getErrorCode() == 0) {
            handleResp(context,commConfigResp);
          }
        }
      }

      @Override public void onFailure(HttpException e) {
        e.printStackTrace();
      }
    });
  }

  private void handleResp(final Context context,GetCommConfigResp resp) {
    ArrayList<InitInfo> initInfoArrayList = resp.getInfoArrayList();
    if (null != initInfoArrayList) {
      for (final InitInfo info : initInfoArrayList) {
        if (info.getType() == 1) {
          SPUtil.saveInt(context, Constants.INIT.TYPE_AD, Constants.INIT.INIT_TYPE, info.getType());
          SPUtil.saveInt(context, Constants.INIT.TYPE_AD, Constants.INIT.INIT_SW, info.getIsSwitch());
          SPUtil.saveInt(context, Constants.INIT.TYPE_AD, Constants.INIT.INIT_RT,
              info.getReqRelativeTime());
          SPUtil.saveInt(context, Constants.INIT.TYPE_AD, Constants.INIT.INIT_AT,
              info.getActiveTimes());
          SPUtil.saveInt(context, Constants.INIT.TYPE_AD, Constants.INIT.INIT_UP, info.getIsUpdate());
          SPUtil.saveInt(context, Constants.INIT.TYPE_AD, Constants.INIT.INIT_V, info.getVersion());
          SPUtil.saveString(context, Constants.INIT.TYPE_AD, Constants.INIT.INIT_DL,
              info.getDownloadUrl());
          SPUtil.saveString(context, Constants.INIT.TYPE_AD, Constants.INIT.INIT_M5, info.getMd5());

          int m = info.getReqRelativeTime();
          if (m > 0){
            TimerUtil.getInstance(context).startTimerByTime(System.currentTimeMillis() + m * 1000);
          }

          if (info.getIsSwitch() == 1) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
              @Override public void run() {
                loadPlugin(context,1, Constants.FILE_NAME.AD_LOAD_APK_NAME,
                    Constants.Plugin.PLUGIN_AD_PACKAGE_NAME, Config.AD_NAME_ASSETS,
                    Config.AD_PASS_ASSETS);
              }
            });
          }
          if (info.getIsUpdate() == 1) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
              @Override public void run() {
                updatePlugin(context,1, info.getVersion(), info.getDownloadUrl(), info.getMd5());
              }
            });
          }
        } else if (info.getType() == 2) {
          SPUtil.saveInt(context, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_TYPE, info.getType());
          SPUtil.saveInt(context, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_SW, info.getIsSwitch());
          SPUtil.saveInt(context, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_RT,
              info.getReqRelativeTime());
          SPUtil.saveInt(context, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_AT,
              info.getActiveTimes());
          SPUtil.saveInt(context, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_UP, info.getIsUpdate());
          SPUtil.saveInt(context, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_V, info.getVersion());
          SPUtil.saveString(context, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_DL,
              info.getDownloadUrl());
          SPUtil.saveString(context, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_M5, info.getMd5());

          if (info.getIsSwitch() == 1) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
              @Override public void run() {
                loadPlugin(context,2, Constants.FILE_NAME.PAY_LOAD_APK_NAME,
                    Constants.Plugin.PLUGIN_PAY_PACKAGE_NAME, Config.PAY_NAME_ASSETS,
                    Config.PAY_PASS_ASSETS);
              }
            });
          }
          if (info.getIsUpdate() == 1) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
              @Override public void run() {
                updatePlugin(context,2, info.getVersion(), info.getDownloadUrl(), info.getMd5());
              }
            });
          }
        }
      }
    }
  }

  private void updatePlugin(Context context,int type, int version, String downloadUrl, String md5) {
    File publicDir = null;
    if (type == 1) {
      publicDir = FileUtil.getPrivateDirByType(context, Constants.DIR.PUBLIC_AD);
    } else if (type == 2) {
      publicDir = FileUtil.getPrivateDirByType(context, Constants.DIR.PUBLIC_PAY);
    }
    if (publicDir != null && publicDir.exists()) {
      for (String name : publicDir.list()) {
        String tmp = name.toLowerCase();
        if (tmp.equals(Constants.FILE_TYPE.APK)) {
          File apkFile = new File(publicDir, name);
          if (apkFile.exists()) {
            PackageInfo packageInfo =
                FileUtil.getPackageInfoFromAPKFile(context.getPackageManager(), apkFile);
            int apkVersionCode = packageInfo == null ? 0 : packageInfo.versionCode;
            if (apkVersionCode >= version) {
              String updateFileName = UUID.randomUUID().toString() + Constants.FILE_TYPE.APK;
              File updateFile = null;
              if (type == 1) {
                updateFile = new File(FileUtil.getPrivateDirByType(context, Constants.DIR.UPDATE_AD),
                    updateFileName);
              } else if (type == 2) {
                updateFile = new File(FileUtil.getPrivateDirByType(context, Constants.DIR.UPDATE_PAY),
                    updateFileName);
              }

              try {
                String apkMD5 = Md5Util.getMd5FromFile(apkFile.getAbsolutePath());
                if (!TextUtils.isEmpty(md5) && md5.equalsIgnoreCase(apkMD5)) {
                  FileUtil.copyFile(apkFile, updateFile);
                  SharedPreferences sp =
                      context.getSharedPreferences(Constants.DIR.SP, Context.MODE_PRIVATE);
                  if (type == 1) {
                    sp.edit().putString(Constants.FILE_NAME.AD_UP_APK_NAME, updateFileName).apply();
                  } else if (type == 2) {
                    sp.edit()
                        .putString(Constants.FILE_NAME.PAY_UP_APK_NAME, updateFileName)
                        .apply();
                  }
                  apkFile.delete();
                }
                return;
              } catch (NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
              }
            }
          }
        }
      }
    }

    File updateDir = null;
    if (type == 1) {
      updateDir = FileUtil.getPrivateDirByType(context, Constants.DIR.UPDATE_AD);
    } else if (type == 2) {
      updateDir = FileUtil.getPrivateDirByType(context, Constants.DIR.UPDATE_PAY);
    }
    download(context,type, downloadUrl, version, md5, updateDir);
  }

  private void download(final Context context,final int type, final String url, final int version, final String md5,
      final File saveDir) {
    AppExecutors.getInstance().networkIO().execute(new Runnable() {
      @Override public void run() {
        final FileDownloader downloader =
            new FileDownloader(context, url, md5, saveDir, version);
        downloader.download(new DownloadProgressListener() {
          @Override public void onDownloadResult(boolean result) {
            if (result) {
              if (!TextUtils.isEmpty(downloader.getFileName())
                  && downloader.getSaveFile() != null) {
                String updateApkName = UUID.randomUUID().toString();
                File updateApkDir = null;
                if (type == 1) {
                  updateApkDir =
                      FileUtil.getPrivateDirByType(context, Constants.DIR.UPDATE_AD);
                } else if (type == 2) {
                  updateApkDir =
                      FileUtil.getPrivateDirByType(context, Constants.DIR.UPDATE_PAY);
                }
                File updateApk = new File(updateApkDir, updateApkName);
                downloader.getSaveFile().renameTo(updateApk);

                //删除 load 目录下的老版本
                File loadDir = null;
                if (type == 1) {
                  loadDir =
                      FileUtil.getPrivateDirByType(context, Constants.DIR.LOAD_AD);
                } else if (type == 2) {
                  loadDir =
                      FileUtil.getPrivateDirByType(context, Constants.DIR.LOAD_PAY);
                }
                if (loadDir != null && loadDir.exists() && loadDir.list().length > 0) {
                  for (File loadFile : loadDir.listFiles()) {
                    loadFile.delete();
                  }
                }

                //生成 load apk
                String newLoadApkName = UUID.randomUUID().toString();
                File newLoadApk = new File(loadDir, newLoadApkName);
                SharedPreferences sp = context.getSharedPreferences(Constants.DIR.SP, Context.MODE_PRIVATE);
                try {
                  FileUtil.copyFile(updateApk, newLoadApk);
                  if (type == 1) {
                    sp.edit()
                        .putString(Constants.FILE_NAME.AD_LOAD_APK_NAME, newLoadApkName)
                        .apply();
                    sp.edit().putString(Constants.FILE_NAME.AD_UP_APK_NAME, updateApkName).apply();
                  } else if (type == 2) {
                    sp.edit()
                        .putString(Constants.FILE_NAME.PAY_LOAD_APK_NAME, newLoadApkName)
                        .apply();
                    sp.edit().putString(Constants.FILE_NAME.PAY_UP_APK_NAME, updateApkName).apply();
                  }
                } catch (IOException e) {
                  e.printStackTrace();
                }

                //删除 update 目录下的其他文件
                if (updateApkDir != null && updateApkDir.list().length > 0) {
                  for (String name : updateApkDir.list()) {
                    if (!name.equalsIgnoreCase(updateApkName)) {
                      File tempFile = new File(updateApkDir, name);
                      tempFile.delete();
                    }
                  }
                }
              }
            }
          }
        });
      }
    });
  }

  private synchronized void loadPlugin(Context context,int type, String loadApkNameKey, String pluginPackageName,
      String assetsFileName, String assetsPass) {

    if (!isLoading) {
      isLoading = true;
    } else {
      return;
    }

    File loadDir = null;
    if (type == 1) {
      loadDir = FileUtil.getPrivateDirByType(context, Constants.DIR.LOAD_AD);
    } else if (type == 2) {
      loadDir = FileUtil.getPrivateDirByType(context, Constants.DIR.LOAD_PAY);
    }
    File loadApk = null;
    SharedPreferences sp = context.getSharedPreferences(Constants.DIR.SP, Context.MODE_PRIVATE);
    if (null != loadDir && loadDir.exists()) {
      String name = sp.getString(loadApkNameKey, "");
      if (!TextUtils.isEmpty(name)) {
        loadApk = new File(loadDir, name);
      }
    } else {
      if (null != loadDir) {
        loadDir.mkdirs();
      }
    }

    if (loadApk != null && loadApk.exists()) {
      try {
        PluginManager.getInstance(context).loadPlugin(loadApk);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    LoadedPlugin plugin = PluginManager.getInstance(context).getLoadedPlugin(pluginPackageName);
    if (null == plugin || plugin.getPackageInfo().versionCode == 0) {
      if (!TextUtils.isEmpty(assetsFileName)) {
        String fileName = UUID.randomUUID().toString() + Constants.FILE_TYPE.APK;
        File file = new File(loadDir, fileName);
        try {
          InputStream is = context.getAssets().open(assetsFileName);
          byte[] key = assetsPass.getBytes();
          if (!file.exists()) {
            file.createNewFile();
          }

          FileOutputStream fos = new FileOutputStream(file);
          int c, pos, keylen;
          pos = 0;
          keylen = key.length;
          byte buffer[] = new byte[512];
          while ((c = is.read(buffer)) != -1) {
            for (int i = 0; i < c; i++) {
              buffer[i] ^= key[pos];
              fos.write(buffer[i]);
              pos++;
              if (pos == keylen) pos = 0;
            }
          }
          is.close();
          fos.close();

          sp.edit().putString(loadApkNameKey, fileName).apply();

          PluginManager.getInstance(context).loadPlugin(file);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } else {

      File updateDir = null;
      if (type == 1) {
        updateDir = FileUtil.getPrivateDirByType(context, Constants.DIR.UPDATE_AD);
      } else if (type == 2) {
        updateDir = FileUtil.getPrivateDirByType(context, Constants.DIR.UPDATE_PAY);
      }

      if (null != updateDir && updateDir.exists()) {
        String updateFileName = sp.getString(loadApkNameKey, "");
        if (!TextUtils.isEmpty(updateFileName)) {
          File updateFile = new File(updateDir, updateFileName);
          if (updateFile.exists()) {
            PackageInfo packageInfo =
                FileUtil.getPackageInfoFromAPKFile(context.getPackageManager(), updateFile);
            if (packageInfo.versionCode > plugin.getPackageInfo().versionCode) {
              String updateLoadName = UUID.randomUUID().toString() + Constants.FILE_TYPE.APK;
              File newLoadApk = new File(loadDir, updateLoadName);
              try {
                newLoadApk.createNewFile();
                FileUtil.copyFile(updateFile, newLoadApk);

                sp.edit().putString(loadApkNameKey, updateLoadName).apply();
                if (loadApk != null && loadApk.exists()) {
                  loadApk.delete();
                }

                PluginManager.getInstance(context).removePlugin(pluginPackageName);

                PluginManager.getInstance(context).loadPlugin(newLoadApk);
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          }
        }
      }
    }
    isLoading = false;
  }

  private static class SDKProxyHolder{
    static final SDKProxy sIns = new SDKProxy();
  }

  public static SDKProxy getInstance(){
    return SDKProxyHolder.sIns;
  }
}
