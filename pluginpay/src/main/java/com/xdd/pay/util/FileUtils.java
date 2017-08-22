package com.xdd.pay.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Properties;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

/**
 * 文件名称: FileUtils.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-22 17:57:20<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class FileUtils {
  private static String RootFilePath;

  private static String DebugFilePath;

  /**
   * 获取数据统计文件根目录
   * 
   * @return
   */
  public static String getFileRootPath() {
    if (TextUtils.isEmpty(RootFilePath)) {
      if (isSDExists()) {
        RootFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + EncryptUtils.getRootFileName();
      } else {
        RootFilePath = "/sdcard/" + EncryptUtils.getRootFileName();
      }
      File f = new File(RootFilePath);
      if (!f.exists()) {
        f.mkdirs();
      }
    }
    return RootFilePath;
  }

  /**
   * 获取debug文件
   * 
   * @return
   */
  public static File getDebugFile() {
    if (TextUtils.isEmpty(DebugFilePath)) {
      if (isSDExists()) {
        DebugFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + EncryptUtils.getDebugFileName();
      } else {
        DebugFilePath = "/sdcard/" + EncryptUtils.getDebugFileName();
      }
    }
    return new File(DebugFilePath);
  }

  /**
   * 判断sd是否装载
   * 
   * @return
   */
  public static boolean isSDExists() {
    return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
  }

  /**
   * 根据后缀找到指定目录下所有的文件
   * 
   * @param targetList
   *          需要插入的列表
   * @param suffix
   *          后缀
   */
  public static void getFileListBySuffix(ArrayList<File> targetList, File dir, String suffix) {
    if (suffix == null || TextUtils.isEmpty(suffix.trim())) {
      return;
    }
    try {
      File[] files = dir.listFiles();
      if (files != null) {
        for (File f : files) {
          if (f.isFile()) {
            if (f.getName().toLowerCase().endsWith(suffix.toLowerCase())) {
              targetList.add(f);
            }
          } else {
            getFileListBySuffix(targetList, f, suffix);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 根据文件名找到指定目录下所有的文件
   * 
   * @param targetList
   *          需要插入的列表
   * @param fileName
   *          文件名
   */
  public static void getFileListByName(ArrayList<File> targetList, File dir, String fileName) {
    if (fileName == null || TextUtils.isEmpty(fileName.trim())) {
      return;
    }
    try {
      File[] files = dir.listFiles();
      if (files != null) {
        for (File f : files) {
          if (f.isFile()) {
            if (f.getName().toLowerCase().equals(fileName.toLowerCase())) {
              targetList.add(f);
            }
          } else {
            getFileListByName(targetList, f, fileName);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } catch (Error e) {
      e.printStackTrace();
    }
  }
  
  public static void copyFile(String fileFrom, String fileTo) throws Exception {
	  InputStream fisFrom = new FileInputStream(fileFrom);
      OutputStream fosTo = new FileOutputStream(fileTo);
      copyStream(fisFrom, fosTo);
  }
  
  public static void copyFile(File fileFrom, File fileTo) throws Exception {
	  InputStream fisFrom = new FileInputStream(fileFrom);
      OutputStream fosTo = new FileOutputStream(fileTo);
      copyStream(fisFrom, fosTo);
  }

  /**
   * 复制文件
   * 
   * @param InputStream
   *          文件输入流
   * @param OutputStream
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

  /**
   * 将内容写入文件
   * 
   * @param content
   *          内容
   * @param file
   *          文件
   */
  public static void writeFile(File file, String content, boolean isAppend) throws IOException {
    FileWriter fw;
    BufferedWriter bw;
    fw = new FileWriter(file, isAppend);
    bw = new BufferedWriter(fw);
    bw.write(content);
    bw.flush();
    bw.close();
    fw.close();
  }

  /**
   * 获取文件的MD5值
   * 
   * @param filepath
   * @return
   * @throws NoSuchAlgorithmException
   * @throws FileNotFoundException
   */
  public static String getMd5FromFile(String filepath) throws NoSuchAlgorithmException, FileNotFoundException {
    MessageDigest digest = MessageDigest.getInstance("MD5");
    File f = new File(filepath);
    String output = "";
    InputStream is = new FileInputStream(f);
    byte[] buffer = new byte[8192];
    int read = 0;
    try {
      while ((read = is.read(buffer)) > 0) {
        digest.update(buffer, 0, read);
      }
      byte[] md5sum = digest.digest();
      BigInteger bigInt = new BigInteger(1, md5sum);
      output = bigInt.toString(16);
      for (; output.length() < 32;) {
        output = "0" + output;
      }
    } catch (IOException e) {
      throw new RuntimeException("Unable to process file for MD5", e);
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
      }
    }
    return output;
  }

  /**
   * 读文件，返回字节数组
   * 
   * @param filename
   * @return
   * @throws IOException
   */
  public static byte[] read(String filename) throws IOException {

    File f = new File(filename);
    if (!f.exists()) {
      throw new FileNotFoundException(filename);
    }

    ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
    BufferedInputStream in = null;
    try {
      in = new BufferedInputStream(new FileInputStream(f));
      int buf_size = 1024;
      byte[] buffer = new byte[buf_size];
      int len = 0;
      while (-1 != (len = in.read(buffer, 0, buf_size))) {
        bos.write(buffer, 0, len);
      }
      return bos.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      try {
        in.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      bos.close();
    }
  }

  public static final String CONFIG_FILE = ".cfg";

  /**
   * 从配置文件中读取配置信息
   * 
   * @param name
   * @return
   */
  public static String getConfigByNameFromFile(String name) {
    String ret = null;
    Properties p = new Properties();
    File file = new File(FileUtils.getFileRootPath() + "/" + CONFIG_FILE);
    FileInputStream fis = null;
    try {
      if (file.exists()) {
        fis = new FileInputStream(file);
        p.load(fis);
        ret = p.getProperty(name);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   * 插入配置信息到文件
   * 
   * @param name
   * @param value
   */
  public static void putConfigToFile(String name, String value) {
    Properties p = new Properties();
    File file = new File(FileUtils.getFileRootPath() + "/" + CONFIG_FILE);
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
      FileInputStream fis = new FileInputStream(file);
      p.load(fis);
      p.setProperty(name, value);
      p.store(new FileOutputStream(file), "");
    } catch (Exception e) {
      QYLog.p(e);
    }
  }

  /**
   * 从配置文件读取参数
   * 
   * @param context
   * @param fileName
   * @param key
   * @return value
   */
  public static String getConfigFromAssetsByKey(Context context, String fileName, String key) {

    Properties p = new Properties();
    String value = null;
    try {
      InputStream inputStream = context.getAssets().open(fileName);
      p.load(inputStream);
      value = p.getProperty(key);
    } catch (Exception e) {
      QYLog.p(e);
    }
    return value;
  }
}
