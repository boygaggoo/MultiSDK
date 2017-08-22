package com.xdd.pay.network.connect;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.xdd.pay.network.callback.Callback;
import com.xdd.pay.network.object.NetworkAddr;
import com.xdd.pay.network.serializer.AttributeUitl;
import com.xdd.pay.network.serializer.MessageC;
import com.xdd.pay.network.serializer.MessageCodec;
import com.xdd.pay.network.serializer.MessageHead;
import com.xdd.pay.network.serializer.SignalCode;
import com.xdd.pay.network.util.NetworkConstants;
import com.xdd.pay.util.QYLog;


public class HTTPConnection {

  private MessageCodec          m_MessageCodec = new MessageCodec();
  ExecutorService               executor       = null;
  private static HTTPConnection mConnection;

  public synchronized static HTTPConnection getInstance() {
    if (mConnection == null) {
      mConnection = new HTTPConnection();
    }
    return mConnection;
  }

  private HTTPConnection() {
    startRecvThread();
  }

  public void shutdown() {
    executor = null;
  }

  public void startRecvThread() {
    if (executor == null) {
      executor = Executors.newCachedThreadPool();
      // executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L,
      // TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new
      // ThreadPoolExecutor.DiscardOldestPolicy());
    }
  }

  public synchronized void sendRequest(NetworkAddr networkAddr, Object requestObject, Callback callback) {
    try {
      MessageC sendMessage = getMessage(requestObject);
      SendRunnable tc = new SendRunnable(networkAddr, sendMessage, callback);
      executor.execute(tc);
    } catch (Exception e) {
      QYLog.p(e);
      if (callback != null) {
        callback.onResp(NetworkConstants.NETWORK_RESPONSE_ERROR, null);
      }
    }
  }

  private MessageC getMessage(Object requestObject) throws Exception {
    SignalCode code = AttributeUitl.getMessageAttribute(requestObject);
    if (code == null || code.messageCode() == 0) {
      throw new Exception("can't get message code");
    }
    UUID uuid = UUID.randomUUID();
    MessageC messageC = new MessageC();
    messageC.head = new MessageHead();
    messageC.head.version = NetworkConstants.PROTOCL_VERSION;
    messageC.head.length = 0;
    messageC.head.firstTransaction = uuid.getMostSignificantBits();
    messageC.head.secondTransaction = uuid.getLeastSignificantBits();
    messageC.head.type = NetworkConstants.PROTOCL_REQ;
    messageC.head.reserved = 0;
    messageC.head.code = code.messageCode();
    messageC.message = requestObject;
    return messageC;
  }

  class SendRunnable implements Runnable {
    private MessageC        sendMessage;
    private Callback mCallback;
    String                  m_HTTPServerAddress;
    List<String>			m_HTTPServerAddressList;
    Integer					m_HTTPServerAddressType;

    public SendRunnable(NetworkAddr networkAddr, MessageC msg, Callback callback) {
      m_HTTPServerAddress = networkAddr.getServerAddress();
      m_HTTPServerAddressList = networkAddr.getServerAddressList();
      m_HTTPServerAddressType = networkAddr.getServerAddressType();
      sendMessage = msg;
      mCallback = callback;
    }

