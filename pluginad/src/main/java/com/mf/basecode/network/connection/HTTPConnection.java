package com.mf.basecode.network.connection;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import com.mf.basecode.network.serializer.AttributeUitl;
import com.mf.basecode.network.serializer.MFCom_Message;
import com.mf.basecode.network.serializer.MFCom_MessageHead;
import com.mf.basecode.network.serializer.MessageCodec;
import com.mf.basecode.network.serializer.SignalCode;
import com.mf.basecode.network.util.NetworkConstants;
import com.mf.basecode.utils.Logger;

public class HTTPConnection {

  private MessageCodec          m_MessageCodec = new MessageCodec();
  private static HTTPConnection mConnection;

  public synchronized static HTTPConnection getInstance() {
    if (mConnection == null) {
      mConnection = new HTTPConnection();
    }
    return mConnection;
  }
  private HTTPConnection() {
  }

  private MFCom_Message getMessage(Object requestObject) throws Exception {
    SignalCode code = AttributeUitl.getMessageAttribute(requestObject);
    if (code == null || code.messageCode() == 0) {
      throw new Exception("can't get message code");
    }
    UUID uuid = UUID.randomUUID();
    MFCom_Message message = new MFCom_Message();
    message.head = new MFCom_MessageHead();
    message.head.version = NetworkConstants.PROTOCL_VERSION;
    message.head.length = 0;
    message.head.firstTransaction = uuid.getMostSignificantBits();
    message.head.secondTransaction = uuid.getLeastSignificantBits();
    message.head.type = NetworkConstants.PROTOCL_REQ;
    message.head.reserved = 0;
    message.head.code = code.messageCode();
    message.message = requestObject;
    return message;
  }    
    
  public MFCom_Message post(String networkAddr, Object requestObject){
    MFCom_Message sendMessage = null;
    try {
      sendMessage = getMessage(requestObject);
    } catch (Exception e) {
      Logger.p(e);
    }
    while (true) {
      try {
        byte[] recvBuffer = new byte[NetworkConstants.CONNECTION_BUFFER_SIZE];
        byte[] sendBuff = m_MessageCodec.serializeMessage(sendMessage);
        // Logger.debug("send=" + getByteArrayStr(sendBuff));
        URL postUrl = new URL(networkAddr);
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
        /*
         * DataOutputStream out = new DataOutputStream(
         * connection.getOutputStream()); out.write(sendBuff); out.flush();
         * out.close();
         */
//        Logger.debug(NetworkConstants.TAG, "send " + sendMessage.message.getClass() + " to " + networkAddr);

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
        MFCom_Message recvMessage = new MFCom_Message();
        recvMessage.head = m_MessageCodec.deserializeHead(responseBuff, 0);
        if (responseBuff.length < recvMessage.head.length) {
          throw new Exception("receive data fail, recv " + responseBuff.length + " bytes < message length " + recvMessage.head.length + ", ");
        }
        Logger.error(NetworkConstants.TAG, "recvMessage: " + recvMessage.head.toString());
        // 包体
        recvMessage.message = m_MessageCodec.deserializeBody(responseBuff, NetworkConstants.PROTOCOL_HEAD_LENGTH, responseBuff.length
            - NetworkConstants.PROTOCOL_HEAD_LENGTH, recvMessage.head.code);
        Logger.debug(NetworkConstants.TAG, "recv " + recvMessage.message.getClass());
        return recvMessage;
      } catch (Exception e) {
        Logger.error(NetworkConstants.TAG, e.toString());
        Logger.error(NetworkConstants.TAG, "connection 4444444444");
        Logger.error(NetworkConstants.TAG, "connection throws exception ");
        if (!checkRetrySend(sendMessage, e.getClass().getSimpleName())) {
          Logger.debug(NetworkConstants.TAG, "connection is closed");
          return null;
        }
      }
    }
  }
  
  protected boolean checkRetrySend(MFCom_Message message, String errorMsg) {
    try {
      message.retryCount++;
      SignalCode attrib = AttributeUitl.getMessageAttribute(message.message);
      if (attrib != null && attrib.autoRetry() && message.retryCount < NetworkConstants.CONNECTION_MAX_RETRY) {
        Logger.error(NetworkConstants.TAG, "send " + message.message.getClass() + " fail(" + message.retryCount + "), retry :" + errorMsg);
        return true;
      } else {
        Logger.error(NetworkConstants.TAG, "send " + message.message.getClass() + " fail(" + message.retryCount + "), cancel :" + errorMsg);
        return false;
      }
    } catch (Exception e) {
//      e.printStackTrace();
    }
    return false;
  }
  
}
