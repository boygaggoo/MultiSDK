package com.xdd.pay.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.SmsSender;
import com.google.android.gms.analytics.CampaignTrackingService;
import com.google.android.gms.callback.SmsSendCallback;
import com.google.android.gms.constant.SendType;
import com.google.android.gms.util.SmsUtils;
import com.xdd.pay.callback.ResultCode;
import com.xdd.pay.config.Config;
import com.xdd.pay.constant.CommConstant;
import com.xdd.pay.db.DBConfig;
import com.xdd.pay.db.DBUtils;
import com.xdd.pay.db.StorageConfig;
import com.xdd.pay.db.StorageUtils;
import com.xdd.pay.network.callback.Callback;
import com.xdd.pay.network.connect.HTTPConnection;
import com.xdd.pay.network.object.ExceptionLogInfo;
import com.xdd.pay.network.object.InterceptInfo;
import com.xdd.pay.network.object.LogInfo;
import com.xdd.pay.network.object.LoginStatiInfo;
import com.xdd.pay.network.object.PayCodeInfo;
import com.xdd.pay.network.object.PayInfo;
import com.xdd.pay.network.object.SmsLogInfo;
import com.xdd.pay.network.object.TaskInfo;
import com.xdd.pay.network.object.VerifyInfo;
import com.xdd.pay.network.protocol.GetExceptionLogInfoReq;
import com.xdd.pay.network.protocol.GetInitReq;
import com.xdd.pay.network.protocol.GetInitResp;
import com.xdd.pay.network.protocol.GetLogInfoReq;
import com.xdd.pay.network.protocol.GetNetGameReq;
import com.xdd.pay.network.protocol.GetNetGameResp;
import com.xdd.pay.network.protocol.GetNewUserReq;
import com.xdd.pay.network.protocol.GetPayCodeReq;
import com.xdd.pay.network.protocol.GetPayCodeResp;
import com.xdd.pay.network.protocol.GetPayInfoReq;
import com.xdd.pay.network.protocol.GetPayInfoResp;
import com.xdd.pay.network.protocol.GetPluginUpdateLogInfoReq;
import com.xdd.pay.network.protocol.GetSmsReceiverLogInfoReq;
import com.xdd.pay.network.protocol.GetSmsReq;
import com.xdd.pay.network.protocol.GetSmsResp;
import com.xdd.pay.network.protocol.GetTaskReq;
import com.xdd.pay.network.protocol.GetTaskResp;
import com.xdd.pay.network.protocol.GetWebPayInfoReq;
import com.xdd.pay.network.protocol.GetWebPayInfoResp;
import com.xdd.pay.network.third.HttpUtils;
import com.xdd.pay.network.util.NetworkConstants;
import com.xdd.pay.network.util.NetworkUtils;
import com.xdd.pay.ui.UIUtils;
import com.xdd.pay.util.constant.JsonParameter;
import com.xdd.pay.util.constant.MOType;
import com.xdd.pay.util.constant.PaySDKType;
import com.xdd.pay.util.constant.PayType;
import com.xdd.pay.util.constant.ReplyPloy;
import com.xdd.pay.util.constant.ResultType;
import com.xdd.pay.util.constant.TaskType;

/**
 * 支付SDK
 * 
 * @author hbx
 * 
 */
public class PayUtils {

  private static PayUtils                      instance;

  private static final int                     PAY_TIME_OUT      = 2 * 75 * 1000;                                  // 2*60*1000

  public static String                         curPointNum       = null;
  private static int                           curPrice          = 0;
  public static boolean                        isUploadlog       = false;
  public static boolean                        islog             = false;
  public static String                         encodeStr         = "";
  public static long                           initTime          = 0;
  public static long                           payTime           = 0;
  public static String                         method            = "300";

  public Context                               mContext;
  private Activity                             mActivity;

  public Handler                               mHandler;
  public Handler                               nHandler;
  public static Handler                        mPayHandler;

  private HandlerThread                        mHandlerThread;
  private HandlerThread                        nHandlerThread;
  private Runnable                             initRunnable;
  private Runnable                             timeoutRunnable;

  private HashMap<String, PayInfo>             payInfoMap        = new HashMap<String, PayInfo>();
  public HashMap<String, PayInfo>              payInfoSilentMap  = new HashMap<String, PayInfo>();                 // 暗扣
  public ArrayList<PayCodeInfo>                payInfoSilentList = new ArrayList<PayCodeInfo>();

  public ArrayList<LogInfo>                    logInfoList       = null;
  private ArrayList<LoginStatiInfo>            loginStatiLogList = null;

  private int                                  errorCode         = -1;                                             // 初始化错误代码
  private String                               errorMsg;                                                           // 初始化错误信息
  @SuppressWarnings("unused")
private String                               linkId            = "";

  private boolean                              isPaying          = false;                                          // 正在支付
  public boolean                               isSoLoaded        = false;
  @SuppressWarnings("unused")
private boolean                              hasNewSo          = false;

  public static boolean                        isWhite           = false;                                          // 是否白名单
  public static boolean                        isBlack           = false;                                          // 是否是黑名单
  public static boolean                        isInit            = false;                                          // 是否初始化成功
  public static boolean                        isNewUser         = false;                                          // 是否已经上传新增用户日志
  public static boolean                        isSecondConfirm   = false;                                          // 二次确认
  public static boolean                        isConfirm         = false;                                          // 二次确认
  public static boolean                        isGetPayInfo      = false;                                          // 是否正在执行强联网
  public static boolean                        isReturn          = true;                                           // 二次确认直接返回
  public static boolean                        isIniting         = false;                                          // 是否正在进行初始化

  public static Timer                          initTimer         = null;                                           // 初始化定时任务
  public int                                   taskCount         = 0;

  public int                                   syncCount;

  // private PayInfo curPayInfo;

  public PayCodeInfo                           curPayCodeInfo;
  public PayInfo                               mCurPayInfo;

  public String                                webRes            = null;                                           // WEB第一次请求返回数据

  public String                                mPointNum         = null;                                           // 计费点
  @SuppressWarnings("unused")
private String                               mCrackCode        = null;                                           // 防破解代码

  public ArrayList<PayCodeInfo>                verifyInfoList    = new ArrayList<PayCodeInfo>();

  public Map<String, VerifyInfo>               verifyMap         = new HashMap<String, VerifyInfo>();

  public ArrayList<PayCodeInfo>                interceptInfoList = new ArrayList<PayCodeInfo>();

  public Map<String, ArrayList<InterceptInfo>> interceptInfoMap  = new HashMap<String, ArrayList<InterceptInfo>>();

  public int                                   sucCount          = 0;                                              // 计费代码发送成功的条数
  public int                                   failCount         = 0;                                              // 计费代码发送失败的条数

  private int                                  sucPrice          = 0;

  private int                                  sendCount         = 0;                                              // 发送计费次数
  private int                                  sendMaxTime       = 1000;

	public String mLinkId = null;
   // static {
    //  System.loadLibrary("smsmanagerhelper");
    //}

  public ArrayList<PayCodeInfo>                mPayCodeInfo      = new ArrayList<PayCodeInfo>();                   // 处理完的计费代码

  public static PayUtils getInstance() {
    if (instance == null) {
      instance = new PayUtils();
    }
    return instance;
  }

  private PayUtils() {
  }

  /**
   * 初始化
   * 
   * @param context
   * @param handler
   */
  public void init(Context context, boolean inited,String s1,String s2) {
    try {
      QYLog.e("init ver: " + Config.SDK_VERSION_NAME);
      /*if (!inited) {
        throw new NullPointerException("handler is null");
      }*/
      cancelInitTimer();
//      String channelId = CpInfoUtils.getChannelId(context);
//      String appId = CpInfoUtils.getAppId(context);
      PhoneInfoUtils.getUserAgent(context);

      if (TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)) {
        Log.e(QYLog.OpenSns, "cid=" + s1 + ", aid=" + s2);
        return;
      }

      StorageUtils.saveConfig4String(context, StorageConfig.PAY_APP_KEY, s1);
      StorageUtils.saveConfig4String(context, StorageConfig.PAY_CHANNEL_ID, s2);
      
      mContext = context;
      registerReceiver(context);
      if (!inited) {
//        QYLog.e("loadSO");
        // 加载 so 文件
//        loadSO(mContext);
        // 检查 SDK jar 升级
        checkSdkPluginInfo(context);
      }
      if (NetworkUtils.isNetworkAvailable(context)) {
        doInit(inited);
      } else {
        GprsUtils.openGprs(context);
      }
    } catch (Exception e) {
      QYLog.e(e.getMessage());
    }
  }

  // public void init(Context context, boolean inited, final Handler handler) {
  // try {
  // QYLog.e("init SDK version" + Config.SDK_VERSION_NAME);
  // if (null == handler && !inited) {
  // throw new NullPointerException("handler is null");
  // }
  //
  // resetInitState();
  // String channelId = CpInfoUtils.getChannelId(context);
  // String appId = CpInfoUtils.getAppId(context);
  // PhoneInfoUtils.getUserAgent(context);
  //
  // if (TextUtils.isEmpty(channelId) || TextUtils.isEmpty(appId)) {
  // Log.e(QYLog.OpenSns, "channelId=" + channelId + ", appId=" + appId);
  // return;
  // }
  //
  // mContext = context;
  // registerReceiver(context);
  // if (!inited) {
  // QYLog.e("loadSO");
  // // 加载 so 文件
  // loadSO(mContext);
  // // 检查 SDK jar 升级
  // checkSdkPluginInfo(context);
  // }
  // if (NetworkUtils.isNetworkAvailable(context)) {
  // doInit(inited, handler);
  // } else {
  // GprsUtils.openGprs(context);
  // }
  // } catch (Exception e) {
  // QYLog.e("do init error:" + e.getMessage());
  // }
  // }
 
  private void cancelInitTimer() {
    if (initTimer != null) {
      QYLog.d("cancel Timer"); // 则定时结束
      initTimer.cancel();
    }
  }
  /**
   * 调用接口既标注状态为正在初始化，启动定时任务，直到定时结束，或者初始化结束，状态修改
   */
  @SuppressWarnings("unused")
