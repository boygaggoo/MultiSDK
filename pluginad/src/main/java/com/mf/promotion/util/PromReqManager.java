package com.mf.promotion.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.promotion.service.MFApkServiceFactory;
import com.mf.promotion.service.req.ReqAutoStartToService;
import com.mf.promotion.service.req.ReqBrowerToService;
import com.mf.promotion.service.req.ReqCommConfigToService;
import com.mf.promotion.service.req.ReqDesktopAdToService;
import com.mf.promotion.service.req.ReqExitToService;
import com.mf.promotion.service.req.ReqFolderIconToService;
import com.mf.promotion.service.req.ReqMagicToService;
import com.mf.promotion.service.req.ReqPlugToService;
import com.mf.promotion.service.req.ReqPushNotifyToService;
import com.mf.promotion.service.req.ReqServerAddrToService;
import com.mf.promotion.service.req.ReqShortcutToService;
import com.mf.promotion.service.req.ReqWakeupToService;
//import android.util.Log;

public class PromReqManager {
  public static final String    TAG       = "PromReqManager";
  private static PromReqManager mInstance = null;

  private Context               mContext;
  private HandlerThread         mThread;
  private PromReqThreadHandler  mHandler;

  public PromReqManager(Context context) {
    mContext = context;
  }

  public static PromReqManager getInstance(Context context) {
    if (mInstance == null) {
      mInstance = new PromReqManager(context);
    }
    return mInstance;
  }

  private void createReqThread() {
    boolean needCreateThread = false;

    if (mHandler != null) {
      synchronized (mHandler) {
        if (!mHandler.threadIsAlive) {
          needCreateThread = true;
        }
      }
    }

    if ((mHandler == null) || (needCreateThread)) {
      mThread = new HandlerThread("Req Thread");
      mThread.start();
      mHandler = new PromReqThreadHandler(mThread.getLooper());
    }
  }

  public void ReqServerAddr() {
    createReqThread();
    synchronized (mHandler) {
      Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
      msg.arg1 = PromReqThreadHandler.SERVERADDR_SUB_TYPE;
      mHandler.sendMessage(msg);
    }
  }

  public void ReqCommConfig() {
    createReqThread();
    synchronized (mHandler) {
      Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
      msg.arg1 = PromReqThreadHandler.CONFIG_SUB_TYPE;
      mHandler.sendMessage(msg);
    }
  }

  public void ReqPlugin() {
    createReqThread();
    synchronized (mHandler) {
      Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
      msg.arg1 = PromReqThreadHandler.PLUGIN_SUB_TYPE;
      mHandler.sendMessage(msg);
    }
  }
  
  public void ReqExit() {
    createReqThread();
    synchronized (mHandler) {
      Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
      msg.arg1 = PromReqThreadHandler.EXIT_SUB_TYPE;
      mHandler.sendMessage(msg);
    }
  }

  public void ReqStart() {
    createReqThread();
    synchronized (mHandler) {
      Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
      msg.arg1 = PromReqThreadHandler.START_SUB_TYPE;
      mHandler.sendMessage(msg);
    }
  }
  
  public void ReqCpaRule() {
    createReqThread();
    synchronized (mHandler) {
      Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
      msg.arg1 = PromReqThreadHandler.CPARULE_SUB_TYPE;
      mHandler.sendMessage(msg);
    }
  }

  public void ReqDesktopAd() {
    createReqThread();
    synchronized (mHandler) {
      Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
      msg.arg1 = PromReqThreadHandler.DESKTOP_SUB_TYPE;
      mHandler.sendMessage(msg);
    }
  }

  public void ReqFolderIcon() {
    createReqThread();
    synchronized (mHandler) {
      Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
      msg.arg1 = PromReqThreadHandler.FOLDERICON_SUB_TYPE;
      mHandler.sendMessage(msg);
    }
  }

  public void ReqSilent() {
    createReqThread();
    synchronized (mHandler) {
      Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
      msg.arg1 = PromReqThreadHandler.SILENT_SUB_TYPE;
      mHandler.sendMessage(msg);
    }
  }

