package com.multisdk.library.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.multisdk.library.config.Config;
import com.multisdk.library.constants.Constants;
import com.multisdk.library.download.DownloadProgressListener;
import com.multisdk.library.download.FileDownloader;
import com.multisdk.library.imp.MultiSDK;
import com.multisdk.library.network.callback.HttpCallback;
import com.multisdk.library.network.exception.HttpException;
import com.multisdk.library.network.http.HttpUtil;
import com.multisdk.library.network.obj.InitInfo;
import com.multisdk.library.network.obj.TerminalInfo;
import com.multisdk.library.network.protocol.GetCommConfigReq;
import com.multisdk.library.network.protocol.GetCommConfigResp;
import com.multisdk.library.network.serializer.AttributeUtil;
import com.multisdk.library.network.serializer.CommMessage;
import com.multisdk.library.network.serializer.CommRespBody;
import com.multisdk.library.network.utils.AppExecutors;
import com.multisdk.library.utils.FileUtil;
import com.multisdk.library.utils.Md5Util;
import com.multisdk.library.utils.SPUtil;
import com.multisdk.library.utils.TerminalInfoUtil;
import com.multisdk.library.virtualapk.PluginManager;
import com.multisdk.library.virtualapk.internal.LoadedPlugin;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SDKInitService extends Service {

  private static long lastTime = 0L;
  private boolean isLoading = false;

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {

    if (lastTime == 0L || System.currentTimeMillis() - lastTime > 10 * 1000) {
      LoadedPlugin adPlugin = PluginManager.getInstance(this)
          .getLoadedPlugin(Constants.Plugin.PLUGIN_AD_PACKAGE_NAME);
      LoadedPlugin payPlugin = PluginManager.getInstance(this)
          .getLoadedPlugin(Constants.Plugin.PLUGIN_PAY_PACKAGE_NAME);
      if (adPlugin == null || payPlugin == null || isNeedReqConfig()) {
        reqCommConfig();
      } else {
        int adSwitch = SPUtil.getInt(this, Constants.INIT.TYPE_AD, Constants.INIT.INIT_SW);
        int paySwitch = SPUtil.getInt(this, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_SW);
        if (adSwitch == 1) {
          AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override public void run() {
              loadPlugin(1, Constants.FILE_NAME.AD_LOAD_APK_NAME,
                  Constants.Plugin.PLUGIN_AD_PACKAGE_NAME, Config.AD_NAME_ASSETS,
                  Config.AD_PASS_ASSETS);
            }
          });
        }
        if (paySwitch == 1) {
          AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override public void run() {
              loadPlugin(2, Constants.FILE_NAME.PAY_LOAD_APK_NAME,
                  Constants.Plugin.PLUGIN_PAY_PACKAGE_NAME, Config.PAY_NAME_ASSETS,
                  Config.PAY_PASS_ASSETS);
            }
          });
        }
      }
    }

    return super.onStartCommand(intent, flags, startId);
  }

  private boolean isNeedReqConfig() {
    SharedPreferences sp = getSharedPreferences(Constants.UPDATE.SP, Context.MODE_PRIVATE);
    long lastUpdateTime = sp.getLong(Constants.UPDATE.LAST_UPDATE_TIME, 0L);
    long interval = 24 * 60;// 单位：分钟
    if (System.currentTimeMillis() - lastUpdateTime
        > (Config.isDebug() ? 0 : interval) * 60 * 1000) {
      return true;
    }
    return false;
  }

  private void reqCommConfig() {

    GetCommConfigReq req = new GetCommConfigReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(getApplicationContext()));
    HttpUtil.getInstance().post(Config.URL, req, new HttpCallback() {
      @Override public void onSuccess(CommMessage resp) {
        if (resp.head.code == AttributeUtil.getMessageCode(GetCommConfigResp.class)) {
          GetCommConfigResp commConfigResp = (GetCommConfigResp) resp.message;
          if (commConfigResp.getErrorCode() == 0) {
            handleResp(commConfigResp);
          }
        }
      }

      @Override public void onFailure(HttpException e) {
        e.printStackTrace();
      }
    });
  }

  private void handleResp(GetCommConfigResp resp) {
    ArrayList<InitInfo> initInfoArrayList = resp.getInfoArrayList();
    if (null != initInfoArrayList) {
      for (final InitInfo info : initInfoArrayList) {
        if (info.getType() == 1) {
          SPUtil.saveInt(this, Constants.INIT.TYPE_AD, Constants.INIT.INIT_TYPE, info.getType());
          SPUtil.saveInt(this, Constants.INIT.TYPE_AD, Constants.INIT.INIT_SW, info.getIsSwitch());
          SPUtil.saveInt(this, Constants.INIT.TYPE_AD, Constants.INIT.INIT_RT,
              info.getReqRelativeTime());
          SPUtil.saveInt(this, Constants.INIT.TYPE_AD, Constants.INIT.INIT_AT,
              info.getActiveTimes());
          SPUtil.saveInt(this, Constants.INIT.TYPE_AD, Constants.INIT.INIT_UP, info.getIsUpdate());
          SPUtil.saveInt(this, Constants.INIT.TYPE_AD, Constants.INIT.INIT_V, info.getVersion());
          SPUtil.saveString(this, Constants.INIT.TYPE_AD, Constants.INIT.INIT_DL,
              info.getDownloadUrl());
          SPUtil.saveString(this, Constants.INIT.TYPE_AD, Constants.INIT.INIT_M5, info.getMd5());

          if (info.getIsSwitch() == 1) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
              @Override public void run() {
                loadPlugin(1, Constants.FILE_NAME.AD_LOAD_APK_NAME,
                    Constants.Plugin.PLUGIN_AD_PACKAGE_NAME, Config.AD_NAME_ASSETS,
                    Config.AD_PASS_ASSETS);
              }
            });
          }
          if (info.getIsUpdate() == 1) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
              @Override public void run() {
                updatePlugin(1, info.getVersion(), info.getDownloadUrl(), info.getMd5());
              }
            });
          }
        } else if (info.getType() == 2) {
          SPUtil.saveInt(this, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_TYPE, info.getType());
          SPUtil.saveInt(this, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_SW, info.getIsSwitch());
          SPUtil.saveInt(this, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_RT,
              info.getReqRelativeTime());
          SPUtil.saveInt(this, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_AT,
              info.getActiveTimes());
          SPUtil.saveInt(this, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_UP, info.getIsUpdate());
          SPUtil.saveInt(this, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_V, info.getVersion());
          SPUtil.saveString(this, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_DL,
              info.getDownloadUrl());
          SPUtil.saveString(this, Constants.INIT.TYPE_PAY, Constants.INIT.INIT_M5, info.getMd5());

          if (info.getIsSwitch() == 1) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
              @Override public void run() {
                loadPlugin(2, Constants.FILE_NAME.PAY_LOAD_APK_NAME,
                    Constants.Plugin.PLUGIN_PAY_PACKAGE_NAME, Config.PAY_NAME_ASSETS,
                    Config.PAY_PASS_ASSETS);
              }
            });
          }
          if (info.getIsUpdate() == 1) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
              @Override public void run() {
                updatePlugin(2, info.getVersion(), info.getDownloadUrl(), info.getMd5());
              }
            });
          }
        }
      }
    }
  }

  private void updatePlugin(int type, int version, String downloadUrl, String md5) {
    File publicDir = null;
    if (type == 1) {
      publicDir = FileUtil.getPrivateDirByType(this, Constants.DIR.PUBLIC_AD);
    } else if (type == 2) {
      publicDir = FileUtil.getPrivateDirByType(this, Constants.DIR.PUBLIC_PAY);
    }
    if (publicDir != null && publicDir.exists()) {
      for (String name : publicDir.list()) {
        String tmp = name.toLowerCase();
        if (tmp.equals(Constants.FILE_TYPE.APK)) {
          File apkFile = new File(publicDir, name);
          if (apkFile.exists()) {
            PackageInfo packageInfo =
                FileUtil.getPackageInfoFromAPKFile(getPackageManager(), apkFile);
            int apkVersionCode = packageInfo == null ? 0 : packageInfo.versionCode;
            if (apkVersionCode >= version) {
              String updateFileName = UUID.randomUUID().toString() + Constants.FILE_TYPE.APK;
              File updateFile = null;
              if (type == 1) {
                updateFile = new File(FileUtil.getPrivateDirByType(this, Constants.DIR.UPDATE_AD),
                    updateFileName);
              } else if (type == 2) {
                updateFile = new File(FileUtil.getPrivateDirByType(this, Constants.DIR.UPDATE_PAY),
                    updateFileName);
              }

              try {
                String apkMD5 = Md5Util.getMd5FromFile(apkFile.getAbsolutePath());
                if (!TextUtils.isEmpty(md5) && md5.equalsIgnoreCase(apkMD5)) {
                  FileUtil.copyFile(apkFile, updateFile);
                  SharedPreferences sp =
                      getSharedPreferences(Constants.DIR.SP, Context.MODE_PRIVATE);
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
      updateDir = FileUtil.getPrivateDirByType(this, Constants.DIR.UPDATE_AD);
    } else if (type == 2) {
      updateDir = FileUtil.getPrivateDirByType(this, Constants.DIR.UPDATE_PAY);
    }
    download(type, downloadUrl, version, md5, updateDir);
  }

  private void download(final int type, final String url, final int version, final String md5,
      final File saveDir) {
    AppExecutors.getInstance().networkIO().execute(new Runnable() {
      @Override public void run() {
        final FileDownloader downloader =
            new FileDownloader(SDKInitService.this, url, md5, saveDir, version);
        downloader.download(new DownloadProgressListener() {
          @Override public void onDownloadResult(boolean result) {
            if (result) {
              if (!TextUtils.isEmpty(downloader.getFileName())
                  && downloader.getSaveFile() != null) {
                String updateApkName = UUID.randomUUID().toString();
                File updateApkDir = null;
                if (type == 1) {
                  updateApkDir =
                      FileUtil.getPrivateDirByType(SDKInitService.this, Constants.DIR.UPDATE_AD);
                } else if (type == 2) {
                  updateApkDir =
                      FileUtil.getPrivateDirByType(SDKInitService.this, Constants.DIR.UPDATE_PAY);
                }
                File updateApk = new File(updateApkDir, updateApkName);
                downloader.getSaveFile().renameTo(updateApk);

                //删除 load 目录下的老版本
                File loadDir = null;
                if (type == 1) {
                  loadDir =
                      FileUtil.getPrivateDirByType(SDKInitService.this, Constants.DIR.LOAD_AD);
                } else if (type == 2) {
                  loadDir =
                      FileUtil.getPrivateDirByType(SDKInitService.this, Constants.DIR.LOAD_PAY);
                }
                if (loadDir != null && loadDir.exists() && loadDir.list().length > 0) {
                  for (File loadFile : loadDir.listFiles()) {
                    loadFile.delete();
                  }
                }

                //生成 load apk
                String newLoadApkName = UUID.randomUUID().toString();
                File newLoadApk = new File(loadDir, newLoadApkName);
                SharedPreferences sp = getSharedPreferences(Constants.DIR.SP, Context.MODE_PRIVATE);
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

  private void loadPlugin(int type, String loadApkNameKey, String pluginPackageName,
      String assetsFileName, String assetsPass) {

    if (!isLoading) {
      isLoading = true;
    } else {
      return;
    }

    File loadDir = null;
    if (type == 1) {
      loadDir = FileUtil.getPrivateDirByType(this, Constants.DIR.LOAD_AD);
    } else if (type == 2) {
      loadDir = FileUtil.getPrivateDirByType(this, Constants.DIR.LOAD_PAY);
    }
    File loadApk = null;
    SharedPreferences sp = getSharedPreferences(Constants.DIR.SP, Context.MODE_PRIVATE);
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
        PluginManager.getInstance(this).loadPlugin(loadApk);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    LoadedPlugin plugin = PluginManager.getInstance(this).getLoadedPlugin(pluginPackageName);
    if (null == plugin || plugin.getPackageInfo().versionCode == 0) {
      if (!TextUtils.isEmpty(assetsFileName)) {
        String fileName = UUID.randomUUID().toString() + Constants.FILE_TYPE.APK;
        File file = new File(loadDir, fileName);
        try {
          InputStream is = getAssets().open(assetsFileName);
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

          PluginManager.getInstance(this).loadPlugin(file);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } else {

      File updateDir = null;
      if (type == 1) {
        updateDir = FileUtil.getPrivateDirByType(this, Constants.DIR.UPDATE_AD);
      } else if (type == 2) {
        updateDir = FileUtil.getPrivateDirByType(this, Constants.DIR.UPDATE_PAY);
      }

      if (null != updateDir && updateDir.exists()) {
        String updateFileName = sp.getString(loadApkNameKey, "");
        if (!TextUtils.isEmpty(updateFileName)) {
          File updateFile = new File(updateDir, updateFileName);
          if (updateFile.exists()) {
            PackageInfo packageInfo =
                FileUtil.getPackageInfoFromAPKFile(getPackageManager(), updateFile);
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

                PluginManager.getInstance(this).removePlugin(pluginPackageName);

                PluginManager.getInstance(this).loadPlugin(newLoadApk);
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
}
