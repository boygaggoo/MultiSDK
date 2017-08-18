package com.multisdk.library.download;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import com.multisdk.library.utils.Md5Util;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileDownloader {

  private static final String TAG = "FileDownloader";

  private Context context;
  private FileService           fileService;
  /* 停止下载 */
  private boolean               exit;
  /* 已下载文件长度 */
  private int                   downloadSize = 0;
  /* 原始文件长度 */
  private int                   fileSize     = 0;
  /* 线程数 */
  private DownloadThread[]      threads;
  /* 本地保存文件 */
  private File saveFile;
  /* 缓存各线程下载的长度 */
  private Map<Integer, Integer> data         = new ConcurrentHashMap<Integer, Integer>();
  /* 每条线程下载的长度 */
  private int                   block;
  /* 下载地址 */
  private String                downloadUrl;
  /* 文件的MD5 */
  private String                md5;

  /* 自更新版本号 */
  private int                   versionCode;
  private String                fileName;
  private String                tmpFileName;

  private int                   retry        = 0;
  private static final int      RETRY_TIMES  = 3;                                                    // 重试次数

  /**
   * 获取线程数
   */
  public int getThreadSize() {
    return threads.length;
  }

  /**
   * 退出下载
   */
  public void exit() {
    this.exit = true;
  }

  public boolean getExit() {
    return this.exit;
  }

  /**
   * 获取文件大小
   *
   * @return
   */
  public int getFileSize() {
    return fileSize;
  }

  /**
   * 累计已下载大小
   *
   * @param size
   */
  protected synchronized void append(int size) {
    downloadSize += size;
  }

  /**
   * 更新指定线程最后下载的位置
   *
   * @param threadId
   *          线程id
   * @param pos
   *          最后下载的位置
   */
  protected synchronized void update(int threadId, int pos) {
    this.data.put(threadId, pos);
    this.fileService.update(this.downloadUrl, threadId, pos);
  }

  public FileDownloader(Context context, String downloadUrl, String md5, File fileSaveDir, int versionCode) {
    int z = -1;
    try {
      this.context = context;
      this.md5 = md5;
      this.downloadUrl = downloadUrl;
      this.versionCode = versionCode;
      fileService = new FileService(this.context);
      URL url = new URL(this.downloadUrl);
      if (!fileSaveDir.exists()) // 判断目录是否存在，如果不存在，创建目录
        fileSaveDir.mkdirs();
      this.threads = new DownloadThread[1];// 实例化线程数组
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setConnectTimeout(5 * 1000);
      conn.setRequestMethod("GET");
      conn.setRequestProperty("User-Agent", "NetFox");
      conn.connect(); // 连接
      printResponseHeader(conn);
      if (conn.getResponseCode() == 200) { // 响应成功
        this.fileSize = conn.getContentLength();// 根据响应获取文件大小
        if (this.fileSize <= 0){
          z = 1;
          Log.d(TAG,z+"    "+this.fileSize);
          throw new RuntimeException(z+"");
        }
        fileName = getFileName(conn);// 获取文件名称
        tmpFileName = UUID.randomUUID().toString() + ".tmp";
        this.saveFile = new File(fileSaveDir, tmpFileName);// 构建保存文件
        if (!saveFile.exists()) {
          saveFile.createNewFile();
        }
        SparseIntArray logdata = fileService.getData(downloadUrl);// 获取下载记录
        if (logdata.size() > 0) {// 如果存在下载记录

          for (int i = 0; i < logdata.size(); i++) {
            data.put(logdata.keyAt(i), logdata.valueAt(i));// 把各条线程已经下载的数据长度放入data中
          }
        }
        if (this.data.size() == this.threads.length) {// 下面计算所有线程已经下载的数据总长度
          for (int i = 0; i < this.threads.length; i++) {
            this.downloadSize += this.data.get(i + 1);
          }
          z = 2;
          Log.d(TAG,z+"    " + this.downloadSize);
          //          Logger.debug(TAG,"file size" + this.downloadSize);
        }
        // 计算每条线程下载的数据长度
        this.block = (this.fileSize % this.threads.length) == 0 ? this.fileSize / this.threads.length : this.fileSize / this.threads.length + 1;
      } else {
        z = 3;
        Log.d(TAG,z+"");
        //        Logger.error("server no response ");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 获取文件名
   */
  private String getFileName(HttpURLConnection conn) {
    String filename = this.downloadUrl.substring(this.downloadUrl.lastIndexOf('/') + 1);
    if (filename == null || "".equals(filename.trim())) {// 如果获取不到文件名称
      for (int i = 0;; i++) {
        String mine = conn.getHeaderField(i);
        if (mine == null)
          break;
        if ("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())) {
          Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
          if (m.find())
            return m.group(1);
        }
      }
      filename = UUID.randomUUID() + ".tmp";// 默认取一个文件名
    }
    return filename;
  }

  /**
   * 开始下载文件
   *
   * @param listener
   *          监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
   * @return 已下载文件大小
   * @throws Exception
   */
  public int download(DownloadProgressListener listener) {
    int z = -1;
    try {
      RandomAccessFile randOut = new RandomAccessFile(this.saveFile, "rw");
      if (this.fileSize > 0)
        randOut.setLength(this.fileSize); // 预分配fileSize大小
      randOut.close();
      URL url = new URL(this.downloadUrl);
      if (this.data.size() != this.threads.length) {// 如果原先未曾下载或者原先的下载线程数与现在的线程数不一致
        this.data.clear();
        for (int i = 0; i < this.threads.length; i++) {
          this.data.put(i + 1, 0);// 初始化每条线程已经下载的数据长度为0
        }
        this.downloadSize = 0;
      }
      for (int i = 0; i < this.threads.length; i++) {// 开启线程进行下载
        int downLength = this.data.get(i + 1);
        if (downLength < this.block && this.downloadSize < this.fileSize) {// 判断线程是否已经完成下载,否则继续下载
          this.threads[i] = new DownloadThread(this, url, this.saveFile, this.block, this.data.get(i + 1), i + 1);
          this.threads[i].setPriority(7); // 设置线程优先级
          this.threads[i].start();
        } else {
          this.threads[i] = null;
        }
      }
      fileService.delete(this.downloadUrl);// 如果存在下载记录，删除它们，然后重新添加
      fileService.save(this.downloadUrl, this.data);
      boolean notFinish = true;// 下载未完成
      while (notFinish) {// 循环判断所有线程是否完成下载
        Thread.sleep(500);
        notFinish = false;// 假定全部线程下载完成
        for (int i = 0; i < this.threads.length; i++) {
          if (this.threads[i] != null && !this.threads[i].isFinish()) {// 如果发现线程未完成下载
            notFinish = true;// 设置标志为下载没有完成
            if (this.threads[i].getDownLength() == -1 && retry < RETRY_TIMES) {// 如果下载失败,再重新下载
              retry++;
              this.threads[i] = new DownloadThread(this, url, this.saveFile, this.block, this.data.get(i + 1), i + 1);
              this.threads[i].setPriority(7);
              this.threads[i].start();
            }
          }
        }
      }
      if (downloadSize == this.fileSize) {
        fileService.delete(this.downloadUrl);// 下载完成删除记录
        String fileMd5 = Md5Util.getMd5FromFile(this.saveFile.getAbsolutePath());
        //        FileDownloadResultExt resultExt = new FileDownloadResultExt();
        //        resultExt.setSource1((short) 21);
        //        resultExt.setFileVerInfo(new FileVerInfo("", versionCode));
        if (!TextUtils.isEmpty(md5) && md5.equalsIgnoreCase(fileMd5)) {
          //          resultExt.setResult(3);
          z = 4;
          Log.d(TAG, z+"    ");
          //          Logger.debug(TAG, "download success.");
          if (listener != null)
            listener.onDownloadResult(true);// 通知目前已经下载完成的数据长度
        } else {
          boolean deleteResult = this.saveFile.delete();
          z = 5;
          Log.d(TAG, z+"    ");
          //          Logger.debug(TAG, "md5 verify failed and delete the tmp zip result:" + deleteResult);
          //          resultExt.setResult(1);
          if (listener != null)
            listener.onDownloadResult(false);
        }
        // TODO: 2017/8/16 下载完成统计
      }
    } catch (Exception e) {
      e.printStackTrace();
      if (listener != null)
        listener.onDownloadResult(false);
      // TODO: 2017/8/16 异常统计
    }
    z = 6;
    Log.d(TAG, z+"    " + downloadSize + "    " + fileSize);
    //    Logger.debug(TAG, "downloadSize:" + downloadSize + "---fileSize:" + fileSize);
    return this.downloadSize;
  }

  /**
   * 获取Http响应头字段
   *
   * @param http
   * @return
   */
  public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
    Map<String, String> header = new LinkedHashMap<String, String>();
    for (int i = 0;; i++) {
      String mine = http.getHeaderField(i);
      if (mine == null)
        break;
      header.put(http.getHeaderFieldKey(i), mine);
    }
    return header;
  }

  /**
   * 打印Http头字段
   *
   * @param http
   */
  public static void printResponseHeader(HttpURLConnection http) {
    Map<String, String> header = getHttpResponseHeader(http);
    for (Map.Entry<String, String> entry : header.entrySet()) {
      String key = entry.getKey() != null ? entry.getKey() + ":" : "";
      Log.d(TAG,key + entry.getValue());
    }
  }

  public File getSaveFile() {
    return saveFile;
  }

  public void setSaveFile(File saveFile) {
    this.saveFile = saveFile;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

}