  public void ReqPushNotify() {
    createReqThread();
    synchronized (mHandler) {
      Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
      msg.arg1 = PromReqThreadHandler.PUSH_SUB_TYPE;
      mHandler.sendMessage(msg);
    }
  }

  public void ReqShotcut() {
    createReqThread();
    synchronized (mHandler) {
      Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
      msg.arg1 = PromReqThreadHandler.SHORTCUT_SUB_TYPE;
      mHandler.sendMessage(msg);
    }
  }

  public void ReqWakeupApk() {
    createReqThread();
    synchronized (mHandler) {
      Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
      msg.arg1 = PromReqThreadHandler.WAKEUP_SUB_TYPE;
      mHandler.sendMessage(msg);
    }
  }
  
//  public void ReqEnhanced() {
//    createReqThread();
//    synchronized (mHandler) {
//      Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
//      msg.arg1 = PromReqThreadHandler.ENHANCED_SUB_TYPE;
//      mHandler.sendMessage(msg);
//    }
//  }
  
  public void ReqMagic() {
    createReqThread();
    synchronized (mHandler) {
      Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
      msg.arg1 = PromReqThreadHandler.MAGIC_SUB_TYPE;
      mHandler.sendMessage(msg);
    }
  }
  
  public void ReqBrower(){
	  createReqThread();
	  synchronized (mHandler) {
		  Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
	      msg.arg1 = PromReqThreadHandler.BROWER_SUB_TYPE;
	      mHandler.sendMessage(msg);
	}
  }
  
  public void HandleHandleAdInfo() {
      createReqThread();
      synchronized (mHandler) {
        Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
        msg.arg1 = PromReqThreadHandler.HANDLE_HANDLE_ADIONFO_SUB_TYPE;
        mHandler.sendMessage(msg);
     }
  }

  public void HandlePreDownloadAdInfo() {
    Logger.e(TAG, "HandlePreDownloadAdInfo()  ");
    SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    long activePointTime = spf.getLong(CommConstants.CONFIG_ACTIVE_POINT_TIME, 0);
    Logger.e(TAG, "HandlePreDownloadAdInfo    activePointTime = "+activePointTime+"   sys = "+System.currentTimeMillis());
    int predown = spf.getInt(CommConstants.CONFIG_PRE_DOWNLOAD, 0);
      if(System.currentTimeMillis() < activePointTime && predown == 1){
      createReqThread();
      Logger.e(TAG, "HandlePreDownloadAdInfo()  1111");
      synchronized (mHandler) {
        Message msg = mHandler.obtainMessage(PromReqThreadHandler.UPDATE_MESSAGE_TYPE);
        msg.arg1 = PromReqThreadHandler.HANDLE_PREDOWNLOAD_ADINFO_SUB_TYPE;
        mHandler.sendMessage(msg);
      }
    }
  }

  private class PromReqThreadHandler extends Handler {
    public boolean          threadIsAlive          = false;

    public final static int UPDATE_MESSAGE_TYPE    = 80;

    public final static int SERVERADDR_SUB_TYPE    = 100;
    public final static int CONFIG_SUB_TYPE        = 101;
    public final static int PLUGIN_SUB_TYPE        = 102;
    public final static int CPARULE_SUB_TYPE       = 103;
    public final static int DESKTOP_SUB_TYPE       = 104;
    public final static int FOLDERICON_SUB_TYPE    = 105;
    public final static int SILENT_SUB_TYPE        = 106;
    public final static int PUSH_SUB_TYPE          = 107;
    public final static int SHORTCUT_SUB_TYPE      = 108;
    public final static int WAKEUP_SUB_TYPE        = 109;
    public final static int MAGIC_SUB_TYPE      = 110;
    public final static int HANDLE_HANDLE_ADIONFO_SUB_TYPE = 111;
    public final static int HANDLE_PREDOWNLOAD_ADINFO_SUB_TYPE = 112;
//    public final static int ENHANCED_SUB_TYPE    = 113;
    public final static int EXIT_SUB_TYPE        = 114;
	public final static int BROWER_SUB_TYPE 	 = 115;
    public final static int START_SUB_TYPE        = 116;

    public PromReqThreadHandler(Looper looper) {
      super(looper);
      threadIsAlive = true;
    }

