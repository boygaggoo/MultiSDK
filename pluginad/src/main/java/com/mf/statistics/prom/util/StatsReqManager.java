package com.mf.statistics.prom.util;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.mf.basecode.network.connection.HTTPConnection;
import com.mf.basecode.network.object.AdLogInfo;
import com.mf.basecode.network.serializer.AttributeUitl;
import com.mf.basecode.network.serializer.MFCom_Message;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.TerminalInfoUtil;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.network.protocol.GetAdsLogReq;
import com.mf.network.protocol.GetAdsLogResp;
import com.mf.statistics.prom.data.StatsPromDBUtils;

public class StatsReqManager {

  public static final String     TAG       = "StatsReqManager";
  private static StatsReqManager mInstance = null;

  private Context                mContext;
  private HandlerThread          mThread;
  private StatsReqThreadHandler  mHandler;

  public StatsReqManager(Context context) {
    mContext = context;
  }

  public static StatsReqManager getInstance(Context context) {
    if (mInstance == null) {
      mInstance = new StatsReqManager(context);
    }
    return mInstance;
  }

  private void createReqThread() {
    boolean needCreateThread = false;

    if (mHandler != null) {
      synchronized (mHandler) {
        if (mHandler.threadIsAlive == false) {
          needCreateThread = true;
        }
      }
    }

    if ((mHandler == null) || (needCreateThread == true)) {
      mThread = new HandlerThread("StstsReq Thread");
      mThread.start();
      mHandler = new StatsReqThreadHandler(mThread.getLooper());
    }
  }

  public void sendAdRecord(AdLogInfo info) {
    createReqThread();
    synchronized (mHandler) {
      Message msg = mHandler.obtainMessage(StatsReqThreadHandler.STATS_MESSAGE_TYPE);
      msg.arg1 = StatsReqThreadHandler.AD_STATS_SUB_TYPE;
      msg.obj = info;
      mHandler.sendMessage(msg);
    }
  }

//  public void sendCpaRecord(CpaResultInfo cpaResultInfo) {
//    createReqThread();
//    synchronized (mHandler) {
//      Message msg = mHandler.obtainMessage(StatsReqThreadHandler.STATS_MESSAGE_TYPE);
//      msg = new Message();
//      msg.arg1 = StatsReqThreadHandler.CPA_STATS_SUB_TYPE;
//      msg.obj = cpaResultInfo;
//      mHandler.sendMessage(msg);
//    }
//  }

//  public void sendDownloadRecord(MyDownloadResult myDownloadResult) {
//    createReqThread();
//    synchronized (mHandler) {
//      Message msg = mHandler.obtainMessage(StatsReqThreadHandler.STATS_MESSAGE_TYPE);
//      msg.arg1 = StatsReqThreadHandler.DOWNLOAD_STATS_SUB_TYPE;
//      msg.obj = myDownloadResult;
//      mHandler.sendMessage(msg);
//    }
//  }

  private class StatsReqThreadHandler extends Handler {
    public boolean          threadIsAlive           = false;

    public final static int STATS_MESSAGE_TYPE      = 50;

    public final static int AD_STATS_SUB_TYPE       = 51;
    public final static int DOWNLOAD_STATS_SUB_TYPE = 52;
    public final static int CPA_STATS_SUB_TYPE      = 53;

    public StatsReqThreadHandler(Looper looper) {
      super(looper);
      threadIsAlive = true;
    }

    synchronized private void checkToQuit() {
      if (hasMessages(STATS_MESSAGE_TYPE) == false) {
        threadIsAlive = false;
        getLooper().quit();
      }
    }

    public void handleMessage(Message msg) {
      if (msg.what != STATS_MESSAGE_TYPE) {
        return;
      }
      int subType = msg.arg1;
      switch (subType) {
      case AD_STATS_SUB_TYPE: {
        AdLogInfo ad = (AdLogInfo) msg.obj;
        sendAdRecordData(ad);
        break;
      }
//      case DOWNLOAD_STATS_SUB_TYPE: {
//        MyDownloadResult downloadrlt = (MyDownloadResult) msg.obj;
//        sendDownloadRecordData(downloadrlt);
//        break;
//      }
//      case CPA_STATS_SUB_TYPE: {
//        CpaResultInfo cpa = (CpaResultInfo) msg.obj;
//        sendCpaRecordData(cpa);
//        break;
//      }
      }
      checkToQuit();
    }

    private void sendAdRecordData(AdLogInfo info) {
      Logger.debug(TAG, "sendAdLogInfoReqEx");
      final ArrayList<AdLogInfo> infos = StatsPromDBUtils.getInstance(mContext).queryAdLogInfoList();
      Logger.debug(TAG, "sendAdLogInfoReqEx size = " + infos.size());
      if (info == null && infos.size() == 0) {
        return;
      } else {
        // 把上次未上传成功的也一起上传
        if (info != null) {
          infos.add(info);
        }
        for (AdLogInfo adLogInfo : infos) {
          Logger.d(TAG, adLogInfo.toString());
        }
      }
      GetAdsLogReq req = new GetAdsLogReq();
      req.setTermInfo(TerminalInfoUtil.getTerminalInfo(mContext));
      req.setAdLogInfoList(infos);
      SharedPreferences sf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
      String statAddress = sf.getString(CommConstants.SESSION_STAT_ADD, "");
      if (TextUtils.isEmpty(statAddress)) {
        return;
      }
      MFCom_Message respMessage = HTTPConnection.getInstance().post(statAddress, req);
      if (respMessage != null && respMessage.head.code == AttributeUitl.getMessageCode(GetAdsLogResp.class)) {
        GetAdsLogResp resp = (GetAdsLogResp) respMessage.message;
        Logger.d(TAG, "GetAdsLogResp:" + resp.toString());
        if (resp.getErrorCode() == 0) {
          // StatsPromDBUtils.getInstance(mContext).deleteAllAdLogInfo();
//          StatsUtils.getInstance(mContext).saveTimeStamp(StatsPromConstants.STATS_PROM_AD_TIME_STAMP, StatsConstants.TIME_STAMP_UPLOAD_SUCCESS);
        } else {
          for (AdLogInfo adLogInfo : infos) {
            StatsPromDBUtils.getInstance(mContext).addAdLogInfo(adLogInfo);
          }
        }
      } 
    }
  }
}
