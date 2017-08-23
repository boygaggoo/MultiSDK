package com.mf.basecode.network.connection;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MagicConnection {

  private static MagicConnection mConnection;

  public synchronized static MagicConnection getInstance() {
    if (mConnection == null) {
      mConnection = new MagicConnection();
    }
    return mConnection;
  }
  private MagicConnection() {
    
  }
  public void post(String tagetUrl, String content) throws Exception {
    URL url = new URL(tagetUrl);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setUseCaches(false);
    conn.setDoInput(true);
    conn.setDoOutput(true);
    conn.setInstanceFollowRedirects(true);
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    conn.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length));
    conn.setConnectTimeout(25 * 1000);
    OutputStream outStream = conn.getOutputStream();
    outStream.write(content.getBytes());
    outStream.flush();
    outStream.close();
    System.out.println(conn.getResponseCode()); // 响应代码 200表示成功
  }

  public void getReq(String tagetUrl, String content) throws Exception{
//    String message=new String(content);
    content=URLEncoder.encode(content, "UTF-8");
    System.out.println(content);
    String path =tagetUrl+content;
    URL url =new URL(path);
    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    conn.setConnectTimeout(5*1000);
    conn.setRequestMethod("GET");
    InputStream inStream = conn.getInputStream();    
//    System.out.println(result);
}
  
}