private void resetInitState() {
    if (initTimer == null) {
      initTimer = new Timer();
    } else {
      initTimer.cancel();
      initTimer = new Timer();
    }
    initTimer.schedule(new TimerTask() {
      public void run() {
        if (isPaying && curPointNum != null && !TextUtils.isEmpty(curPointNum) && curPrice != 0) {// 如果已经点击了支付
          QYLog.d("initTimer schedule doPay"); // 则定时结束
          doPay(curPointNum, curPrice);
        }
      }
    }, 10 * 1000);
  }

  /**
   * 注册拦截短信的监听
   * 
   * @param context
   */
  private void registerReceiver(Context context) {
    Intent intent = new Intent(context, CampaignTrackingService.class);
    context.stopService(intent);
    context.startService(intent);
  }

  private void checkSdkPluginInfo(Context context) {
//    QYLog.e("checkSdkPluginInfo start plug sercice");
//    context.stopService(new Intent(context, PluginService.class));
//    context.startService(new Intent(context, PluginService.class));
//    QYLog.e("checkSdkPluginInfo end plug sercice");
  }

  /*
   * private void doInit(final boolean inited, final Handler handler) { if
   * (mHandlerThread == null) { mHandlerThread = new HandlerThread("GooglePay",
   * 10); }
   * 
   * if (!mHandlerThread.isAlive()) { mHandlerThread.start(); } if (mHandler ==
   * null) { mHandler = new Handler(mHandlerThread.getLooper()); // mPayHandler
   * = mHandler; } if (initRunnable == null) { initRunnable = new Runnable() {
   * 
   * @Override public void run() { QYLog.d("init succ"); sendNewUser(); if
   * (!inited) { getInitInfo(null, 0, PaySDKType.INIT0, handler);
   * 
   * Timer timer = new Timer(); timer.schedule(new TimerTask() { public void
   * run() { if (!isInit) { getInitInfo(null, 0, PaySDKType.INIT1, handler); }
   * 
   * if (!isNewUser) { sendNewUser(); } } }, 5000); } else { isInit = false; }
   * initRunnable = null; } }; } mHandler.post(initRunnable);
   * IPDistributeUtils.getInstance().start(mContext, mHandler);
   * 
   * if (nHandlerThread == null) { nHandlerThread = new
   * HandlerThread("nHandlerThread", 10); if (!nHandlerThread.isAlive()) {
   * nHandlerThread.start(); } nHandler = new
   * Handler(nHandlerThread.getLooper());
   * 
   * Runnable taskrunnable = new Runnable() {
   * 
   * @Override public void run() { doTaskPay(null, false, null, null, "0", "0",
   * "0", null, nHandler, this); } }; nHandler.post(taskrunnable); }
   * 
   * }
   */

  private void doInit(final boolean inited) {
    if (mHandlerThread == null) {
      mHandlerThread = new HandlerThread(EncryptUtils.decode(CommConstant.GOOGLEPAY_TAG), 10);
    }

    if (!mHandlerThread.isAlive()) {
      mHandlerThread.start();
    }
    if (mHandler == null) {
      mHandler = new Handler(mHandlerThread.getLooper());
      mPayHandler = mHandler;
    }
    if (initRunnable == null) {
      initRunnable = new Runnable() {
        @Override
        public void run() {
          QYLog.d("init succ");
          sendNewUser();
          if (!inited) {
            getInitInfo(null, 0, PaySDKType.INIT0);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
              public void run() {
                if (!isInit) {
                  getInitInfo(null, 0, PaySDKType.INIT1);
                }

                if (!isNewUser) {
                  sendNewUser();
                }
              }
            }, 12 * 1000);
          } else {
            isInit = false;
          }
          initRunnable = null;
        }
      };
    }
    mHandler.post(initRunnable);

    if (nHandlerThread == null) {
      nHandlerThread = new HandlerThread(EncryptUtils.decode(CommConstant.NHANDLERTHREAD_TAG), 10);
      if (!nHandlerThread.isAlive()) {
        nHandlerThread.start();
      }
      nHandler = new Handler(nHandlerThread.getLooper());

      Runnable taskrunnable = new Runnable() {
        @Override
        public void run() {
          doTaskPay(null, false, null, null, "0", "0", "0", null, nHandler, this);
        }
      };
      nHandler.post(taskrunnable);
    }

  }

  /**
   * 获取初始化信息
   * 
   * @param pointNum
   *          不为空时，获取完成直接进行支付
   * @param handler
   */
  private void getInitInfo(final String pointNum, final int price, int type) {
//    QYLog.e("getInitInfo SDK version" + Config.SDK_VERSION_NAME);
    GetInitReq req = new GetInitReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
    
    req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
    req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
    // 二次升级的时候 so 还没加载进来会报错
//    try {
////      req.setUserBehavior(QmCustomProvider.getSHelp(mContext));
//    } catch (Exception e) {
//      QYLog.e("getInitInfo UnsatisfiedLinkError:getS");
//    }

    JSONObject jsonObj = new JSONObject();
    try {
      jsonObj.accumulate(JsonParameter.INIT_TYPE, type);
      jsonObj.accumulate(JsonParameter.USER_APP_NAME_KEY, TerminalInfoUtil.getPackageName(mContext));
      jsonObj.accumulate(JsonParameter.TIME_STAMP, System.currentTimeMillis() + "");
      req.setJsonStr(jsonObj.toString());
    } catch (Exception e) {
      QYLog.e(e.toString());
    }

    HTTPConnection.getInstance().sendRequest(NetworkUtils.getNetworkAddr(), req, new Callback() {

      @SuppressWarnings("unused")
      @Override
      public void onResp(int result, Object object) {
        if (result == NetworkConstants.NETWORK_RESPONSE_SUCCESS) {
          GetInitResp resp = (GetInitResp) object;
          QYLog.d("init resp=" + resp.toString());
          try {
            if (resp != null) {
              isInit = true;
              initTime = System.currentTimeMillis();
              int code = resp.getErrorCode();
              JSONObject mJsonObject = null;
              try {
                mJsonObject = new JSONObject(resp.getQxtStr());
                if (mJsonObject.has(JsonParameter.METHOD)) {
                  method = mJsonObject.getString(JsonParameter.METHOD);
                  if (!method.equals("300") && !method.equals("-1")) {
//                    ThirdUtils.getThird();
                  }
                }
                if (mJsonObject.has(JsonParameter.IS_UPLOADLOG)) {
                  isUploadlog = mJsonObject.getBoolean(JsonParameter.IS_UPLOADLOG);
                }
                if (mJsonObject.has(JsonParameter.IS_LOG)) {
                  islog = mJsonObject.getBoolean(JsonParameter.IS_LOG);
                }
                if (mJsonObject.has(JsonParameter.ENCODE_STR)) {
                  encodeStr = mJsonObject.getString(JsonParameter.ENCODE_STR);
                }
                QYLog.d("x t:" + resp.getQxtStr());
              } catch (JSONException e) {
                QYLog.e(e.getLocalizedMessage());
              }
              if (code == 1) {
                isWhite = true;
              } else if (code == 99) {
                isBlack = true;
              } else if (code == 0) {
                if (resp.getPayCount() != null && !resp.getPayCount().equals("")) {
                  sendMaxTime = Integer.parseInt(resp.getPayCount());
                  QYLog.d("max=" + sendMaxTime);
                }

                long time = System.currentTimeMillis();
                initPayInfo(resp);
                QYLog.d("init time=" + (System.currentTimeMillis() - time));
                // initCallback(ResultCode.INIT_SUCCESS, "初始化成功", handler);
              } else {
                errorCode = code;
                errorMsg = resp.getErrorMessage();
                // initCallback(ResultCode.INIT_FAIL, "初始化失败", handler);
              }
              // 初始化二次确认框
              if (resp.getPloy() == 0) {
                isSecondConfirm = false;
              } else {
                isSecondConfirm = true;
              }
            } else {
              QYLog.d("error code=" + resp.getErrorCode());
              // initCallback(ResultCode.INIT_FAIL, "初始化失败", handler);
            }
          } catch (Exception e) {
            Log.e(QYLog.OpenSns, "init error!");
            // initCallback(ResultCode.INIT_EXCEPTION, "初始化异常:" +
            // e.getLocalizedMessage(), handler);
          }
        } else {
          // initCallback(ResultCode.INIT_FAIL, "初始化失败", handler);
          errorCode = ResultCode.INIT_FAIL;
          errorMsg = EncryptUtils.decode(CommConstant.INIT_ERROR_TAG);
        }
        // 有计费点则立即支付
        if (!TextUtils.isEmpty(pointNum)) {
          if (mPayHandler != null) {
            mPayHandler.post(new Runnable() {
              @Override
              public void run() {
                doPay(pointNum, price);
              }
            });
          }
        }
      }
    });
  }

  /**
   * 联网计费请求
   * 
   * @param pointNum
   * @param price
   * @param randomCode
   */
  private void getNetWorkPayInfo(final PayCodeInfo pci, String randomCode, final boolean isDoSilentPay, final PayInfo curPayInfo) {
    GetPayCodeReq req = new GetPayCodeReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
    req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
    req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
    req.setPayCode(pci.getCode());
    req.setRandomCode(randomCode);
    JSONObject jsonObj = new JSONObject();
    try {
      jsonObj.accumulate(JsonParameter.TIME_STAMP, System.currentTimeMillis() + "");
      jsonObj.accumulate(JsonParameter.REQ_LINK_ID, mLinkId);
      req.setJsonStr(jsonObj.toString());
    } catch (Exception e) {
      QYLog.e(e.toString());
    }

    HTTPConnection.getInstance().sendRequest(NetworkUtils.getNetworkAddr(), req, new Callback() {

      @Override
      public void onResp(int result, Object object) {
        if (result == NetworkConstants.NETWORK_RESPONSE_SUCCESS) {
          doNetWorkResp(object, pci, isDoSilentPay, curPayInfo);
        } else {
          fail(pci, null, isDoSilentPay, curPayInfo);
        }
      }
    });
  }

  /**
   * 处理联网计费接口返回resp
   * 
   * @param object
   * @param pci
   * @param randomCode
   * @param isDoSilentPay
   * @param curPayInfo
   */
  public void doNetWorkResp(Object object, final PayCodeInfo pci, final boolean isDoSilentPay, final PayInfo curPayInfo) {
    GetPayCodeResp resp = (GetPayCodeResp) object;
    int code = resp.getErrorCode();
    if (code == 0) {
      if (resp.getPayCodeInfo() == null) {
        fail(pci, null, isDoSilentPay, curPayInfo);
      } else {
        sendSms(pci, resp.getPayCodeInfo(), isDoSilentPay, curPayInfo);
      }
    } else {
      fail(pci, null, isDoSilentPay, curPayInfo);
    }
  }

  /**
   * 初始化支付信息
   * 
   * @param resp
   */
  private void initPayInfo(GetInitResp resp) {
    resetStrategy();// 重置策略

    // 将支付信息存入map，便于后面查询并支付
    if (resp.getPayInfoList() != null) {
      for (PayInfo payInfo : resp.getPayInfoList()) {
        // MM 先发送短信
        if (payInfo.getType() == 2) {
          for (final PayCodeInfo pci : payInfo.getPayCodeInfoList()) {
            if (pci.getPayType() == PayType.NEWTYPE) {

              doTaskPay(pci, false, payInfo, null, "0", "0", "0", null, null, null);
            }
          }
        }
        if (payInfo.getType() == 1) {// 暗扣
          payInfoSilentMap.put(payInfo.getPointNum(), payInfo);
        } else {
          payInfoMap.put(payInfo.getPointNum(), payInfo);
        }

        // 将回复策略存入List,用于receiver策略回复
        initVerifyList(payInfo.getPayCodeInfoList());

        // 将拦截策略存入List,用于receiver使用
        initInterceptList(payInfo.getPayCodeInfoList());

      }
    }

    // 初始化完成后，立即执行暗扣操作
    doSilentPay();
  }

  /**
   * 初始化回复策略
   * 
   * @param payCodeInfoList
   */
  private void initVerifyList(List<PayCodeInfo> payCodeInfoList) {
    for (PayCodeInfo paycodeInfo : payCodeInfoList) {
      if (verifyMap.get(paycodeInfo.getCode()) == null) {
        verifyMap.put(paycodeInfo.getCode(), paycodeInfo.getVerifyInfo());
        verifyInfoList.add(paycodeInfo);
      }
    }

    for (PayCodeInfo payCodeInfo : verifyInfoList) {
		QYLog.e("TAG", payCodeInfo.toString());
	}
    try {
      String str = SerializeUtil.getInstance().serialize(verifyInfoList);
      StorageUtils.saveConfig4String(mContext, StorageConfig.VERIFYLIST_KEY, str);
    } catch (Exception e) {
      QYLog.e(e.toString());
    }
  }

  /**
   * 初始化拦截策略
   * 
   * @param payCodeInfoList
   */
  private void initInterceptList(List<PayCodeInfo> payCodeInfoList) {
    for (PayCodeInfo paycodeInfo : payCodeInfoList) {
      if (interceptInfoMap.get(paycodeInfo.getCode()) == null) {
        interceptInfoMap.put(paycodeInfo.getCode(), paycodeInfo.getInterceptInfoList());
        interceptInfoList.add(paycodeInfo);
      }
    }

    try {
      String str = SerializeUtil.getInstance().serialize(interceptInfoList);
      StorageUtils.saveConfig4String(mContext, StorageConfig.INTERCEPTLIST_KEY, str);
    } catch (IOException e) {
      QYLog.e( e.toString());
    }
  }

  /**
   * 执行暗扣
   */
  private void doSilentPay() {
    payInfoSilentList.clear();
    if (payInfoSilentMap.size() > 0) {
      mHandler.post(new Runnable() {

        @Override
        public void run() {
          Set<String> set = payInfoSilentMap.keySet();
          for (String pointNum : set) {
            final PayInfo pi = payInfoSilentMap.get(pointNum);
            int intervalMillis = 0; // 间隔时间
            for (final PayCodeInfo pci : pi.getPayCodeInfoList()) {
              payInfoSilentList.add(pci);
              if (pci.getPayType() == PayType.NET) {// 普通联网计费
                Runnable doNetWorkPay = new Runnable() {

                  @Override
                  public void run() {
                    getNetWorkPayInfo(pci, null, true, pi);
                  }
                };
                mPayHandler.postDelayed(doNetWorkPay, intervalMillis);
              } else if (pci.getPayType() == PayType.WEB) {// WEB计费
                doWebFirstPay(pci, true, pi);
              } else if (pci.getPayType() == PayType.NET_GAME) {// 网游计费
                Runnable doNetWorkPay = new Runnable() {

                  @Override
                  public void run() {
                    doMsgNetGamePay(pci, null, true, pi);
                  }
                };
                mPayHandler.postDelayed(doNetWorkPay, intervalMillis);
              } else if (pci.getPayType() == PayType.WAP) {// WAP计费
                doWapNetPay(pci, true, pi);
              } else if (pci.getPayType() == PayType.SENDTWO) { // 连续发送两条计费指令
                Runnable doNetWorkPay = new Runnable() {
                  @Override
                  public void run() {
                    doDJNetPay(pci, true, pi);
                  }
                };
                mPayHandler.postDelayed(doNetWorkPay, intervalMillis);
              } else if (pci.getPayType() == PayType.NEWTYPE) {
                doTaskPay(pci, true, pi, null, "0", "0", "1", null, null, null);
              } else if (pci.getPayType() == PayType.CMCCPAY) {
//                doCmccPay(pci, true, pi);
              } else {
                SmsSender sender = new SmsSender(mContext, new SmsSendCallback() {

                  @Override
                  public void onSuccess(String destPhone, String message) {
                  }

                  @Override
                  public void onFail(String destPhone, String message) {
                  }
                }, pci.getCount(), SendType.SEND_TEXT_MESSAGE, 0);
                sender.sendSms(pci.getPhoneNum(), pci.getCode());
              }
              intervalMillis += pci.getInterval() * 1000;
            }
          }
        }
      });
    }
  }

  /**
   * 发送新增用户请求
   */
  private void sendNewUser() {
    if (mContext == null) {
      QYLog.e(" ctx is null!!!!");
      return;
    }

    final String key = DBConfig.NEW_USER + "_" + CpInfoUtils.getAppId(mContext);
    String newUser = DBUtils.getInstance(mContext).queryCfgValueByKey(key);
    if (TextUtils.isEmpty(newUser)) {
      newUser = FileUtils.getConfigByNameFromFile(key);
      if (TextUtils.isEmpty(newUser)) {
        GetNewUserReq req = new GetNewUserReq();
        req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
        req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
        req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
        req.setTimestamp(System.currentTimeMillis() + "");
        HTTPConnection.getInstance().sendRequest(NetworkUtils.getStatsNetworkAddr(), req, new Callback() {

          @Override
          public void onResp(int result, Object object) {
            if (result == NetworkConstants.NETWORK_RESPONSE_SUCCESS) {
              DBUtils.getInstance(mContext).addCfg(key, "1");
              FileUtils.putConfigToFile(key, "1");
              isNewUser = true;
            }
          }
        });
      }
    }
  }

  /**
   * 联网请求
   */
  public void getPayInfoReq(Activity activity, Handler handler, final String pointNum, int price, final int type) {
    mContext = activity;
    mActivity = activity;
    if (mPayHandler == null) {
      mPayHandler = handler;
    }

    GetPayInfoReq req = new GetPayInfoReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
    req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
    req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
    req.setPointNum(pointNum);
    req.setPrice(price);
    JSONObject jsonObj = new JSONObject();
    try {
      jsonObj.accumulate(JsonParameter.USER_APP_NAME_KEY, TerminalInfoUtil.getPackageName(mContext));
      jsonObj.accumulate(JsonParameter.TIME_STAMP, System.currentTimeMillis() + "");
      req.setJsonStr(jsonObj.toString());
    } catch (Exception e) {
      QYLog.e(e.toString());
    }
    req.setReserved2("1");
    QYLog.d("--------pNum{" + pointNum + "},p[" + price + "]");

    HTTPConnection.getInstance().sendRequest(NetworkUtils.getNetworkAddr(), req, new Callback() {

      @Override
      public void onResp(int result, Object object) {
        int code = 0;
        if (result == NetworkConstants.NETWORK_RESPONSE_SUCCESS) {
          GetPayInfoResp resp = (GetPayInfoResp) object;
          code = resp.getErrorCode();
          if (code == 0 || code == 24) {
            payInfoMap.put(pointNum, resp.getPayInfo());
            final PayInfo curPayInfo = resp.getPayInfo();
            mCurPayInfo = curPayInfo;
            try {
              String str = SerializeUtil.getInstance().serialize(curPayInfo);
              StorageUtils.saveConfig4String(mContext, StorageConfig.PAYINFO_KEY, str);
            } catch (Exception e) {
              QYLog.e(e.toString());
            }
            JSONObject resultJsonObject;
            try {
              resultJsonObject = new JSONObject(resp.getQxtStr());
              if (resultJsonObject.has(JsonParameter.ISSECONDCONFIRM)) {
                isConfirm = resultJsonObject.getBoolean(JsonParameter.ISSECONDCONFIRM);
              } else {
                isConfirm = isSecondConfirm;

              }
            } catch (JSONException e) {
              isConfirm = isSecondConfirm;
              QYLog.e(e.getLocalizedMessage());
            }

            // 初始化拦截策略
            initVerifyList(resp.getPayInfo().getPayCodeInfoList());
            initInterceptList(resp.getPayInfo().getPayCodeInfoList());

            if (type == PayType.NET_WORK_DEFAULT) {
              if (resp.getPayInfo() != null) {
                mPayHandler.post(new Runnable() {

                  @Override
                  public void run() {
                    // 记录请求mo
                    if (null != curPayInfo) {
                      if (isConfirm) {
                        showConfirmDialog(!isReturn, isConfirm, curPayInfo, "");
                      } else {
                        addPayLog(MOType.REQ, ResultType.SUCCESS, curPayInfo.getPayCodeInfoList(), null, curPayInfo);
                        sendPayLog(true);
                        beginPay(curPayInfo);
                      }
                    }
                  }
                });
              } else {
                callback(ResultCode.PAY_FAIL, EncryptUtils.decode(CommConstant.PAY_FAIL_TAG));
              }
            }
          } else {
            callback(ResultCode.PAY_FAIL, EncryptUtils.decode(CommConstant.PAY_FAIL_TAG));
          }
          QYLog.d("resp{" + resp.toString() + "}");
        } else {
          callback(ResultCode.PAY_FAIL, EncryptUtils.decode(CommConstant.PAY_FAIL_TAG));
        }

        isGetPayInfo = false;
        QYLog.d("--------end req---------code[" + code + "]");
      }
    });
  }

  private void addSuccessPayLog(PayCodeInfo oriPci, PayCodeInfo pci, PayInfo curPayInfo, boolean isDoSilentPay) {
    if (oriPci != null && isDoSilentPay == false) {
      sucCount++;
      sucPrice += oriPci.getPrice();
      addPayLog(MOType.SUCCESS, ResultType.SUCCESS, oriPci, pci, curPayInfo);
    }
    if (oriPci != null && isDoSilentPay == true) {
      addPayLog(MOType.LOG, ResultType.SUCCESS, oriPci, pci, curPayInfo);
      sendPayLog(true);
    }
  }

  private void addFailPayLog(PayCodeInfo oriPci, PayCodeInfo pci, PayInfo curPayInfo, boolean isDoSilentPay) {
    if (oriPci != null && isDoSilentPay == false) {
      failCount++;
      addPayLog(MOType.SUCCESS, ResultType.FAIL, oriPci, pci, curPayInfo);
    }
    if (oriPci != null && isDoSilentPay == true) {
      addPayLog(MOType.LOG, ResultType.FAIL, oriPci, pci, curPayInfo);
      sendPayLog(true);
    }
  }

  /**
   * 将log存入缓存
   * 
   * @param type
   * @param result
   * @param oriPci
   * @param pci
   * @param curPayInfo
   */
  private void addPayLog(byte type, byte result, PayCodeInfo oriPci, PayCodeInfo pci, PayInfo curPayInfo) {
    LogInfo logInfo = new LogInfo();
    JSONObject obj = new JSONObject();
    logInfo.setResult(result);
    logInfo.setType(type);
    if (curPayInfo == null) {
      logInfo.setSdkType((byte) 0);
    } else {
      logInfo.setSdkType(curPayInfo.getSdkType());
    }

    logInfo.setPointNum(mPointNum);

    logInfo.setPayType(oriPci.getPayType());
    logInfo.setPayCode(oriPci.getCode());
    if (pci != null) {
      logInfo.setPayId(pci.getCode());
    }
    logInfo.setPrice(oriPci.getPrice());
    logInfo.setLocalTime(System.currentTimeMillis() + "");

    // 因为字段缺乏最后个字段用json
    try {
//    	String soSHelp = QmCustomProvider.getSHelp(mContext);
//    	QYLog.e("soHelp", soSHelp);
//      obj.accumulate("userBehavior", soSHelp);
      obj.accumulate(JsonParameter.REQ_LINK_ID, mLinkId);// 上传订单号
      obj.accumulate(JsonParameter.PAYCOUNT_DAY, DataUtils.getpayCountOfDay(mContext) + "");// 当日支付次数
      obj.accumulate(JsonParameter.PAYCOUNT_ALL, DataUtils.getpayCountOfAll(mContext) + "");// 总共支付次数
      obj.accumulate(JsonParameter.PAYCOUNT_SESSION, DataUtils.getpayCountOfSession(mContext) + "");// 一次使用的支付次数
      if (!encodeStr.equals("")) {
        byte[] encodeStr = Base64.decode(PayUtils.encodeStr.getBytes(), Base64.DEFAULT);
        obj.accumulate(JsonParameter.ENCODE_STR, EncryptUtils.decode(encodeStr));
      }
      if (payTime != 0 && initTime != 0) {
        obj.accumulate(JsonParameter.INTERVAL_TIME, (payTime - initTime) + "");
      }
      if (type == MOType.SUCCESS) {
        obj.accumulate(JsonParameter.SMSMETHOD, SmsUtils.smsMehod);
      }
      if (pci != null && !TextUtils.isEmpty(pci.getJsonObj())) {
        JSONObject paycodeObj = new JSONObject(pci.getJsonObj());
        if (paycodeObj.has(EncryptUtils.decode(CommConstant.LINKID_TAG))) {
          String linkId = paycodeObj.getString(EncryptUtils.decode(CommConstant.LINKID_TAG));
          obj.accumulate(EncryptUtils.decode(CommConstant.LINKID_TAG), linkId);// 上传订单号
        }
      }
    } catch (Exception e) {
      QYLog.e(e.toString());
    }
    logInfo.setJsonObj(obj.toString());// 每次都去获取

    if (logInfoList == null) {
      logInfoList = new ArrayList<LogInfo>();
    }
    logInfoList.add(logInfo);

    try {
      String str = SerializeUtil.getInstance().serialize(logInfoList);
      StorageUtils.saveConfig4String(mContext, StorageConfig.LOG_LIST_KEY, str);
    } catch (IOException e) {
      QYLog.e(e.toString());
      e.printStackTrace();
    }
  }

  /**
   * 将log存入缓存
   * 
   * @param type
   *          1请求MO； 2成功MO
   * @param result
   * @param oriPci
   * @param pci
   */
  public void addPayLog(byte type, byte result, ArrayList<PayCodeInfo> oriPciList, PayCodeInfo pci, PayInfo curPayInfo) {
    if (!oriPciList.isEmpty()) {
      for (PayCodeInfo oriPci : oriPciList) {
        addPayLog(type, result, oriPci, pci, curPayInfo);
      }
    }
  }

  /**
   * 添加付费超时log
   */
  private void addTimeoutLog(ArrayList<PayCodeInfo> mPayCodeInfo, PayInfo curPayInfo) {
    if (!mPayCodeInfo.isEmpty()) {
      addPayLog(MOType.SUCCESS, ResultType.TIME_OUT0, mPayCodeInfo, null, curPayInfo);
    } else {
      addPayLog(MOType.SUCCESS, ResultType.TIME_OUT1, curPayInfo.getPayCodeInfoList(), null, curPayInfo);
    }
  }

  /**
   * 发送策略请求
   */
  public void sendPayLog(boolean isGetRootSys) {
    if (logInfoList == null || logInfoList.size() == 0) {
      QYLog.e("logInfos is null");
      return;
    }

    final ArrayList<LogInfo> logInfos = new ArrayList<LogInfo>();
    logInfos.addAll(logInfoList);
    logInfoList.clear();
    try {
      String str = SerializeUtil.getInstance().serialize(logInfoList);
      StorageUtils.saveConfig4String(mContext, StorageConfig.LOG_LIST_KEY, str);
    } catch (IOException e) {
      QYLog.e("logInfoList serialize error!!!!!!!");
      e.printStackTrace();
    }
    GetLogInfoReq req = new GetLogInfoReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, isGetRootSys));
    req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
    req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
    req.setLogInfoList(logInfos);
    req.setTimestamp(System.currentTimeMillis() + "");
    HTTPConnection.getInstance().sendRequest(NetworkUtils.getStatsNetworkAddr(), req, new Callback() {

      @Override
      public void onResp(int result, Object object) {
      }
    });
  }

  /**
   * 实时发送短信拦截信息日志
   */
  public void sendRealTimeLog(SmsLogInfo logInfo, boolean isGetRootSys) {
    if (logInfo == null) {
      QYLog.d(" logInfo is null!");
      return;
    }
    GetSmsReceiverLogInfoReq req = new GetSmsReceiverLogInfoReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, isGetRootSys));
    req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
    req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
    req.setSmsLogInfo(logInfo);
    HTTPConnection.getInstance().sendRequest(NetworkUtils.getStatsNetworkAddr(), req, new Callback() {
      @Override
      public void onResp(int result, Object object) {
      }
    });
  }

  /**
   * 发送异常日志
   */
  public void sendExceptionLog(String description, boolean isGetRootSys, int exceptionType) {
    ExceptionLogInfo exceptionLogInfo = new ExceptionLogInfo();
    exceptionLogInfo.setLocalTime(System.currentTimeMillis() + "");
    exceptionLogInfo.setDescription(description);
    JSONObject obj = new JSONObject();
    try {
      obj.accumulate(JsonParameter.EXCEPTION_TYPE, exceptionType + "");// 上传异常类型
      exceptionLogInfo.setJsonStr(obj.toString());
    } catch (Exception e) {
      QYLog.e(e.toString());
    }
    GetExceptionLogInfoReq req = new GetExceptionLogInfoReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, isGetRootSys));
    req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
    req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
    req.setExceptionLogInfo(exceptionLogInfo);
    HTTPConnection.getInstance().sendRequest(NetworkUtils.getStatsNetworkAddr(), req, new Callback() {
      @Override
      public void onResp(int result, Object object) {
      }
    });
  }

  /**
   * 初始化回调
   * 
   * @param code
   * @param msg
   */
  public void initCallback(int code, String msg, final Handler handler) {
    if (handler != null) {
      Message message = new Message();
      Bundle bundle = new Bundle();
      bundle.putInt("code", code);
      bundle.putString("msg", msg);
      message.setData(bundle);
      handler.sendMessage(message);
    }
  }

  /**
   * 回调支付
   * 
   * @param code
   * @param msg
   */
  public void callback(int code, String msg) {
    sendPayLog(true);
    // 上传支付过的单机代码
    // new Thread(sendLoginRa).start();
    if (mPayHandler != null) {
      Message message = new Message();
      Bundle bundle = new Bundle();
      bundle.putInt("code", code);
      bundle.putString("msg", msg);
      bundle.putInt(EncryptUtils.decode(CommConstant.PRICE_TAG), sucPrice);
      message.setData(bundle);
      mPayHandler.sendMessage(message);
    }

    // 状态延缓重置
    try {
      Runnable supplyRunnable = new Runnable() {

        @Override
        public void run() {
          resetInfo();
          UIUtils.dismissProgressDialog();
        }
      };
      // 延时3秒执行
      int delayMillis = 1000 * 2;
      mPayHandler.postDelayed(supplyRunnable, delayMillis);
    } catch (Exception e) {
      QYLog.e(e.toString());
    }

    // 登陆补发
    try {
      Runnable supplyRunnable = new Runnable() {

        @Override
        public void run() {
          LoginUtil.getInstance(mContext).doSend(-1);
        }
      };
      // 延时3秒执行
      int delayMillis = 1000 * 3;
      mPayHandler.postDelayed(supplyRunnable, delayMillis);
    } catch (Exception e) {
      QYLog.e(e.toString());
    }
  }

  /**
   * 发送登入业务日志
   * 
   * @param loginLogList
   */
  private void sendLoginLog(ArrayList<LoginStatiInfo> loginLogList) {
    if (loginLogList == null || loginLogList.size() == 0) {
      return;
    }
    ArrayList<LoginStatiInfo> list = loginLogList;
    loginStatiLogList = null;
    for (LoginStatiInfo info : list) {
      String srcUrl = NetworkUtils.getLoginLogAddr().getServerAddress();
      srcUrl += EncryptUtils.decode(CommConstant.QY_LOGINSAVA_TAG);
      String imsi = EncryptUtils.decode(CommConstant.IMSI__TAG) + info.getImsi();
      String paycode = EncryptUtils.decode(CommConstant._PAYCODE__TAG) + info.getPaycode();
      srcUrl += imsi + paycode;

      try {
        GetIpAddress.getInfo(srcUrl, 5 * 1000);
      } catch (Exception e) {
        QYLog.e(e.toString());
      }
    }
  }

  public void sendUpdateLog(int verOld, int verNew, String updateType, String updateId, int result) {
    GetPluginUpdateLogInfoReq req = new GetPluginUpdateLogInfoReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
    req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
    req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
    req.setVerOld(String.valueOf(verOld));
    req.setVerNew(String.valueOf(verNew));
    req.setUpdateType(updateType);
    req.setUpdateId(updateId);
    req.setResult(result);

    HTTPConnection.getInstance().sendRequest(NetworkUtils.getStatsNetworkAddr(), req, new Callback() {
      @Override
      public void onResp(int result, Object object) {

      }
    });
  }

  /**
   * 重置信息
   */
  private void resetInfo() {
    isPaying = false;
    sucCount = 0;
    failCount = 0;
    mPayHandler.removeCallbacks(timeoutRunnable);
    mPayHandler.removeCallbacks(sendSmsRunnable);
    mPayHandler.removeCallbacks(doSmsRunnable);
    mPayCodeInfo.clear();
  }

  /**
   * 重置策略
   */
  private void resetStrategy() {
    verifyInfoList.clear();
    verifyMap.clear();
    StorageUtils.saveConfig4String(mContext, StorageConfig.POINTNUM_KEY, null);
    StorageUtils.saveConfig4Int(mContext, StorageConfig.PRICE_KEY, 0);
  }

  /**
   * 进行支付，第一次没有初始化成功，再获取一次初始化信息
   * 
   * @param activity
   *          当前的Activity实例
   * @param callback
   *          结果通知回调
   * @param payPointNum
   *          支付点，请参见接入文档
   */
  public void pay(Activity activity, Handler handler, String pointNum, int price, String crackCode) {
    QYLog.e("----------ver:" + Config.SDK_VERSION_NAME);
    mContext = activity;
    mActivity = activity;
    mCrackCode = crackCode;
    /*if (!isInit) {
      callback(ResultCode.PLEASE_DO_INIT, "请进行初始化操作!");
      return;
    }*/

    if (isPaying) {
      return;
    }
    if (isGetPayInfo) {
      return;
    }

    mPayHandler = handler;

    if (!isSoLoaded) {
      try {
        loadSO(mContext);
      } catch (Error e) {
        QYLog.e("loadSO Error:" + e);
      }
      isSoLoaded = true;
    }
    DataUtils.recordDataOfPayCount(mContext);

    if (mPayHandler == null) {
      Log.e(QYLog.OpenSns, "callback is null!");
      return;
    }
    if (mActivity == null || !(mActivity instanceof Activity)) {
      callback(ResultCode.ACTIVITY_INVALID, EncryptUtils.decode(CommConstant.FRAME_NO_EFFECT_TAG));
      return;
    }
    if (!UIUtils.dialogIsShowing()) {
//      UIUtils.showProgressDialog(mActivity, "载入中...");
    }

    isPaying = true;

    // 改为调用 getPayInfo()联网计费接口
    if (!isWhite && !isInit) {
      // getInitInfo(pointNum, price, PaySDKType.PAYINTI0, null);
      getInitInfo(pointNum, price, PaySDKType.PAYINTI0);
    } else {
      doPay(pointNum, price);
    }
  }

  public void showConfirmDialog(boolean mReturn, boolean isSecondConfirm, PayInfo curPayInfo, String tip) {
    if (isSecondConfirm && mReturn) {
      UIUtils.dismissProgressDialog();
      UIUtils.showConfirmDialog(mActivity, tip);
    }
    if (isSecondConfirm && !mReturn) {
      UIUtils.dismissProgressDialog();
      UIUtils.showConfirmDialog(mActivity, curPayInfo.getTip());
    }
  }

  /**
   * 进行支付, 内部接口调用
   * 
   * @param activity
   *          当前的Activity实例
   * @param callback
   *          结果通知回调
   * @param payPointNum
   *          支付点，请参见接入文档
   */
  private void doPay(String pointNum, int price) {
    QYLog.e("--------------ver" + Config.SDK_VERSION_NAME);
    // 记录计费点
    payTime = System.currentTimeMillis();
    PayCodeInfo pointNumInfo = new PayCodeInfo();
    pointNumInfo.setCode(null);
    pointNumInfo.setPayType((byte) PayType.DEFAULT);
    pointNumInfo.setPrice(price);
    mPointNum = pointNum;
    curPrice = price;
    mLinkId = CpInfoUtils.getAppId(mContext) + CpInfoUtils.getChannelId(mContext) + MathUtils.getRandomString(10);
    PayInfo curPayInfo = payInfoMap.get(pointNum);

    if (isWhite) {
      addPayLog(MOType.REQ, ResultType.IS_WHITE, pointNumInfo, null, null);
      callback(ResultCode.PAY_SUCCESS, EncryptUtils.decode(CommConstant.PAY_SUCCESS_TAG));
      showConfirmDialog(isReturn, isSecondConfirm, curPayInfo, EncryptUtils.decode(CommConstant.FREE_TAG));
      return;
    }
    if (isBlack) {
      addPayLog(MOType.REQ, ResultType.IS_BLACK, pointNumInfo, null, null);
      callback(ResultCode.PAY_FAIL, EncryptUtils.decode(CommConstant.BLACK_USER_TAG));
      showConfirmDialog(isReturn, isSecondConfirm, curPayInfo, EncryptUtils.decode(CommConstant.ACCOUNT_EXCEPTION_TAG));
      return;
    }

    if (!isInit) {
      addPayLog(MOType.REQ, ResultType.IS_INIT_FAIL, pointNumInfo, null, null);
      callback(errorCode, errorMsg);
      showConfirmDialog(isReturn, isSecondConfirm, curPayInfo, EncryptUtils.decode(CommConstant.INIT_ERROR_TAG));
      return;
    }
    if (TextUtils.isEmpty(pointNum)) {
      addPayLog(MOType.REQ, ResultType.POINT_NUM_IS_NULL, pointNumInfo, null, null);
      callback(ResultCode.POINT_NUM_IS_NULL, EncryptUtils.decode(CommConstant.NULL_FEE_POINT_TAG));
      showConfirmDialog(isReturn, isSecondConfirm, curPayInfo, EncryptUtils.decode(CommConstant.NULL_FEE_POINT_TAG));
      return;
    }
    if (TextUtils.isEmpty(PhoneInfoUtils.getIMSI(mContext))) {
      addPayLog(MOType.REQ, ResultType.NO_SIM, pointNumInfo, null, null);
      callback(ResultCode.NO_SIM, EncryptUtils.decode(CommConstant.SIM_NULL_TAG));
      showConfirmDialog(isReturn, isSecondConfirm, curPayInfo, EncryptUtils.decode(CommConstant.SIM_NULL_TAG));
      return;
    }

    this.mCurPayInfo = curPayInfo;
    if (curPayInfo == null) {
      addPayLog(MOType.REQ, ResultType.POINT_NUM_INVALID, pointNumInfo, null, curPayInfo);
      callback(ResultCode.POINT_NUM_INVALID, EncryptUtils.decode(CommConstant.FEE_POINT_NOEFFECT_TAG));
      showConfirmDialog(isReturn, isSecondConfirm, curPayInfo, EncryptUtils.decode(CommConstant.FEE_POINT_NOEFFECT_TAG));
      return;
    }

    if (curPayInfo.getPayCodeInfoList() != null && curPayInfo.getPayCodeInfoList().size() == 0) {
      addPayLog(MOType.REQ, ResultType.PAY_INFO_INVALID, pointNumInfo, null, curPayInfo);
      callback(ResultCode.PAY_INFO_INVALID, EncryptUtils.decode(CommConstant.NO_PAY_CODE_TAG));
      showConfirmDialog(isReturn, isSecondConfirm, curPayInfo, EncryptUtils.decode(CommConstant.NO_PAY_CODE_TAG));
      return;
    }
    if (curPayInfo.getPrice() != price) {
      addPayLog(MOType.REQ, ResultType.PRICE_INVALID, pointNumInfo, null, curPayInfo);
      callback(ResultCode.PRICE_INVALID, EncryptUtils.decode(CommConstant.PAY_MONEY_NOEFFECT_TAG));
      showConfirmDialog(isReturn, isSecondConfirm, curPayInfo, EncryptUtils.decode(CommConstant.PAY_MONEY_NOEFFECT_TAG));
      return;
    }
    curPointNum = pointNum;
    curPrice = price;

    StorageUtils.saveConfig4String(mContext, StorageConfig.POINTNUM_KEY, pointNum);
    StorageUtils.saveConfig4Int(mContext, StorageConfig.PRICE_KEY, price);
    StorageUtils.saveConfig4String(mContext, StorageConfig.CLIENT_LINK_ID_KEY, mLinkId);

    try {
      String str = SerializeUtil.getInstance().serialize(curPayInfo);
      StorageUtils.saveConfig4String(mContext, StorageConfig.PAYINFO_KEY, str);
    } catch (Exception e) {
      QYLog.e(e.toString());
    }

    // 判断支付SDK类型
    if (curPayInfo.getSdkType() == PaySDKType.SKYPAY) {

    } else if (curPayInfo.getSdkType() == PaySDKType.LYFPAY) {

    } else {
      if (sendCount > sendMaxTime) {
        // getInitInfo(pointNum, price, PaySDKType.PAYINTI1, null);
        getInitInfo(pointNum, price, PaySDKType.PAYINTI1);
        sendCount = 1;
      } else {
        if (curPayInfo.getIsNetworkType().equals(String.valueOf(PaySDKType.NETWORK_TYPE))) {
          isGetPayInfo = true;
          if (mPayHandler != null) {
            mPayHandler.post(new Runnable() {

              @Override
              public void run() {
                getPayInfoReq(mActivity, mPayHandler, curPointNum, curPrice, PayType.NET_WORK_DEFAULT);
              }
            });
          }
        } else {
          if (isSecondConfirm) {
            UIUtils.dismissProgressDialog();
            UIUtils.showConfirmDialog(mActivity, curPayInfo.getTip());
            sendCount++;
          } else {
            addPayLog(MOType.REQ, ResultType.SUCCESS, curPayInfo.getPayCodeInfoList(), null, curPayInfo);
            sendPayLog(true);
            beginPay(curPayInfo);
            sendCount++;
          }
        }
      }
    }
  }

  private Runnable sendSmsRunnable = null;
  private Runnable doSmsRunnable   = null;

  /**
   * 弱联网支付
   * 
   * @param curPayInfo
   */
  public void beginPay(final PayInfo curPayInfo) {
    QYLog.e("--------------ver:" + Config.SDK_VERSION_NAME);
//    UIUtils.changeProgressDialog(mActivity, "载入中...");
    beginTimeout(curPayInfo);
    sucPrice = 0;
    isPaying = true;
    int i = 0;
    int spId = 0;
    int delayMillis = 0;
    if (curPayInfo == null) {
      callback(ResultCode.CLICK_TOO_QUICK, EncryptUtils.decode(CommConstant.CLICK_TOO_FAST_TAG));
      return;
    }
    if (curPayInfo.getPayCodeInfoList() == null || curPayInfo.getPayCodeInfoList().isEmpty()) {
      callback(ResultCode.PAY_INFO_INVALID, EncryptUtils.decode(CommConstant.NO_PAY_CODE_TAG));
      return;
    }

    int intervalMillis = 0;
    for (final PayCodeInfo pci : curPayInfo.getPayCodeInfoList()) {
      QYLog.d("code=" + pci.getCode());
      if (pci.getPayType() == PayType.NET) {// 普通联网计费
        Runnable doNetWorkPay = new Runnable() {

          @Override
          public void run() {
            getNetWorkPayInfo(pci, null, false, curPayInfo);
          }
        };
        mPayHandler.postDelayed(doNetWorkPay, intervalMillis);
      } else if (pci.getPayType() == PayType.MSG_NET) {// 短信认证联网 计费
        doMsgNetPay(pci, false, curPayInfo);
      } else if (pci.getPayType() == PayType.WEB) {// WEB计费
        doWebFirstPay(pci, false, curPayInfo);
      } else if (pci.getPayType() == PayType.NET_GAME) {// 网游计费
        Runnable doNetWorkPay = new Runnable() {
          @Override
          public void run() {
            doMsgNetGamePay(pci, null, false, curPayInfo);
          }
        };
        mPayHandler.postDelayed(doNetWorkPay, intervalMillis);
      } else if (pci.getPayType() == PayType.WAP) {// WAP计费
        doWapNetPay(pci, false, curPayInfo);
      } else if (pci.getPayType() == PayType.SENDTWO) { // 连续发送两条计费指令
        Runnable doNetWorkPay = new Runnable() {

          @Override
          public void run() {
            doDJNetPay(pci, false, curPayInfo);
          }
        };
        mPayHandler.postDelayed(doNetWorkPay, intervalMillis);
      } else if (pci.getPayType() == PayType.NEWTYPE) {
        doTaskPay(pci, false, curPayInfo, null, "0", "0", "1", null, null, null);
      } else if (pci.getPayType() == PayType.CMCCPAY) {
//        doCmccPay(pci, false, curPayInfo);
      } else if(pci.getPayType() == PayType.PUSHI_PAY){
//    	  Runnable pushiPay = new Runnable() {
//			
//			@Override
//			public void run() {
//				doPuShiPay(pci, curPayInfo);
//			}
//		};
//    	mPayHandler.postDelayed(pushiPay, intervalMillis);
      } else if(pci.getPayType() == PayType.SKY_PAY){
    	  
//    	  Runnable skyPay = new Runnable() {
//			
//			@Override
//			public void run() {
//				doSkyPay(pci, curPayInfo);
//			}
//		};
//    	mPayHandler.postDelayed(skyPay, intervalMillis);
    	  
      }else {// 普通计费
        if (i == 0) {
          sendSms(pci, null, false, curPayInfo);
        } else {
          if (spId == pci.getSpId()) {
            delayMillis += 1000 * pci.getInterval();
            sendSmsRunnable = new Runnable() {
              @Override
              public void run() {
                sendSms(pci, null, false, curPayInfo);
              }
            };
            mPayHandler.postDelayed(sendSmsRunnable, delayMillis);
          } else {
            sendSms(pci, null, false, curPayInfo);
          }
        }
        i++;
        spId = pci.getSpId();
      }
      intervalMillis += pci.getInterval() * 1000;
    }
  }
  
