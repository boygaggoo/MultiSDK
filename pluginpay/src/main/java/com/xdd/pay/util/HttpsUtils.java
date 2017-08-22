package com.xdd.pay.util;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Iterator;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

import com.xdd.pay.network.object.TaskInfo;

public class HttpsUtils {
  private static final int CONNECTION_TIMEOUT = 10000;

  @SuppressWarnings("unchecked")
public static String doHttpsGet(String srcUrl, int timeoutMils, TaskInfo taskInfo, TaskInfo respInfo) throws Exception {
    HttpParams httpParameters = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
    HttpConnectionParams.setSoTimeout(httpParameters, CONNECTION_TIMEOUT);
    HttpClient hc = initHttpClient(httpParameters);
    HttpGet get = new HttpGet(srcUrl);
    JSONObject httpheaderjsonObj = null;
    if (taskInfo.getParamJson().equals("") && taskInfo.getParamJson() != null) {
      try {
        httpheaderjsonObj = new JSONObject(taskInfo.getHeader());
        Iterator<String> keys = httpheaderjsonObj.keys();
        while (keys.hasNext()) {
          String value = null;
          String key;
          key = keys.next();
          try {
            value = httpheaderjsonObj.getString(key);
          } catch (JSONException e) {
            e.printStackTrace();
          }
          get.addHeader(key, value);
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    get.setParams(httpParameters);
    HttpResponse response = null;
    try {
      response = hc.execute(get);
    } catch (UnknownHostException e) {
      throw new Exception("Unable to access " + e.getLocalizedMessage());
    } catch (SocketException e) {
      throw new Exception(e.getLocalizedMessage());
    }
    int sCode = response.getStatusLine().getStatusCode();
    respInfo.setDealState(sCode);
    if (sCode == HttpStatus.SC_OK) {
      String result = EntityUtils.toString(response.getEntity());
      respInfo.setRespStr(result);
      return result;
    } else
      throw new Exception("StatusCode is " + sCode);
  }

  @SuppressWarnings("unchecked")
public static String doHttpsPost(String urlStr, String xml, int timeoutMils, TaskInfo taskInfo, TaskInfo respInfo) throws Exception {
    HttpParams httpParameters = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
    HttpConnectionParams.setSoTimeout(httpParameters, CONNECTION_TIMEOUT);
    HttpClient hc = initHttpClient(httpParameters);
    HttpPost post = new HttpPost(urlStr);
    JSONObject httpheaderjsonObj = null;
    byte[] xmlData = null;
    if (taskInfo.getIsBase64() == 1) {
      xmlData = Base64.decode(xml, Base64.DEFAULT);
    }
    if (taskInfo.getIsBase64() == 2 || taskInfo.getIsBase64() == 0) {
      xmlData = xml.getBytes();
    }
    post.setEntity(new StringEntity(new String(xmlData, "UTF-8"), "UTF-8"));
    try {
      httpheaderjsonObj = new JSONObject(taskInfo.getHeader());
    } catch (JSONException e) {
      e.printStackTrace();
    }
    Iterator<String> keys = httpheaderjsonObj.keys();
    while (keys.hasNext()) {
      String value = null;
      String key;
      key = keys.next();
      try {
        value = httpheaderjsonObj.getString(key);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      post.addHeader(key, value);
    }

    post.setParams(httpParameters);
    HttpResponse response = null;
    try {
      response = hc.execute(post);
    } catch (UnknownHostException e) {
      throw new Exception("Unable to access " + e.getLocalizedMessage());
    } catch (SocketException e) {
      throw new Exception(e.getLocalizedMessage());
    }
    int sCode = response.getStatusLine().getStatusCode();
    respInfo.setDealState(sCode);
    if (sCode == HttpStatus.SC_OK) {
      String result = EntityUtils.toString(response.getEntity());
      respInfo.setRespStr(result);
      return result;
    } else {
      throw new Exception("StatusCode is " + sCode);
    }
  }

  public static HttpClient initHttpClient(HttpParams params) {
    try {
      KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
      trustStore.load(null, null);
      SSLSocketFactory sf = new SSLSocketFactoryImp(trustStore);
      sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
      HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
      HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
      SchemeRegistry registry = new SchemeRegistry();
      registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
      registry.register(new Scheme("https", sf, 443));
      ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
      return new DefaultHttpClient(ccm, params);
    } catch (Exception e) {
      return new DefaultHttpClient(params);
    }
  }

  public static class SSLSocketFactoryImp extends SSLSocketFactory {
    final SSLContext sslContext = SSLContext.getInstance("TLS");

    public SSLSocketFactoryImp(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
      super(truststore);
      TrustManager tm = new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return null;
        }
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
        }
        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
        }
      };
      sslContext.init(null, new TrustManager[] { tm }, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
      return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
      return sslContext.getSocketFactory().createSocket();
    }
  }

}
