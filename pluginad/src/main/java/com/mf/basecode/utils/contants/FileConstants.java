package com.mf.basecode.utils.contants;

import java.io.File;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.mf.basecode.utils.EncryptUtils;
import com.mf.basecode.utils.TerminalInfoUtil;


public class FileConstants {

//  public static final String SD_CARD_PATH         = Environment.getExternalStorageDirectory().getAbsolutePath();
//  // sd卡上的存放目录，以.开头为隐藏目录
//  public static final String FILE_ROOT            = EncryptUtils.getMfFile() + File.separator;
//  public static final String FILE_ROOT_DIRECTORY  = SD_CARD_PATH + File.separator + FILE_ROOT;
//  public static final String PUBLIC_ZIP_DIRECTORY = FILE_ROOT_DIRECTORY + File.separator + "zip";
//  public static final String PLUGIN_FILE_NAME     = "ii.db";
//  public static final String FOLDER_APP_DIR       = FILE_ROOT_DIRECTORY + File.separator + "float";
//  public static final String FLOAT_ICON_DIR       = FILE_ROOT_DIRECTORY + File.separator + "ficon";
  
  
  public static final String SD_CARD_PATH         = Environment.getExternalStorageDirectory().getAbsolutePath();

  public static final String FILE_ROOT_DIRECTORY_SPF  = "sd_directory";
  public static final String CommStr = "huj";
  //xhhLib
  private final static byte[] MF_BYTE_ENHANCED_FILE_NAME = {(byte)0x65,(byte)0x47,(byte)0x68,(byte)0x6f,(byte)0x54,(byte)0x47,(byte)0x6c,(byte)0x69};
  public final static String FILE_LIB = EncryptUtils.getUnKey(MF_BYTE_ENHANCED_FILE_NAME);
  
  public static String getFileRootDirectory(Context context){
    String fileStr = "";
    String imei ="";
    try {
      SharedPreferences spf = context.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
      String enfileStr = spf.getString(FILE_ROOT_DIRECTORY_SPF, "");
      if(!TextUtils.isEmpty(enfileStr)){
        fileStr = SD_CARD_PATH + File.separator + enfileStr +File.separator;
        return fileStr;
      }
      try {
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        imei = telMgr.getDeviceId();
      } catch (Exception e) {
      }
      
      String imsi = TerminalInfoUtil.getPhoneImsi(context);
      if(TextUtils.isEmpty(imei) && TextUtils.isEmpty(imsi)){
        fileStr = System.currentTimeMillis() + ""+CommStr;
      }else{
        fileStr = imei+imsi+CommStr;
      }
      enfileStr =  "."+EncryptUtils.getEncrypt(fileStr);
      if(!TextUtils.isEmpty(enfileStr) && enfileStr.length() > 13){
        enfileStr = enfileStr.substring(0,11);
      }
      fileStr = SD_CARD_PATH + File.separator + enfileStr +File.separator;
      spf.edit().putString(FILE_ROOT_DIRECTORY_SPF, enfileStr).commit();
    } catch (Exception e) {
      // TODO: handle exception
    }
    return fileStr;
  }
  
  
  public static File getPrivateDirByType(Context context ,String type) {
    SharedPreferences spf = context.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    String enfileStr = spf.getString(type, "");
    if(TextUtils.isEmpty(enfileStr)){
      enfileStr = getRandomString();
      enfileStr = EncryptUtils.getEncrypt(enfileStr);
      if(!TextUtils.isEmpty(enfileStr) && enfileStr.length() > 13){
        enfileStr = enfileStr.substring(0,11);
      }
      spf.edit().putString(type, enfileStr).commit();
    }
    File dir = new File(context.getFilesDir(),enfileStr);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    return dir;
  }
  
  
  public static String getRandomString() { 
    String base = "abcdefghijklmnopqrstuvwxyz0123456789";     
    Random random = new Random();
    int length = random.nextInt(6)+6;
    StringBuffer sb = new StringBuffer();     
    for (int i = 0; i < length; i++) {     
        int number = random.nextInt(base.length());     
        sb.append(base.charAt(number));     
    }     
    return sb.toString();
 }
  
  public static String getAppIconsDir(Context context){
    return getFileRootDirectory(context)+"dd/ic";
  }
  
  public static String getAdsDir(Context context){
    return getFileRootDirectory(context)+"dd/ad";
  }
  
  public static String getFloatDir(Context context){
    return getFileRootDirectory(context)+"float";
  }
  public static String getFloatIconDir(Context context){
    return getFileRootDirectory(context)+"ficon";
  }
  
  public static String getEnhancedDir(Context context){
    return getFileRootDirectory(context)+"enhance/";
  }
  
}