    @Override
    public void run() {
      int ipIndex = 0;
      while (true) {
        try {
          String serverAddress = null;
          if (null == m_HTTPServerAddress && null != m_HTTPServerAddressList) {
            serverAddress = m_HTTPServerAddressList.get(ipIndex);
          } else {
            serverAddress = m_HTTPServerAddress;
          }

          byte[] recvBuffer = new byte[NetworkConstants.CONNECTION_BUFFER_SIZE];
          byte[] sendBuff = m_MessageCodec.serializeMessage(sendMessage);
          // Logger.debug("send=" + getByteArrayStr(sendBuff));
          Log.e("url","url:" + serverAddress);
          URL postUrl = new URL(serverAddress);
          HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
          connection.setRequestMethod("POST");
          connection.setConnectTimeout(NetworkConstants.CONNECTION_TIMEOUT);
          connection.setReadTimeout(NetworkConstants.READWIRTE_TIMEOUT);
          connection.setUseCaches(false);
          connection.setDoInput(true);
          connection.setDoOutput(true);
          connection.setInstanceFollowRedirects(true);
          connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
          connection.connect();

          OutputStream outStream = connection.getOutputStream();
          outStream.write(sendBuff);
          outStream.flush();
          outStream.close();
          QYLog.d("send " + sendMessage.message.getClass() + " to " + serverAddress);

          int code = connection.getResponseCode();
          if (code != HttpURLConnection.HTTP_OK && code != HttpURLConnection.HTTP_PARTIAL) {
            throw new Exception("get response fail , response code = " + code);
          }

          InputStream inputStream = connection.getInputStream();
          ByteArrayOutputStream saveStream = new ByteArrayOutputStream();

          int len = -1;
          while ((len = inputStream.read(recvBuffer)) != -1) {
            saveStream.write(recvBuffer, 0, len);
          }
          byte[] responseBuff = saveStream.toByteArray();

          /*
           * Logger.debug("recv size=" + responseBuff.length);
           * Logger.debug("recv=" + getByteArrayStr(responseBuff));
           */

          saveStream.close();
          inputStream.close();
          connection.disconnect();
          MessageC recvMessage = new MessageC();
          recvMessage.head = m_MessageCodec.deserializeHead(responseBuff, 0);
          // 比对UUID
          if (recvMessage.head.firstTransaction != sendMessage.head.firstTransaction
              || recvMessage.head.secondTransaction != sendMessage.head.secondTransaction) {
            throw new Exception("receive data fail, uuid not equal");
          }
          // 比对数据长度
          if (responseBuff.length < recvMessage.head.length) {
            throw new Exception("receive data fail, recv " + responseBuff.length + " bytes < message length " + recvMessage.head.length + ", ");
          }
          // 包体
          recvMessage.message = m_MessageCodec.deserializeBody(responseBuff, NetworkConstants.PROTOCOL_HEAD_LENGTH, responseBuff.length
              - NetworkConstants.PROTOCOL_HEAD_LENGTH, recvMessage.head.code);
          QYLog.d("recv " + recvMessage.message.getClass());
          if (mCallback != null) {
            mCallback.onResp(NetworkConstants.NETWORK_RESPONSE_SUCCESS, recvMessage.message);
          }

//          if (ipIndex > 0 && null != m_HTTPServerAddressType) {
//            EncryptUtils.changeIpArraySeq(m_HTTPServerAddressType, ipIndex);
//          }

          return;
        } catch (Exception e) {
		  QYLog.e("SendRunnable exception");
		  QYLog.p(e);
          if (!checkRetrySend(sendMessage, e.getClass().getSimpleName())) {
            ipIndex++;
            sendMessage.retryCount = 0;
            if ((null != m_HTTPServerAddress && null == m_HTTPServerAddressList)
                || (null == m_HTTPServerAddress && null != m_HTTPServerAddressList && ipIndex >= m_HTTPServerAddressList.size())) {
              if (mCallback != null) {
                mCallback.onResp(NetworkConstants.NETWORK_RESPONSE_ERROR, null);
              } else {
                QYLog.e("mCallback is null");
              }
              QYLog.d("connection is closed");
              return;
            }
          }
        }
      }
    }

    protected boolean checkRetrySend(MessageC messageC, String errorMsg) {
      try {
        messageC.retryCount++;
        SignalCode attrib = AttributeUitl.getMessageAttribute(messageC.message);
        if (attrib != null && attrib.autoRetry() && messageC.retryCount < NetworkConstants.CONNECTION_MAX_RETRY) {
          QYLog.d("send " + messageC.message.getClass() + " fail(" + messageC.retryCount + "), retry :" + errorMsg);
          return true;
        } else {
          QYLog.d("send " + messageC.message.getClass() + " fail(" + messageC.retryCount + "), cancel :" + errorMsg);
          return false;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return false;
		}
	}
}
