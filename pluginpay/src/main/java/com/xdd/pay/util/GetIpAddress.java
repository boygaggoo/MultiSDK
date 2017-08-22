package com.xdd.pay.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GetIpAddress {

    public static String getInfo(String srcUrl, int timeoutMils) throws IOException {

        URL url = new URL(srcUrl);
        HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
        httpconn.setReadTimeout(timeoutMils);

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

    public static BufferedReader getReader(String srcUrl, int timeoutMils) throws IOException {
        URL url = new URL(srcUrl);
        HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
        httpconn.setReadTimeout(timeoutMils);

        InputStreamReader inputReader = new InputStreamReader(httpconn.getInputStream());
        BufferedReader bufReader = new BufferedReader(inputReader);
        return bufReader;
    }
    
    public static String getInfoByPost4Normal(String urlStr, String param, int timeoutMils, boolean needCompress) {
        DataInputStream input = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] parmaData = param.getBytes();
        try {
            URL url = new URL(urlStr);
            URLConnection urlCon = url.openConnection();
            
            if (needCompress) {
            	urlCon.addRequestProperty("Accept-Encoding", "gzip");
            	urlCon.addRequestProperty("Content-Type", "application/octet-stream");
			}
            
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setUseCaches(false);
            urlCon.setConnectTimeout(timeoutMils);
            urlCon.setReadTimeout(timeoutMils);

            DataOutputStream printout = null;
			if (needCompress) {
				printout = new DataOutputStream(new GZIPOutputStream(urlCon.getOutputStream()));
			} else {
				printout = new DataOutputStream(urlCon.getOutputStream());
			}
            printout.write(parmaData);
            printout.flush();
            printout.close();
            
            String encode = urlCon.getHeaderField("Accept-Encoding");
            if (null != encode && encode.equalsIgnoreCase("gzip")) {
            	input = new DataInputStream(new GZIPInputStream(new BufferedInputStream(urlCon.getInputStream())));
			} else {
				input = new DataInputStream(urlCon.getInputStream());
			}
            byte[] bufferByte = new byte[256];

            int i = -1;
            while ((i = input.read(bufferByte)) > -1) {
                out.write(bufferByte, 0, i);
                out.flush();
            }
        } catch (Exception e) {
            QYLog.e("连接错误：" + e);
        }
        return out.toString();
    }

 // POST
    public static String getInfoByPost(String urlStr, String xml, int timeoutMils) {
        DataInputStream input = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] xmlData = xml.getBytes();
        try {
            URL url = new URL(urlStr);
            URLConnection urlCon = url.openConnection();
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            urlCon.setUseCaches(false);
            urlCon.setConnectTimeout(timeoutMils);
            urlCon.setReadTimeout(timeoutMils);

            urlCon.setRequestProperty("Content-Type", "text/xml");
            urlCon.setRequestProperty("Content-length", String.valueOf(xmlData.length));
            DataOutputStream printout = new DataOutputStream(urlCon.getOutputStream());
            printout.write(xmlData);
            printout.flush();
            printout.close();
            input = new DataInputStream(urlCon.getInputStream());
            byte[] bufferByte = new byte[256];

            int i = -1;
            while ((i = input.read(bufferByte)) > -1) {
                out.write(bufferByte, 0, i);
                out.flush();
            }
        } catch (Exception e) {
            QYLog.e("连接错误：");
        }
        return out.toString();
    }
    
    // POST
    public static String getInfoByPost2(String urlStr, String xml, int timeoutMils) {
        StringBuffer contentBuffer = new StringBuffer();
        DataOutputStream out = null;
        BufferedReader reader = null;
        try {
            byte[] xmlData = xml.getBytes();
            URL postUrl = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            // Post 请求不能使用缓存
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "text/xml");
            connection.setRequestProperty("Content-length", String.valueOf(xmlData.length));
            connection.setConnectTimeout(timeoutMils);
            connection.setReadTimeout(timeoutMils);

            out = new DataOutputStream(connection.getOutputStream());
            out.write(xmlData);
            out.flush();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                contentBuffer.append(line);
            }

        } catch (Exception e) {
            QYLog.e("网络连接异常");
            return "";
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                QYLog.e("关闭网络数据连接错误");
            }
        }
        return contentBuffer.toString();
    }
}