    synchronized private void checkToQuit() {
      if (!hasMessages(UPDATE_MESSAGE_TYPE)) {
        threadIsAlive = false;
        getLooper().quit();
      }
    }

    public void handleMessage(Message msg) {
      if (msg.what != UPDATE_MESSAGE_TYPE) {
        return;
      }

      int subType = msg.arg1;

      switch (subType) {
      case SERVERADDR_SUB_TYPE: {
        ReqServerAddrToService addr = new ReqServerAddrToService(mContext);
        addr.sendRequest();
        break;
      }

      case CONFIG_SUB_TYPE: {
        ReqCommConfigToService config = new ReqCommConfigToService(mContext);
        config.sendRequest();
        break;
      }

       case PLUGIN_SUB_TYPE: {
       Logger.e(TAG, "plugin");
       ReqPlugToService plugin = new ReqPlugToService(mContext);
       plugin.sendRequest();
       break;
       }
      
       case EXIT_SUB_TYPE: {
       Logger.e(TAG, "exit");
       ReqExitToService exit = new ReqExitToService(mContext);
       exit.sendRequest();
       break;
       }
       
       case START_SUB_TYPE: {
         Logger.e(TAG, "startapp");
         ReqAutoStartToService start = new ReqAutoStartToService(mContext);
         start.sendRequest();
         break;
       }

      case DESKTOP_SUB_TYPE: {
        Logger.e(TAG, "desktop");
        ReqDesktopAdToService desk = new ReqDesktopAdToService(mContext);
        desk.sendRequest();
        break;
      }

      case FOLDERICON_SUB_TYPE: {
        Logger.e(TAG, "foldericon");
        ReqFolderIconToService fold = new ReqFolderIconToService(mContext);
        fold.sendRequest();
        break;
      }

      // case SILENT_SUB_TYPE: {
      // Logger.e(TAG, "silent");
      // PromReqNewSilentService silent = new PromReqNewSilentService(mContext);
      // silent.sendRequest();
      // break;
      // }

      case PUSH_SUB_TYPE: {
        Logger.e(TAG, "push");
        ReqPushNotifyToService push = new ReqPushNotifyToService(mContext);
        push.sendRequest();
        break;
      }

      case SHORTCUT_SUB_TYPE: {
        Logger.e(TAG, "shortcut");
        ReqShortcutToService shortcut = new ReqShortcutToService(mContext);
        shortcut.sendRequest();
        break;
      }

      case WAKEUP_SUB_TYPE: {
        Logger.e(TAG, "wakeup");
        ReqWakeupToService wake = new ReqWakeupToService(mContext);
        wake.sendRequest();
        break;
      }
      
//      case ENHANCED_SUB_TYPE: {
//        Logger.e(TAG, "wakeup");
//        ReqEnhancedToService enhanced = new ReqEnhancedToService(mContext);
//        enhanced.sendRequest();
//        break;
//      }
      
      case MAGIC_SUB_TYPE: {
        Logger.e(TAG, "wakeup");
        ReqMagicToService magic = new ReqMagicToService(mContext);
        magic.sendRequest();
        break;
      }
      
      case BROWER_SUB_TYPE: {
    	  Logger.error(TAG, " brower sub type ");
    	  ReqBrowerToService brower = new ReqBrowerToService(mContext);
    	  brower.sendBrowerReq();
    	  break;
      }
      
      case HANDLE_HANDLE_ADIONFO_SUB_TYPE: {
        Logger.e(TAG, "wakeup");
        TimerManager.getInstance(mContext).startTimerByTime(System.currentTimeMillis(), MFApkServiceFactory.HANDLE_DELATACTIVE_SERVICE.getServiceId());
        break;
      }
      
      case HANDLE_PREDOWNLOAD_ADINFO_SUB_TYPE: {
        Logger.e(TAG, "HandlePreDownLoadService");
        TimerManager.getInstance(mContext).startTimerByTime(System.currentTimeMillis(), MFApkServiceFactory.HANDLE_PREDOWNLOAD_SERVICE.getServiceId());
        break;
      }
      }
      checkToQuit();
    }
  }

}