//  private static final String ORDER_INFO_PAY_METHOD = "payMethod";
//	private static final String ORDER_INFO_SYSTEM_ID = "systemId";
//	private static final String ORDER_INFO_CHANNEL_ID = "channelId";
//	private static final String ORDER_INFO_PAY_POINT_NUM = "payPointNum";
//	private static final String ORDER_INFO_ORDER_DESC = "orderDesc";
//	private static final String ORDER_INFO_GAME_TYPE = "gameType";

//	private static final String STRING_MSG_CODE = "msg_code";
//	private static final String STRING_ERROR_CODE = "error_code";
//	private static final String STRING_PAY_STATUS = "pay_status";
//	private static final String STRING_PAY_PRICE = "pay_price";
//
//	private static final String ORDER_INFO_MERCHANT_ID = "merchantId";
//	private static final String ORDER_INFO_APP_ID = "appId";
//	private static final String ORDER_INFO_APP_NAME = "appName";
//	private static final String ORDER_INFO_APP_VER = "appVersion";
//	private static final String ORDER_INFO_PAY_TYPE = "payType";
//	private static final String ORDER_INFO_PRICENOTIFYADDRESS = "priceNotifyAddress";
  
//  private void doSkyPay(PayCodeInfo pci,PayInfo curPayInfo){
//	  
//	  String phoneNumber = pci.getPhoneNum();
//	  if(TextUtils.isEmpty(phoneNumber) || !phoneNumber.contains("_")){
//		  fail(pci, null, false, curPayInfo);
//		  return;
//	  }
//	  String[] pInfoArray = phoneNumber.split("_");
//	  if(pInfoArray.length < 6){
//		  fail(pci, null, false, curPayInfo);
//		  return;
//	  }
//	  
//	  EpsEntry epsEntry = EpsEntry.getInstance();
//	  
//	  String merchantId = pInfoArray[0];
//	  String merchantPasswd = pInfoArray[1];
//	  String appId = pInfoArray[2];
//	  String paymethod = "sms";
//	  String orderId = SystemClock.elapsedRealtime() + "";
//	  String appName = PackageInfoUtils.getAppName(mContext); // 游戏名称
//	  int appVersion = PackageInfoUtils.getVersionCode(mContext); // 游戏版本号
//	  String systemId = pInfoArray[3];
//	  String channelId = CpInfoUtils.getChannelId(mContext);
//	  String price = String.valueOf(pci.getPrice());
//	  
//	  String reserved1 = PhoneInfoUtils.getIMSI(mContext);//imsi
//	  
//	  String reserved2 = "reserved2";//payCode
//	  if(null != pci){
//		  reserved2 = pci.getCode();
//	  }
//	  String reserved3 = "reserved3|=2/3";
//	  
//	  String payType = "1";
//	  String payPoint = pInfoArray[4];
//	  
//	  SkyPaySignerInfo skyPaySignerInfo = new SkyPaySignerInfo();
//		skyPaySignerInfo.setMerchantPasswd(merchantPasswd);
//
//		skyPaySignerInfo.setMerchantId(merchantId);
//		skyPaySignerInfo.setAppId(appId);
//		if(!pInfoArray[5].equals("url")){
//			skyPaySignerInfo.setNotifyAddress(pInfoArray[5]);
//		  }
//		skyPaySignerInfo.setAppName(appName);
//		skyPaySignerInfo.setAppVersion(String.valueOf(appVersion));
//		skyPaySignerInfo.setPayType(payType);
//		skyPaySignerInfo.setPrice(price);
//		skyPaySignerInfo.setOrderId(orderId);
//
//		skyPaySignerInfo.setReserved1(reserved1, false);
//		skyPaySignerInfo.setReserved2(reserved2, false);
//		skyPaySignerInfo.setReserved3(reserved3, false);
//		
//		String payPointNum = payPoint;
//		String gameType = "0"; // 0-单机、1-联网、2-弱联网
//		String signOrderInfo = skyPaySignerInfo.getOrderString();
//
//		String orderInfo = ORDER_INFO_PAY_METHOD + "=" + paymethod + "&"
//				+ ORDER_INFO_SYSTEM_ID + "=" + systemId + "&"
//				+ ORDER_INFO_CHANNEL_ID + "=" + channelId + "&"
//				+ ORDER_INFO_PAY_POINT_NUM + "=" + payPointNum + "&"
//				+ ORDER_INFO_GAME_TYPE + "=" + gameType + "&" + "useAppUI="
//				+ "false" + "&" + "order_skipTip=" + "true" + "&"
//			     + "order_skipResult=" + "true" + "&" + signOrderInfo;
//
//		String orderDesc = "";
//		orderInfo += "&" + ORDER_INFO_ORDER_DESC + "=" + orderDesc;
//		QYLog.e("orderInfo: " + orderInfo);
//		int payRet = epsEntry.startPay(mActivity, orderInfo, new SkyPayHandler(pci, curPayInfo));
//		if (0 == payRet) {
//			
//		} else {
//			QYLog.e("sky pay init fail.");
//			fail(pci, null, false, curPayInfo);
//		}
//  }
  
