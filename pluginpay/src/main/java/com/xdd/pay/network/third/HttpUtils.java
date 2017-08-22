package com.xdd.pay.network.third;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.xdd.pay.network.third.constants.Constants;

public class HttpUtils {

  /**
   * GET方式获取内容
   * 
   * @param srcUrl
   * @param timeoutMils
   * @return
   * @throws IOException
   */
  public static String getInfoByGET(String srcUrl) throws IOException {
    URL url = new URL(srcUrl);
    HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
    httpconn.setConnectTimeout(Constants.CONNECT_TIMEOUT);
    httpconn.setReadTimeout(Constants.READ_TIMEOUT);

    InputStreamReader inputReader = new InputStreamReader(httpconn.getInputStream());
    BufferedReader bufReader = new BufferedReader(inputReader);

    String tmpLine = "";
    StringBuffer contentBuffer = new StringBuffer();

    while ((tmpLine = bufReader.readLine()) != null) {
      contentBuffer.append(tmpLine);
    }

    bufReader.close();
    httpconn.disconnect();
    return contentBuffer.toString();
  }

  /**
   * POST方式获取内容
   * 
   * @param srcUrl
   * @param timeoutMils
   * @return
   * @throws IOException
   */
  public static String getInfoByPOST(String urlStr, String xml) throws IOException {
    DataInputStream input = null;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] xmlData = xml.getBytes();
    URL url = new URL(urlStr);
    HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
    httpconn.setRequestMethod("POST");
    httpconn.setDoOutput(true);
    httpconn.setDoInput(true);
    httpconn.setUseCaches(false);
    httpconn.setConnectTimeout(Constants.CONNECT_TIMEOUT);
    httpconn.setReadTimeout(Constants.READ_TIMEOUT);

    httpconn.setRequestProperty("Content-Type", "text/xml");
    httpconn.setRequestProperty("Content-length", String.valueOf(xmlData.length));
    DataOutputStream printout = new DataOutputStream(httpconn.getOutputStream());
    printout.write(xmlData);
    printout.flush();
    printout.close();
    input = new DataInputStream(httpconn.getInputStream());
    byte[] bufferByte = new byte[256];

    int i = -1;
    while ((i = input.read(bufferByte)) > -1) {
      out.write(bufferByte, 0, i);
      out.flush();
    }
    return out.toString();
  }
}
