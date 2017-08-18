package com.multisdk.library.download;

import android.util.Log;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadThread extends Thread {
  private static final String TAG = "DownloadThread";
  private File                saveFile;
  private URL                 downUrl;
  private int                 block;
  /* 下载开始位置 */
  private int                 threadId = -1;
  private int                 downLength;
  private boolean             finish   = false;
  private FileDownloader      downloader;

  public DownloadThread(FileDownloader downloader, URL downUrl, File saveFile, int block, int downLength, int threadId) {
    this.downUrl = downUrl;
    this.saveFile = saveFile;
    this.block = block;
    this.downloader = downloader;
    this.threadId = threadId;
    this.downLength = downLength;
  }

  @Override
  public void run() {
    int i = 1;
    if (downLength < block) {// 未下载完成
      try {
        HttpURLConnection http = (HttpURLConnection) downUrl.openConnection();
        http.setConnectTimeout(10 * 1000); // 设置连接超时
        http.setReadTimeout(10000);
        int startPos = block * (threadId - 1) + downLength;// 开始位置
        int endPos = block * threadId - 1;// 结束位置
        http.setRequestProperty("RANGE", "bytes=" + startPos + "-" + endPos);// 设置获取实体数据的范围
        http.setRequestProperty("User-Agent", "NetFox");
        http.setRequestProperty("Connection", "Keep-Alive"); // 设置为持久连接
        // 得到输入流
        InputStream inStream = http.getInputStream();
        byte[] buffer = new byte[1024];
        int offset = 0;
        print(i+"    " + this.threadId + "    " + startPos);
//        print("Thread " + this.threadId + " start download from position " + startPos);
        // 随机访问文件
        RandomAccessFile threadfile = new RandomAccessFile(this.saveFile, "rwd");
        // 定位到pos位置
        threadfile.seek(startPos);
        while (!downloader.getExit() && (offset = inStream.read(buffer, 0, 1024)) != -1) {
          // 写入文件
          threadfile.write(buffer, 0, offset);
          downLength += offset; // 累加下载的大小
          downloader.update(this.threadId, downLength); // 更新指定线程下载最后的位置
          downloader.append(offset); // 累加已下载大小
        }
        threadfile.close();
        inStream.close();
        i = 2;
        print(i+"    " + this.threadId);
//        print("Thread " + this.threadId + " download finish");
        this.finish = true;
      } catch (Exception e) {
        this.downLength = -1;
        this.finish = true;
        e.printStackTrace();
      }
    }
  }

  private static void print(String msg) {
    Log.d(TAG, msg);
  }

  /**
   * 下载是否完成
   * 
   * @return
   */
  public boolean isFinish() {
    return finish;
  }

  /**
   * 已经下载的内容大小
   * 
   * @return 如果返回值为-1,代表下载失败
   */
  public long getDownLength() {
    return downLength;
  }
}