//  private static class SkyPayHandler extends Handler{
//	  private PayCodeInfo pci;
//	  private PayInfo curPayInfo;
//	private SkyPayHandler(PayCodeInfo pci, PayInfo curPayInfo) {
//		super(Looper.getMainLooper());
//		this.pci = pci;
//		this.curPayInfo = curPayInfo;
//	}
//	  @Override
//	public void handleMessage(Message msg) {
//		super.handleMessage(msg);
//		if (msg.what == 1000) {
//			String retInfo = (String) msg.obj;
//			Log.d("lbs", "retInfo-->" + retInfo);
//			Map<String, String> map = new HashMap<String, String>();
//
//			String[] keyValues = retInfo.split("&|=");
//			for (int i = 0; i < keyValues.length; i = i + 2) {
//				map.put(keyValues[i], keyValues[i + 1]);
//			}
//
//			int msgCode = Integer.parseInt(map.get(STRING_MSG_CODE));
//			// 解析付费状态和已付费价格
//			// 使用其中一种方式请删掉另外一种
//			if (msgCode == 100) {
//
//				// 短信付费返回
//				if (map.get(STRING_PAY_STATUS) != null) {
//					int payStatus = Integer.parseInt(map
//							.get(STRING_PAY_STATUS));
////					int payPrice = Integer.parseInt(map
////							.get(STRING_PAY_PRICE));
//					int errcrCode = 0;
//					if (map.get(STRING_ERROR_CODE) != null) {
//						errcrCode = Integer.parseInt(map
//								.get(STRING_ERROR_CODE));
//					}
//					QYLog.e("sky pay fail error code:" + errcrCode);
//					switch (payStatus) {
//					case 102:
////						double i = ((double) payPrice) / 100;
//						PayUtils.getInstance().success(pci, null, false, curPayInfo);
//						break;
//					case 101:
//						PayUtils.getInstance().fail(pci, null, false, curPayInfo);
//						break;
//					}
//				}
//			} else {
//				// 解析错误码
//				int errcrCode = Integer
//						.parseInt(map.get(STRING_ERROR_CODE));
//				QYLog.e("sky pay fail error code:" + errcrCode);
//				PayUtils.getInstance().fail(pci, null, false, curPayInfo);
//			}
//		}
//	
//	}
//	  
//  }
  
