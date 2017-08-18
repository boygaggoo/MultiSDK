package com.multisdk.library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.multisdk.library.constants.Constants;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class FileUtil {

  public static File getPrivateDirByType(Context context,String type){
    SharedPreferences sp = context.getSharedPreferences(Constants.DIR.SP,Context.MODE_PRIVATE);
    String enStr = sp.getString(type,"");
    if (TextUtils.isEmpty(enStr)){
      enStr = getRandomString();
      if (!TextUtils.isEmpty(enStr) && enStr.length() > 13){
        enStr = enStr.substring(0,11);
      }
      sp.edit().putString(type,enStr).apply();
    }
    return new File(context.getFilesDir(),enStr);
  }

  private static String getRandomString() {
    String base = "abcdefghijklmnopqrstuvwxyz";
    Random random = new Random();
    int length = random.nextInt(6)+6;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int number = random.nextInt(base.length());
      sb.append(base.charAt(number));
    }
    return sb.toString();
  }

  public static PackageInfo getPackageInfoFromAPKFile(PackageManager packageManager, File apkFile) {
    return packageManager.getPackageArchiveInfo(apkFile.getAbsolutePath(), PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
  }

  /**
   * 复制文件
   *
   * @param sourceFile
   *          源文件
   * @param targetFile
   *          输出文件
   */
  public static void copyFile(File sourceFile, File targetFile) throws IOException {
    copyStream(new FileInputStream(sourceFile), new FileOutputStream(targetFile));
  }

  /**
   * 复制文件
   *
   * @param is
   *          文件输入流
   * @param os
   *          文件输出流
   */
  public static void copyStream(InputStream is, OutputStream os) throws IOException {
    BufferedInputStream bis = new BufferedInputStream(is);
    BufferedOutputStream bos = new BufferedOutputStream(os);
    // 缓冲数组
    byte[] b = new byte[1024 * 2];
    int len;
    while ((len = bis.read(b)) != -1) {
      bos.write(b, 0, len);
    }
    // 刷新此缓冲的输出流
    bos.flush();

    // 关闭流
    bis.close();
    bos.close();
    is.close();
    os.close();
  }
}
