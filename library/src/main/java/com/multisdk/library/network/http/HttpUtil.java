package com.multisdk.library.network.http;

import android.os.Process;
import android.text.TextUtils;
import com.multisdk.library.network.callback.Callback;
import com.multisdk.library.network.callback.HttpCallback;
import com.multisdk.library.network.callback.NetworkCallback;
import com.multisdk.library.network.config.HttpConfig;
import com.multisdk.library.network.exception.HttpException;
import com.multisdk.library.network.serializer.AttributeUtil;
import com.multisdk.library.network.serializer.CommMessage;
import com.multisdk.library.network.serializer.CommMessageHead;
import com.multisdk.library.network.serializer.MessageCode;
import com.multisdk.library.network.serializer.SignalCode;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class HttpUtil {

  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
  private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
  private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
  private static final int KEEP_ALIVE_SECONDS = 5;
  private static final BlockingQueue<Runnable> sPoolWorkQueue =
      new ArrayBlockingQueue<Runnable>(20);
  private static final ThreadFactory sThreadFactory = new ThreadFactory() {

    public Thread newThread(Runnable r) {
      return new HttpUtilThread(r);
    }
  };

  private static class HttpUtilThread extends Thread {
    HttpUtilThread(Runnable r) {
      super(r);
    }

    @Override public void run() {
      Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
      super.run();
    }
  }

  private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR;

  static {
    ThreadPoolExecutor threadPoolExecutor =
        new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
    threadPoolExecutor.allowCoreThreadTimeOut(true);
    THREAD_POOL_EXECUTOR = threadPoolExecutor;
  }

  private MessageCode mMessageCode = new MessageCode();

  private static class HttpUtilHolder {
    static final HttpUtil httpUtil = new HttpUtil();
  }

  public static HttpUtil getInstance() {
    return HttpUtilHolder.httpUtil;
  }

  public void post(String url,Object req,HttpCallback httpCallback){
    if (TextUtils.isEmpty(url) || req == null || httpCallback == null){
      return;
    }
    try {
      CommMessage commMessage = getMessage(req);
      WorkRunnable workRunnable = new WorkRunnable(commMessage,url,new Callback(httpCallback));
      THREAD_POOL_EXECUTOR.execute(workRunnable);
    } catch (Exception e) {
      httpCallback.onFailure(new HttpException(HttpException.HTTP_ERROR_GET_MSG,e));
    }
  }

  private CommMessage getMessage(Object reqObj) throws Exception {
    SignalCode code = AttributeUtil.getMessageAttribute(reqObj);
    if (code == null || code.messageCode() == 0) {
      int z = 800;
      throw new Exception(z + "");
    }
    UUID uuid = UUID.randomUUID();
    CommMessage message = new CommMessage();
    message.head = new CommMessageHead();
    message.head.version = HttpConfig.PROTOCOL_VERSION;
    message.head.length = 0;
    message.head.firstTransaction = uuid.getMostSignificantBits();
    message.head.secondTransaction = uuid.getLeastSignificantBits();
    message.head.type = HttpConfig.PROTOCOL_REQ;
    message.head.reserved = 0;
    message.head.code = code.messageCode();
    message.message = reqObj;
    return message;
  }

  private class WorkRunnable implements Runnable {

    private CommMessage commMessage;
    private String url;
    private NetworkCallback callback;

    public WorkRunnable(CommMessage commMessage, String url, NetworkCallback callback) {
      this.commMessage = commMessage;
      this.url = url;
      this.callback = callback;
    }

    @Override public void run() {
      while (true) {
        try {
          byte[] receiveBuffer = new byte[HttpConfig.CONNECTION_BUFFER_SIZE];
          byte[] sendBuffer = mMessageCode.serializeMessage(commMessage);
          URL postUrl = new URL(url);
          HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
          connection.setRequestMethod("POST");
          connection.setConnectTimeout(HttpConfig.CONNECTION_TIMEOUT);
          connection.setReadTimeout(HttpConfig.READ_WRITE_TIMEOUT);
          connection.setUseCaches(false);
          connection.setDoInput(true);
          connection.setDoOutput(true);
          connection.setInstanceFollowRedirects(true);
          connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
          connection.connect();

          OutputStream outStream = connection.getOutputStream();
          outStream.write(sendBuffer);
          outStream.flush();
          outStream.close();

          int code = connection.getResponseCode();
          if (code != HttpURLConnection.HTTP_OK && code != HttpURLConnection.HTTP_PARTIAL) {
            throw new Exception("resp error code = " + code);
          }

          InputStream inputStream = connection.getInputStream();
          ByteArrayOutputStream saveStream = new ByteArrayOutputStream();
          int len = -1;
          while ((len = inputStream.read(receiveBuffer)) != -1) {
            saveStream.write(receiveBuffer, 0, len);
          }
          byte[] responseBuff = saveStream.toByteArray();
          saveStream.close();
          inputStream.close();
          connection.disconnect();

          CommMessage receiveMessage = new CommMessage();
          receiveMessage.head = mMessageCode.deserializeHead(responseBuff, 0);
          if (responseBuff.length < receiveMessage.head.length) {
            throw new Exception("data len error.");
          }
          // 包体
          receiveMessage.message =
              mMessageCode.deserializeBody(responseBuff, HttpConfig.PROTOCOL_HEAD_LENGTH,
                  responseBuff.length - HttpConfig.PROTOCOL_HEAD_LENGTH, receiveMessage.head.code);
          if (callback != null){
            callback.onSuccess(receiveMessage);
          }
          return;
        } catch (Exception e) {
          if (!checkRetrySend(commMessage, e.getClass().getSimpleName())) {
            if (callback != null) {
              callback.onFailure(new HttpException(HttpException.HTTP_ERROR,e));
            }
            return;
          }
        }
      }
    }

    boolean checkRetrySend(CommMessage message, String errorMsg) {
      try {
        message.retryCount++;
        SignalCode attribute = AttributeUtil.getMessageAttribute(message.message);
        return attribute != null && message.retryCount < HttpConfig.CONNECTION_MAX_RETRY;
      } catch (Exception e) {
        //        Logger.p(e);
      }
      return false;
    }
  }
}