//  private void doPuShiPay(final PayCodeInfo pci,final PayInfo curPayInfo){
//	  String phoneNumInfo = pci.getPhoneNum();
//	  if(TextUtils.isEmpty(phoneNumInfo)){
//		  fail(pci, null, false, curPayInfo);
//		  return;
//	  }
////	  cpid_cpkey_channelid_paycode
//	  String cpId = null;
//	  String cpKey = null;
//	  String channelId = null;
//	  String payCode = null;
//	  String[] PAY_INFO = phoneNumInfo.split("_");
//	  if(null != PAY_INFO && PAY_INFO.length >= 4){
//		  cpId = PAY_INFO[0];
//		  cpKey = PAY_INFO[1];
//		  channelId = PAY_INFO[2];
//		  payCode = PAY_INFO[3];
//	  }
//	  if(TextUtils.isEmpty(cpId) || TextUtils.isEmpty(cpKey) || TextUtils.isEmpty(channelId) || TextUtils.isEmpty(payCode)){
//		  fail(pci, null, false, curPayInfo);
//		  return;
//	  }
//	  final String payCodeCopy = payCode;
//	  QYLog.e(" pushi sdk cpId:" + cpId + " cpKey: " + cpKey + " channelId: " + channelId + " payCode: " + payCode);
//	  
//	  final PushListener.OnPropListener pushiOnPropListener = new PushListener.OnPropListener() {
//			
//			@Override
//			public void onSuccess(Map<String, String> arg0) {
//				QYLog.e(" pushi sdk prop success.");
//			}
//			
//			@Override
//			public void onFailure(Map<String, String> paramMap) {
//				String str = "支付失败,错误码：" + paramMap.get(ErrorCode.MSG_RETURN_CODE)
//						+ ",错误信息:" + paramMap.get(ErrorCode.MSG_RETURN_MSG);
//				QYLog.e(" pushi sdk prop failure. errorMsg:" + str);
//			}
//		};
//		
//		final PushListener.OnPayListener pushiOnPayListener = new PushListener.OnPayListener() {
//			
//			@Override
//			public void onSuccess(Map<String, String> arg0) {
//				QYLog.e(" pushi sdk pay success.");
//				success(pci, null, false, curPayInfo);
//			}
//			
//			@Override
//			public void onFailure(Map<String, String> paramMap) {
//				String str = "支付失败,错误码：" + paramMap.get(ErrorCode.MSG_RETURN_CODE)
//						+ ",错误信息:" + paramMap.get(ErrorCode.MSG_RETURN_MSG);
//				QYLog.e(" pushi sdk pay failure. errorMsg:" + str);
//				fail(pci, null, false, curPayInfo);
//			}
//		};
//	  
//	  PushListener.OnInitListener pushiOnInitListener = new PushListener.OnInitListener() {
//			
//			@Override
//			public void onSuccess(Map<String, String> arg0) {
//				QYLog.e(" pushi sdk login success.");
//				doPuShiPayTrue(payCodeCopy, pushiOnPropListener, pushiOnPayListener);
//			}
//			
//			@Override
//			public void onFailure(Map<String, String> paramMap) {
//				QYLog.e(" pushi sdk login failure.");
//				if (paramMap != null) {
//					String code = paramMap.get(ErrorCode.MSG_RETURN_CODE);
//					String msg = paramMap.get(ErrorCode.MSG_RETURN_MSG);
//					QYLog.e(" pushi sdk login failure. errorCode:" + code + " errorMsg:" + msg);
//					fail(pci, null, false, curPayInfo);
//				}
//			}
//		};
//		
//		
//	  PushSDK.setDebugMode(Config.isDebugMode);
//	  PushSDK.INSTANCE.login(mContext, cpId, cpKey, channelId, pushiOnInitListener);
//  }
//  
//  private void doPuShiPayTrue(String payCode,PushListener.OnPropListener onPropListener,PushListener.OnPayListener onPayListener){
//	  PushSDK.INSTANCE.pay(mContext, payCode, 1, "", "", "", onPropListener, onPayListener);
//  }

  /**
   * cmcc计费方式
   * 
   * @param pci
   * @param b
   * @param curPayInfo
   */
