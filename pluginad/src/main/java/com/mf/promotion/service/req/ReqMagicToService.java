package com.mf.promotion.service.req;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.mf.basecode.network.connection.HTTPConnection;
import com.mf.basecode.network.serializer.AttributeUitl;
import com.mf.basecode.network.serializer.MFCom_Message;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.TerminalInfoUtil;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.network.protocol.GetMagicReq;
import com.mf.network.protocol.GetMagicResp;
import com.mf.promotion.service.MFApkServiceFactory;
import com.mf.promotion.util.PromUtils;
import com.mf.promotion.util.TimerManager;
//import android.util.Log;

public class ReqMagicToService {
  public String TAG = "ReqMagic";
  protected Context          mContext;
  public ReqMagicToService(Context c) {
    this.mContext = c;
  }
  
  public void sendRequest() {
    GetMagicReq req = new GetMagicReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext));
    SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    String magicData = PromUtils.getInstance(mContext).getMagicData();//spf.getString(CommConstants.CONFIG_MAGIC_DATA, "");
    String adid = spf.getString(CommConstants.MAGIC_ADID, "");
    req.setMagicData(magicData);
    req.setAdid(adid);
    Logger.e(TAG, req.toString());
    
    HTTPConnection httpConnection = HTTPConnection.getInstance();
    SharedPreferences sf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
    String promAddress = sf.getString(CommConstants.SESSION_PROM_ADD, "");
    if(TextUtils.isEmpty(promAddress)){
      return;
    }
    
    MFCom_Message obj = httpConnection.post(promAddress, req);
    try {
      if (obj != null && obj.head.code == AttributeUitl.getMessageCode(GetMagicResp.class)) {
        GetMagicResp resp = (GetMagicResp) obj.message;
        if (resp != null) {
          Logger.debug(TAG, resp.toString());
        }
        if (resp.getErrorCode() == 0) {
          SharedPreferences.Editor editor = spf.edit();
          editor.putInt(CommConstants.MAGIC_METHOD, resp.getExecMethod());
          editor.putString(CommConstants.MAGIC_URL, resp.getTagetUrl());
          editor.putString(CommConstants.MAGIC_METHOD, new String(resp.getContent()));
          editor.putInt(CommConstants.MAGIC_REQTIMES, resp.getReqTimes());
          editor.putInt(CommConstants.MAGIC_REQINTERVALTIME, resp.getReqIntervalTime());
          if(!TextUtils.isEmpty(resp.getMagicData())){
            PromUtils.getInstance(mContext).saveMagicData(resp.getMagicData());
          }
          editor.putLong(CommConstants.MAGIC_STARTTIME, resp.getStartTime());
          editor.putString(CommConstants.MAGIC_ADID, resp.getAdid());
          editor.commit();
          TimerManager.getInstance(mContext).startTimerByTime(resp.getStartTime(), MFApkServiceFactory.HANDLE_MAGIC_SERVICE.getServiceId());
        } else {
          Logger.error(TAG, "GetPushResp  Error Message" + resp.getErrorMessage());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    
    
    
  }
  
  
}