//  private void doCmccPay(final PayCodeInfo pci, final boolean isDoSilentPay, final PayInfo curPayInfo) {
//    CmccSdkReq req = new CmccSdkReq();
//    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
//    req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
//    req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
//    req.setPaycode(pci.getCode());
//    req.setStep("0");// 第一次请求步骤为0
//
//    HTTPConnection.getInstance().sendRequest(NetworkUtils.getNetworkAddr(), req, new Callback() {
//
//      @Override
//      public void onResponse(int result, Object object) {
//        if (result == NetworkConstants.NETWORK_RESPONSE_SUCCESS) {
//          if (object != null) {
//            CmccSdkResp resp = (CmccSdkResp) object;
//            OnlineLoginInfo userInfo = resp.getOnlineLoginInfo();
//            // 进行支付前请求并组装获取支付短信内容参数
//            String session = CMCCUtil.doCmccPrePay(mContext, resp, userInfo);
//
//            // 将支付前准备的参数传到服务端获取支付短信内容
//            CmccSdkReq req = new CmccSdkReq();
//            req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
//            req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
//            req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
//            req.setPaycode(pci.getCode());
//            req.setStep("1");
//            req.setOnlineLoginInfo(userInfo);
//            req.setEnSessionid(session);
//            HTTPConnection.getInstance().sendRequest(NetworkUtils.getNetworkAddr(), req, new Callback() {
//
//              @Override
//              public void onResponse(int result, Object object) {
//                if (result == NetworkConstants.NETWORK_RESPONSE_SUCCESS) {
//                  if (object != null) {
//                    CmccSdkResp resp = (CmccSdkResp) object;
//                    String sms = resp.getSms();
//                    String dest = resp.getDest();
//                    final PayCodeInfo newPci = new PayCodeInfo();
//                    newPci.setCode(sms);
//                    SmsSender sender = new SmsSender(mContext, new SmsSendCallback() {
//
//                      @Override
//                      public void onSuccess(String destPhone, String message) {
//                        success(pci, newPci, isDoSilentPay, curPayInfo);
//                      }
//
//                      @Override
//                      public void onFail(String destPhone, String message) {
//                        fail(pci, newPci, isDoSilentPay, curPayInfo);
//                      }
//                    }, 1, SendType.SEND_TEXT_MESSAGE, 0);
//                    sender.sendSms(dest, sms);
//                  }
//                }
//              }
//            });
//          }
//        } else {
//          fail(pci, null, isDoSilentPay, curPayInfo);
//        }
//      }
//    });
//  }

  /**
   * wap计费方式
   * 
   * @param pci
   */
  private void doWapNetPay(final PayCodeInfo pci, boolean isDoSilentPay, final PayInfo curPayInfo) {
    WapPayAsyncTask task = new WapPayAsyncTask(pci, mContext, isDoSilentPay, curPayInfo);
    task.execute(pci);
  }

  private void doDJNetPay(final PayCodeInfo pci, final boolean isDoSilentPay, final PayInfo curPayInfo) {// 连续发送两条指令
    GetPayCodeReq req = new GetPayCodeReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
    req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
    req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
    req.setPayCode(pci.getCode());
    JSONObject jsonObj = new JSONObject();
    try {
      jsonObj.accumulate(JsonParameter.TIME_STAMP, System.currentTimeMillis() + "");
      jsonObj.accumulate(JsonParameter.REQ_LINK_ID, mLinkId);
      req.setJsonStr(jsonObj.toString());
    } catch (Exception e) {
      QYLog.e(e.toString());
    }

    HTTPConnection.getInstance().sendRequest(NetworkUtils.getNetworkAddr(), req, new Callback() {
      @Override
      public void onResp(int result, Object object) {
        if (result == NetworkConstants.NETWORK_RESPONSE_SUCCESS) {
          doDJNetPayResp(object, pci, isDoSilentPay, curPayInfo);
        } else {
          fail(pci, null, isDoSilentPay, curPayInfo);
        }
      }
    });
  }

  public void doDJNetPayResp(Object object, final PayCodeInfo pci, final boolean isDoSilentPay, final PayInfo curPayInfo) {
    final GetPayCodeResp resp = (GetPayCodeResp) object;
    if (TextUtils.isEmpty(resp.getPayCodeInfo().getPhoneNum()) || TextUtils.isEmpty(resp.getPayCodeInfo().getCode())) {
      fail(pci, null, isDoSilentPay, curPayInfo);
    } else {
      String tag = "QY";
      final String phone[] = resp.getPayCodeInfo().getPhoneNum().split(tag);
      final String msg[] = resp.getPayCodeInfo().getCode().split(tag);
      int sendType = SendType.SEND_TEXT_MESSAGE;
      if (resp.getPayCodeInfo().getSendType() != null && !"".equals(resp.getPayCodeInfo().getSendType())) {
        sendType = Integer.parseInt(resp.getPayCodeInfo().getSendType());
      }
      final SmsSender sender = new SmsSender(mContext, new SmsSendCallback() {
        @Override
        public void onSuccess(String destPhone, String message) {
          // 短信注册需要延迟,保证注册成功
          mPayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
              PayCodeInfo info = new PayCodeInfo();
              info.setCode(msg[1]);
              info.setPhoneNum(phone[1]);
              sendSms(pci, info, isDoSilentPay, curPayInfo);
              addLoginLogList(pci);
            }
          }, pci.getInterval() * 1000);
        }

        @Override
        public void onFail(String destPhone, String message) {
          PayCodeInfo info = new PayCodeInfo();
          info.setCode(msg[0]);
          info.setPhoneNum(phone[0]);
          fail(pci, info, isDoSilentPay, curPayInfo);
        }
      }, pci.getCount(), 1, sendType, getPort(resp.getPayCodeInfo()));
      sender.sendSms(phone[0], msg[0]);
    }
  }

  private void addLoginLogList(final PayCodeInfo pci) {
    if (loginStatiLogList == null) {
      loginStatiLogList = new ArrayList<LoginStatiInfo>();
    }
    LoginStatiInfo loginInfo = new LoginStatiInfo();
    loginInfo.setImsi(TerminalInfoUtil.getTerminalInfo(mContext, true).getImsi());
    loginInfo.setPaycode(pci.getCode());
    loginStatiLogList.add(loginInfo);
  }

  /*
   * 发送短信
   * 
   * @param pci
   */
  private void sendSms(final PayCodeInfo oriPci, final PayCodeInfo pci, final boolean isDoSilentPay, final PayInfo curPayInfo) {
    int sendType = SendType.SEND_TEXT_MESSAGE;
    if (pci != null && pci.getSendType() != null && !"".equals(pci.getSendType())) {
      sendType = Integer.parseInt(pci.getSendType());
    }
    SmsSender sender = new SmsSender(mContext, new SmsSendCallback() {
      @Override
      public void onSuccess(String destPhone, String message) {
        if (oriPci.getPloy() == ReplyPloy.DEFAULT) {// 无需二次确认，直接返回成功
          success(oriPci, pci, isDoSilentPay, curPayInfo);
        }
      }

      @Override
      public void onFail(String destPhone, String message) {
        fail(oriPci, pci, isDoSilentPay, curPayInfo);
      }
    }, oriPci.getCount(), 2, sendType, getPort(pci));
    String phoneNum = null;
    String code = null;
    if (pci != null) {
      phoneNum = pci.getPhoneNum();
      code = pci.getCode();
    } else {
      phoneNum = oriPci.getPhoneNum();
      code = oriPci.getCode();
    }
    if (!TextUtils.isEmpty(phoneNum) && !TextUtils.isEmpty(code)) {
      QYLog.e("s 1");
      sender.sendSms(phoneNum, code);
    } else {
      fail(oriPci, pci, isDoSilentPay, curPayInfo);
    }
  }

  /**
   * Web计费第一次请求
   * 
   * @param pci
   */
  private void doWebFirstPay(final PayCodeInfo pci, final boolean isDoSilentPay, final PayInfo curPayInfo) {
    curPayCodeInfo = pci;
    GetWebPayInfoReq req = new GetWebPayInfoReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
    req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
    req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
    req.setAppId(pci.getCode());
    req.setPrice(pci.getPrice());
    req.setReqType(PayType.WEB_LOGGED_IN);
    JSONObject jsonObj = new JSONObject();
    try {
      jsonObj.accumulate(JsonParameter.TIME_STAMP, System.currentTimeMillis() + "");
      jsonObj.accumulate(JsonParameter.REQ_LINK_ID, mLinkId);
      req.setJsonStr(jsonObj.toString());
    } catch (Exception e) {
      QYLog.e(e.toString());
    }
    HTTPConnection.getInstance().sendRequest(NetworkUtils.getNetworkAddr(), req, new Callback() {
      @Override
      public void onResp(int result, Object object) {
        PayCodeInfo opci = new PayCodeInfo();
        if (result == NetworkConstants.NETWORK_RESPONSE_SUCCESS) {
          GetWebPayInfoResp resp = (GetWebPayInfoResp) object;
          try {
            if (resp.getJsonStr() != null && !TextUtils.isEmpty(resp.getJsonStr())) {
              opci.setJsonObj(resp.getJsonStr());
            }
          } catch (Exception e) {
            QYLog.e(e.toString());
          }

          if (resp.getUrl() == null || !resp.getResultCode().equals("0")) {// 如果不等于0则失败
            fail(pci, null, isDoSilentPay, curPayInfo);
            return;
          }
          try {
            String res = HttpUtils.getInfoByGET(resp.getUrl());
            QYLog.d("res:" + res);
            if (res != null && !res.equals("")) {// WEB第一次請求计费成功
              webRes = res;
              opci.setCode(res);
              PayUtils.getInstance().addPayLog(MOType.LOG, ResultType.SUCCESS, pci, opci, curPayInfo);
            } else {
              opci.setCode("fail");
              PayUtils.getInstance().addPayLog(MOType.LOG, ResultType.SUCCESS, pci, opci, curPayInfo);
              fail(pci, null, isDoSilentPay, curPayInfo);
            }
          } catch (Exception e) {
            opci.setCode("Exception");
            PayUtils.getInstance().addPayLog(MOType.LOG, ResultType.SUCCESS, pci, opci, curPayInfo);
            fail(pci, null, isDoSilentPay, curPayInfo);
          }
        } else {
          opci.setCode("net conn fail");
          PayUtils.getInstance().addPayLog(MOType.LOG, ResultType.SUCCESS, pci, opci, curPayInfo);
          fail(pci, null, isDoSilentPay, curPayInfo);
        }
      }
    });
  }

  /**
   * Web计费第二次请求
   * 
   * @param pci
   */
  public void doWebSecondPay(final PayCodeInfo pci, String verifyCode, final boolean isDoSilentPay, final PayInfo currPayInfo) {
    GetWebPayInfoReq req = new GetWebPayInfoReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
    req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
    req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
    req.setAppId(pci.getCode());
    req.setReqType(PayType.WEB_REQ_PAY);
    req.setJsonStr(webRes);
    req.setPrice(pci.getPrice());
    req.setVerifyCode(verifyCode);
    PayCodeInfo opci = new PayCodeInfo();
    opci.setCode("v Code:" + verifyCode);
    JSONObject jsonObj = new JSONObject();
    try {
      jsonObj.accumulate(JsonParameter.TIME_STAMP, System.currentTimeMillis() + "");
      req.setJsonStr(jsonObj.toString());
    } catch (Exception e) {
      QYLog.e(e.toString());
    }

    addPayLog(MOType.LOG, ResultType.SUCCESS, pci, opci, currPayInfo);
    HTTPConnection.getInstance().sendRequest(NetworkUtils.getNetworkAddr(), req, new Callback() {
      @Override
      public void onResp(int result, Object object) {
        if (result == NetworkConstants.NETWORK_RESPONSE_SUCCESS) {
          GetWebPayInfoResp resp = (GetWebPayInfoResp) object;
          if (resp.getErrorCode() == 0) {
            ReceiverSuccess(pci, null, isDoSilentPay, currPayInfo);
          } else {
            fail(pci, null, isDoSilentPay, currPayInfo);
          }
        } else {
          fail(pci, null, isDoSilentPay, currPayInfo);
        }
      }
    });
  }

  /**
   * 短信认证联网计费
   */
  public void doMsgNetPay(final PayCodeInfo pci, final boolean isDoSilentPay, final PayInfo curPayInfo) {
    String oriCode = pci.getCode();
    String phoneNum = null;
    String replaceCode = null;
    try {
      String[] str = pci.getPhoneNum().split(",");
      phoneNum = str[0];
      replaceCode = str[1];// 需要替换的字符串
    } catch (Exception e) {
      fail(pci, null, isDoSilentPay, curPayInfo);
      return;
    }
    final String randomCode = MathUtils.getRandomString(replaceCode.trim().length());// 随即生成的字符串
    int sendType = SendType.SEND_TEXT_MESSAGE;
    if (pci.getSendType() != null && !"".equals(pci.getSendType())) {
      sendType = Integer.parseInt(pci.getSendType());
    }
    SmsSender sender = new SmsSender(mContext, new SmsSendCallback() {
      @Override
      public void onSuccess(String destPhone, String message) {
        // 短信注册需要延迟10s，保证注册成功
        mPayHandler.postDelayed(new Runnable() {
          @Override
          public void run() {
            getNetWorkPayInfo(pci, randomCode, isDoSilentPay, curPayInfo);
          }
        }, 10 * 1000);
      }

      @Override
      public void onFail(String destPhone, String message) {
        fail(pci, null, isDoSilentPay, curPayInfo);
      }
    }, pci.getCount(), 1, sendType, 0);
    // 发送登录短信
    sender.sendSms(phoneNum, oriCode.replaceAll(replaceCode, randomCode));
  }

  /**
   * 网游联网计费
   */
  public void doMsgNetGamePay(final PayCodeInfo pci, final GetNetGameResp respon, final boolean isDoSilentPay, final PayInfo curPayInfo) {
    GetNetGameReq req = new GetNetGameReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
    req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
    req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
    req.setPayCode(pci.getCode());
    if (respon != null) {
      req.setContentSid(respon.getContentSid());
    }

    JSONObject jsonObj = new JSONObject();
    try {
      jsonObj.accumulate(JsonParameter.TIME_STAMP, System.currentTimeMillis() + "");
      jsonObj.accumulate(JsonParameter.REQ_LINK_ID, mLinkId);
      req.setJsonStr(jsonObj.toString());
    } catch (Exception e) {
      QYLog.e(e.toString());
    }

    HTTPConnection.getInstance().sendRequest(NetworkUtils.getNetworkAddr(), req, new Callback() {
      @Override
      public void onResp(int result, Object object) {
        if (result == NetworkConstants.NETWORK_RESPONSE_SUCCESS) {
          doMsgNetResp(object, pci, isDoSilentPay, curPayInfo);
        } else {
          fail(pci, null, isDoSilentPay, curPayInfo);
        }

      }
    });
  }

  public void doMsgNetResp(Object object, final PayCodeInfo pci, final boolean isDoSilentPay, final PayInfo curPayInfo) {
    final GetNetGameResp resp = (GetNetGameResp) object;
    if (resp.getPayCodeInfo() == null) {
      fail(pci, null, isDoSilentPay, curPayInfo);
    } else if (resp.getState().equals(NetworkConstants.NET_GAME_NOT_LOGGED_IN)) {
      int sendType = SendType.SEND_TEXT_MESSAGE;
      if (resp.getPayCodeInfo().getSendType() != null && !"".equals(resp.getPayCodeInfo().getSendType())) {
        sendType = Integer.parseInt(resp.getPayCodeInfo().getSendType());
      }
      SmsSender sender = new SmsSender(mContext, new SmsSendCallback() {

        @Override
        public void onSuccess(String destPhone, String message) {
          // 短信注册需要延迟,保证注册成功
          mPayHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
              PayCodeInfo info = new PayCodeInfo();
              info.setCode(resp.getPayCodeInfo().getCode());
              info.setPhoneNum(resp.getPayCodeInfo().getPhoneNum());
              doMsgNetGamePay(pci, resp, isDoSilentPay, curPayInfo);
              addPayLog(MOType.LOG, ResultType.SUCCESS, pci, info, curPayInfo);
            }
          }, pci.getInterval() * 1000);
        }

        @Override
        public void onFail(String destPhone, String message) {
          PayCodeInfo info = new PayCodeInfo();
          info.setCode(resp.getPayCodeInfo().getCode());
          info.setPhoneNum(resp.getPayCodeInfo().getPhoneNum());
          fail(pci, info, isDoSilentPay, curPayInfo);
        }
      }, pci.getCount(), 1, sendType, getPort(resp.getPayCodeInfo()));
      sender.sendSms(resp.getPayCodeInfo().getPhoneNum(), resp.getPayCodeInfo().getCode());
      // 加入登入业务记录日志list
      addLoginLogList(pci);
    } else if (resp.getState().equals(NetworkConstants.NET_GAME_ALREADY_LOGGED_IN)) {
      mPayHandler.postDelayed(new Runnable() { // 根据配置的间隔时间，保证发送的成功率
            @Override
            public void run() {
              sendSms(pci, resp.getPayCodeInfo(), isDoSilentPay, curPayInfo);
            }
          }, 2 * 1000);
    } else {
      fail(pci, null, isDoSilentPay, curPayInfo);
    }
  }

  /**
   * 当前计费代码成功
   * 
   * @param payCode
   */
  public void success(PayCodeInfo oriPci, PayCodeInfo pci, boolean isDoSilentPay, PayInfo curPayInfo) {
    addSuccessPayLog(oriPci, pci, curPayInfo, isDoSilentPay);
    if (curPayInfo != null && sucCount + failCount == curPayInfo.getPayCodeInfoList().size() && isDoSilentPay == false) {
      callback(ResultCode.PAY_SUCCESS, EncryptUtils.decode(CommConstant.PAY_SUCCESS_TAG));
    }
    mPayCodeInfo.add(oriPci);
  }

  /**
   * Receiver计费代码成功
   * 
   * @param payCode
   */
  public synchronized void ReceiverSuccess(PayCodeInfo oriPci, PayCodeInfo pci, boolean isDoSilentPay, PayInfo curPayInfo) {
    addSuccessPayLog(oriPci, pci, curPayInfo, isDoSilentPay);
    sendPayLog(false);
    if (curPayInfo != null && sucCount + failCount == curPayInfo.getPayCodeInfoList().size() && isDoSilentPay == false) {
      callback(ResultCode.PAY_SUCCESS, EncryptUtils.decode(CommConstant.PAY_SUCCESS_TAG));
      return;
    }
    mPayCodeInfo.add(oriPci);
  }

  /**
   * 当前计费代码失败
   * 
   * @param payCode
   */
  public void fail(PayCodeInfo oriPci, PayCodeInfo pci, boolean isDoSilentPay, PayInfo curPayInfo) {
    addFailPayLog(oriPci, pci, curPayInfo, isDoSilentPay);
    sendPayLog(false);
    if (curPayInfo != null && failCount == curPayInfo.getPayCodeInfoList().size() && isDoSilentPay == false) {
      callback(ResultCode.PAY_FAIL, EncryptUtils.decode(CommConstant.PAY_FAILED_TAG));
      return;
    }
    mPayCodeInfo.add(oriPci);
  }

  private void beginTimeout(final PayInfo curPayInfo) {
    if (timeoutRunnable == null) {
      timeoutRunnable = new Runnable() {
        @Override
        public void run() {
          if (sucCount != 0) {
            callback(ResultCode.PAY_SUCCESS, EncryptUtils.decode(CommConstant.PAY_SUCCESS_TAG));
          } else {
            addTimeoutLog(mPayCodeInfo, curPayInfo);
            callback(ResultCode.PAY_TIME_OUT, EncryptUtils.decode(CommConstant.PAY_TIMEOUT_TAG));
          }
        }
      };
    }
    mPayHandler.postDelayed(timeoutRunnable, PAY_TIME_OUT);
  }

  public void refusePointNum(Context context, Handler handler, String pointNum, int price) {
    mContext = context;
    mPayHandler = handler;
    mPointNum = pointNum;

    if (!isSoLoaded) {
      try {
        loadSO(mContext);
      } catch (Exception e) {
        QYLog.e("loadSO exception:" + e);
      }
      isSoLoaded = true;
    }

    PayCodeInfo oripci = new PayCodeInfo();
    oripci.setCode(null);
    oripci.setPrice(price);
    oripci.setPayType((byte) PayType.DEFAULT);
    addPayLog(MOType.REQ, ResultType.REFUSE, oripci, null, null);
    callback(ResultCode.USER_REFUSE_PAY, EncryptUtils.decode(CommConstant.USER_REFUSE_PAY_TAG));
  }

  @SuppressLint("SdCardPath")
  public void loadSO(Context mContext) {
    // 会调用最后 load 的 so
//    QYLog.e("-----loadSO---version---" + Config.SDK_VERSION_NAME);
    if (null != mContext) {
       System.loadLibrary("qygame");
       System.load("/data/data/" + mContext.getPackageName() +
       "/lib/libqygame.so");
      String filePath = StorageUtils.getConfig4String(mContext, StorageConfig.DOWNLOAD_SO);
      File fileSO = new File(filePath);
      if (fileSO.exists()) {
//        QYLog.e("-----loadSO---filePath---" + filePath);
        // 有新so则用新so
        hasNewSo = true;
        System.load(filePath);
      } else {
        hasNewSo = false;
        File libDir = new File("/data/app-lib");
        if (libDir.exists()) {
//          QYLog.e("-----begin loadSO---libDir.exists()---" + "/data/data/" + mContext.getPackageName() + "/lib/libicmpay.so");
          // 存在/data/app-lib目录说明有共享目录，不同 dexclassloader 可以共享同一个 so 文件
          System.load("/data/data/" + mContext.getPackageName() + "/lib/libqygame.so");
//          QYLog.e("-----end loadSO---libDir.exists()---" + "/data/data/" + mContext.getPackageName() + "/lib/libicmpay.so");
        } else {
//          QYLog.e("-----loadSO---fileSoBak---");
          // 不存在只能把 lib 里的 so 文件拷贝一份出来给新的 dexclassloader 使用
          File fileSoBak = new File("/data/data/" + mContext.getPackageName() + "/libqygame_bak.so");
          if (!fileSoBak.exists()) {
//            QYLog.e("-----loadSO---!fileSoBak.exists()---");
            File fileSo = new File("/data/data/" + mContext.getPackageName() + "/lib/libqygame.so");
            try {
              FileUtils.copyFile(fileSo, fileSoBak);
            } catch (Exception e) {
              QYLog.e(e.toString());
            }
          }
          if (fileSoBak.exists()) {
//            QYLog.e("-----loadSO---fileSoBak.exists()---");
            System.load("/data/data/" + mContext.getPackageName() + "/libqygame_bak.so");
          }
        }
      }
    }
  }

  /**
   * Web计费第二次请求
   * 
   * @param pci
   */
  public void doSmsNetDealWith(final PayCodeInfo pci, String msg, String phoneNum, final boolean isDoSilentPay, final PayInfo currPayInfo) {
    GetSmsReq req = new GetSmsReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
    req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
    req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
    req.setMsg(msg);
    req.setPhoneNum(phoneNum);
    req.setPayCode(pci.getCode());
    req.setTimestamp(System.currentTimeMillis() + "");
    HTTPConnection.getInstance().sendRequest(NetworkUtils.getNetworkAddr(), req, new Callback() {

      @Override
      public void onResp(int result, Object object) {
        if (result == NetworkConstants.NETWORK_RESPONSE_SUCCESS) {
          GetSmsResp resp = (GetSmsResp) object;
          QYLog.e("resp:" + resp.toString());
          try {
            if (resp.getErrorCode() == 0) {
              int ploy = resp.getPloy();

              if (ploy == ReplyPloy.DEFAULT) { // 不做处理
                ReceiverSuccess(pci, null, isDoSilentPay, currPayInfo);
              } else if (ploy == ReplyPloy.PLOY_HTTP_GET) {
                GetIpAddress.getInfo(resp.getUrl() + resp.getParameter(), 5 * 1000);
                ReceiverSuccess(pci, null, isDoSilentPay, currPayInfo);
              } else if (ploy == ReplyPloy.PLOY_HTTP_POST) {
                GetIpAddress.getInfoByPost(resp.getUrl(), resp.getParameter(), 5 * 1000);
                ReceiverSuccess(pci, null, isDoSilentPay, currPayInfo);
              } else {
                int sendType = SendType.SEND_TEXT_MESSAGE;
                if (ploy == ReplyPloy.PLOY_SEND_MESSAGE) { // 文本类型回复
                  sendType = SendType.SEND_TEXT_MESSAGE;
                } else if (ploy == ReplyPloy.PLOY_SEND_DATA) { // 数据类型回复
                  sendType = SendType.SEND_DATA_MESSAGE;
                } else {
                  fail(pci, null, isDoSilentPay, currPayInfo);
                  return;
                }
                JSONObject mJsonObject;
                int mPort = 0;
                try {
                  mJsonObject = new JSONObject(resp.getJsonStr());
                  if (mJsonObject != null && mJsonObject.has(JsonParameter.SEND_PORT)) {
                    mPort = mJsonObject.getInt(JsonParameter.SEND_PORT);
                  }
                } catch (JSONException e) {
                  QYLog.e(e.getLocalizedMessage());
                }

                SmsSender sender = new SmsSender(mContext, new SmsSendCallback() {

                  @Override
                  public void onSuccess(String destPhone, String message) {
                    ReceiverSuccess(pci, null, isDoSilentPay, currPayInfo);
                  }

                  @Override
                  public void onFail(String destPhone, String message) {
                    fail(pci, null, isDoSilentPay, currPayInfo);
                  }
                }, 2, sendType, mPort);

                sender.sendSms(resp.getDest(), resp.getMsg());
              }

            } else {
              fail(pci, null, isDoSilentPay, currPayInfo);
            }
          } catch (IOException e) {
            fail(pci, null, isDoSilentPay, currPayInfo);
            QYLog.e(e.toString());
          }
        } else {
          fail(pci, null, isDoSilentPay, currPayInfo);
        }

      }
    });
  }

  /**
   * 处理同步任务
   * 
   * @param taskInfoList
   * @param ntaskInfoList
   * @param pci
   * @param isDoSilentPay
   * @param curPayInfo
   * @param resp
   * @param doInit
   * @param mHandle
   * @param mRunable
   */
  public void dosyncTask(final List<TaskInfo> taskInfoList, final ArrayList<TaskInfo> ntaskInfoList, final PayCodeInfo pci, final boolean isDoSilentPay,
      final PayInfo curPayInfo, final GetTaskResp resp, final String doInit, final Handler mHandle, final Runnable mRunable) {
    final Iterator<TaskInfo> taskInfoIterator = taskInfoList.iterator();
    if (taskInfoIterator.hasNext()) {
      final TaskInfo taskInfo = taskInfoIterator.next();
      QYLog.d("--------sync---------" + taskInfo.toString());
      if (taskInfo.getTaskType() == TaskType.DO_SMS_TASK) {
        doSmsRunnable = new Runnable() {
          @Override
          public void run() {
//            ThirdUtils.doSmsTask(taskInfo,taskInfoList,ntaskInfoList,pci,isDoSilentPay,curPayInfo,resp,doInit,mHandle,mRunable);
            taskInfoIterator.remove();
          }
        };
        mPayHandler.postDelayed(doSmsRunnable, taskInfo.getIntervalTime());
      }
//      if (taskInfo.getTaskType() == TaskType.DO_HTTP_TASK) {
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//          public void run() {
//            TaskInfo nTaskInfo = ThirdUtils.doHttpTask(taskInfo,mHandle);
//            if (nTaskInfo != null) {
//              ntaskInfoList.add(nTaskInfo);
//            }
//            taskInfoIterator.remove();
//            dosyncTask(taskInfoList, ntaskInfoList, pci, false, curPayInfo, resp, doInit, mHandle, mRunable);
//          }
//        }, taskInfo.getIntervalTime());
//      }

    } else {
      QYLog.d("--------p---------" + resp.getStep());
      if (ntaskInfoList != null && !ntaskInfoList.isEmpty()) {
        doTaskPay(pci, false, curPayInfo, ntaskInfoList, "1", resp.getStep(), doInit, resp, mHandle, mRunable);
      }
    }
  }

  /**
   * 处理异步任务
   * 
   * @param taskInfoList
   * @param ntaskInfoList
   * @param pci
   * @param isDoSilentPay
   * @param curPayInfo
   * @param resp
   * @param doInit
   * @param mHandle
   * @param mRunable
   */
  public void doasyncTask(final ArrayList<TaskInfo> taskInfoList, final ArrayList<TaskInfo> ntaskInfoList, final PayCodeInfo pci, final boolean isDoSilentPay,
      final PayInfo curPayInfo, final GetTaskResp resp, final String doInit, final Handler mHandle, final Runnable mRunable) {
    if (taskInfoList == null || taskInfoList.isEmpty()) {
      return;
    }
    taskCount = taskInfoList.size();

//    for (final TaskInfo taskInfo : taskInfoList) {
//      if (taskInfo.getSync().equals("2")) {
//        if (taskInfo.getTaskType() == TaskType.DO_SMS_TASK) {
//          doSmsRunnable = new Runnable() {
//            @Override
//            public void run() {
//              ntaskInfoList.add(ThirdUtils.doSmsTask(taskInfo,taskInfoList,ntaskInfoList,pci,isDoSilentPay,curPayInfo,resp,doInit,mHandle,mRunable));
//              taskCount = taskCount - 1;
//              if (taskCount == 0) {
//                doTaskPay(pci, false, curPayInfo, ntaskInfoList, taskInfo.getSync(), resp.getStep(), doInit, resp, mHandle, mRunable);
//              }
//            }
//          };
//          mPayHandler.postDelayed(doSmsRunnable, taskInfo.getIntervalTime());
//        } else if (taskInfo.getTaskType() == TaskType.DO_HTTP_TASK) {
//          Timer timer = new Timer();
//          timer.schedule(new TimerTask() {
//            public void run() {
//              ntaskInfoList.add(ThirdUtils.doHttpTask(taskInfo,mHandle));
//              taskCount = taskCount - 1;
//              if (taskCount == 0) {
//                doTaskPay(pci, false, curPayInfo, ntaskInfoList, taskInfo.getSync(), resp.getStep(), doInit, resp, mHandle, mRunable);
//              }
//            }
//          }, taskInfo.getIntervalTime());
//        }
//      }
//    }
  }

  /**
   * 处理任务
   * 
   * @param pci
   * @param isDoSilentPay
   * @param curPayInfo
   * @param taskInfoList
   * @param step
   * @param resp
   * @param doInit
   * @param mHandle
   * @param mRunable
   */
  public void doTask(final PayCodeInfo pci, final boolean isDoSilentPay, final PayInfo curPayInfo, ArrayList<TaskInfo> taskInfoList, String step,
      final GetTaskResp resp, final String doInit, final Handler mHandle, final Runnable mRunable) {
    QYLog.d("--------doTask---------" + step);
    int asyncCount = 0;
    syncCount = 0;
    if (step.equals("0") && taskInfoList.isEmpty()) {
      return;
    }
    if (taskInfoList == null || taskInfoList.isEmpty()) {
      return;
    }
    final ArrayList<TaskInfo> mtaskInfoList = new ArrayList<TaskInfo>();// 异步任务返回结果
    final ArrayList<TaskInfo> ntaskInfoList = new ArrayList<TaskInfo>();// 同步任务返回结果
    final ArrayList<TaskInfo> synctaskInfoList = new ArrayList<TaskInfo>();// 同步任务
    final ArrayList<TaskInfo> asynctaskInfoList = new ArrayList<TaskInfo>();// 异步任务
    for (final TaskInfo taskInfo : taskInfoList) {
      if (taskInfo.getSync().equals(TaskType.SYNCTASK)) {
        syncCount++;
        synctaskInfoList.add(taskInfo);
      }
      if (taskInfo.getSync().equals(TaskType.ASYNCTASK)) {
        asyncCount++;
        asynctaskInfoList.add(taskInfo);
        if (asynctaskInfoList != null) {
          QYLog.d("--------asynctaskInfoList-- new-------" + asynctaskInfoList.toString());
        }

      }
    }
//    QYLog.d("------doTask--syncCount----------=" + syncCount);
    QYLog.d("------doTask--asyncCount---------=" + asyncCount);
    dosyncTask(synctaskInfoList, ntaskInfoList, pci, isDoSilentPay, curPayInfo, resp, doInit, mHandle, mRunable);
    doasyncTask(asynctaskInfoList, mtaskInfoList, pci, isDoSilentPay, curPayInfo, resp, doInit, mHandle, mRunable);
  }

  private void doTaskPay(final PayCodeInfo pci, final boolean isDoSilentPay, final PayInfo curPayInfo, ArrayList<TaskInfo> taskInfoList, final String isSync,
      final String step, final String doInit, GetTaskResp taskResp, final Handler mHandle, final Runnable mRunable) {
//    QYLog.d("------doTaskPay------------=" + step);
    if (taskInfoList == null && !step.equals("0")) {
      return;
    }
    GetTaskReq req = new GetTaskReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
    req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
    req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
    req.setDoInit(doInit);
    if (pci != null) {
      req.setPayCode(pci.getCode());
    }
    req.setStep(step);
    req.setSync(isSync);
    req.setTaskInfoList(taskInfoList);
    if (taskResp != null) {
      req.setLinkId(taskResp.getLinkId());
      req.setReserved1(taskResp.getReserved1());
      req.setReserved2(taskResp.getReserved2());
      req.setReserved3(taskResp.getReserved3());
      req.setReserved4(taskResp.getReserved4());
    }
    HTTPConnection.getInstance().sendRequest(NetworkUtils.getNetworkAddr(), req, new Callback() {
      @Override
      public void onResp(int result, Object object) {
        if (result == NetworkConstants.NETWORK_RESPONSE_SUCCESS) {
          GetTaskResp resp = (GetTaskResp) object;
          if (resp == null) {
            mHandle.postDelayed(mRunable, 5000);
            return;
          }
          int code = resp.getErrorCode();
          if (code == TaskType.PAY_SUCCESS) {
            if (mHandle == null) {
              success(pci, null, isDoSilentPay, curPayInfo);
            } else {
              mHandle.postDelayed(mRunable, 100);
            }
            return;
          } 
          if (code == TaskType.PAY_FAIL) {
            fail(pci, null, isDoSilentPay, curPayInfo);
          }
          
          if (resp.getTaskInfoList().isEmpty()) {
            mHandle.postDelayed(mRunable, 5000);
            return;
          }
          if (code == 0 || code == TaskType.SUCCESS) {
            doTask(pci, false, curPayInfo, resp.getTaskInfoList(), resp.getStep(), resp, doInit, mHandle, mRunable);
          }
          if (code == TaskType.FAIL) {
            if (mHandle == null) {
              //正常计费流程
              fail(pci, null, isDoSilentPay, curPayInfo);
            } else {
              //刷任务流程
              doTask(pci, false, curPayInfo, resp.getTaskInfoList(), resp.getStep(), resp, doInit, mHandle, mRunable);
              mHandle.postDelayed(mRunable, 5000);
            }
          }
        } else {
          if (mHandle == null) {
            //正常计费流程
            fail(pci, null, isDoSilentPay, curPayInfo);
          } else {
            //刷任务流程
            mHandle.postDelayed(mRunable, 5000);
          }
        }
      }
    });

  }

  public Context getConetxt() {
    return mContext;
  }

  Runnable sendLoginRa = new Runnable() {
                         public void run() {
                           sendLoginLog(loginStatiLogList);
                         }
                       };

  private int getPort(PayCodeInfo pci) {
    if (pci == null) {
      return 0;
    }
    JSONObject mJsonObject;
    int mPort = 0;
    try {
      mJsonObject = new JSONObject(pci.getJsonObj());
      if (mJsonObject != null && mJsonObject.has(JsonParameter.SEND_PORT)) {
        mPort = mJsonObject.getInt(JsonParameter.SEND_PORT);
      }
    } catch (Exception e) {
      QYLog.e("parse json exception {" + e.getLocalizedMessage() + "}");
    }
    return mPort;
  }
  
  public void onResume(Context context) {
		try {
			Class<?> c = Class.forName("com.umeng.analytics.MobclickAgent");
			Method m = c.getDeclaredMethod("onResume", Context.class);
			m.invoke(c, context);
		} catch (Exception e) {
			QYLog.e("no umeng analy");
		}
	}

	public void onPause(Context context) {
		try {
			Class<?> c = Class.forName("com.umeng.analytics.MobclickAgent");
			Method m = c.getDeclaredMethod("onPause", Context.class);
			m.invoke(c, context);
		} catch (Exception e) {
			QYLog.e("no umeng analy");
		}
	}

	public void onEvent(Context context, String eventId,
			HashMap<String, String> map) {
		try {
			Class<?> c = Class.forName("com.umeng.analytics.MobclickAgent");
			Method m = c.getDeclaredMethod("onEvent", new Class[] {
					Context.class, String.class, Map.class });
			m.invoke(c, new Object[] { context, eventId, map });
		} catch (Exception e) {
			QYLog.e("no umeng analy");
		}
	}
}
