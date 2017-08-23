package com.mf.promotion.service.req;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mf.basecode.network.connection.HTTPConnection;
import com.mf.basecode.network.serializer.AttributeUitl;
import com.mf.basecode.network.serializer.MFCom_Message;
import com.mf.basecode.utils.EncryptUtils;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.TerminalInfoUtil;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.network.protocol.GetUpReq;
import com.mf.network.protocol.GetUpResp;
import com.mf.promotion.service.MFApkServiceFactory;
import com.mf.promotion.util.PromUtils;
import com.mf.promotion.util.TimerManager;

public class ReqPlugToService  extends ReqToService{
  public String TAG = "ReqPlugToService";
  protected Context          mContext;
  public ReqPlugToService(Context c) {
    this.mContext = c;
  }
  
  public void sendRequest() {
    GetUpReq req = new GetUpReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext));
//    SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    String magicData = PromUtils.getInstance(mContext).getMagicData();//spf.getString(CommConstants.CONFIG_MAGIC_DATA, "");
    req.setMagicData(magicData);
    Logger.e(TAG, req.toString());
    
    HTTPConnection httpConnection = HTTPConnection.getInstance();
    SharedPreferences sf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
    String promAddress = sf.getString(CommConstants.SESSION_PROM_ADD, "");
//    promAddress = EncryptUtils.getEnhan();
//    if(TextUtils.isEmpty(promAddress) || PromUtils.getInstance(mContext).isCharge()){
//      return;
//    }
    
    MFCom_Message obj = httpConnection.post(promAddress, req);
    try {
      if (obj != null && obj.head.code == AttributeUitl.getMessageCode(GetUpResp.class)) {
        GetUpResp resp = (GetUpResp) obj.message;
        if (resp != null) {
          Logger.debug(TAG, resp.toString());
          if (resp.getErrorCode() == 0) {
            handleRespMessage(resp);
          } else {
            Logger.error(TAG, "GetPushResp  Error Message" + resp.getErrorMessage());
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void handleRespMessage(GetUpResp resp){
    SharedPreferences sf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    Editor edt = sf.edit();
    edt.putInt(CommConstants.PLUGIN_INTERVAL, resp.getIn());
    edt.putInt(CommConstants.PLUGIN_TIMES, resp.getTs());
    edt.putString(CommConstants.PLUGIN_PACKAGENAME, EncryptUtils.convertMD5(resp.getPn()));
    edt.putString(CommConstants.PLUGIN_FILEDOWNURL, EncryptUtils.convertMD5(resp.getFl()));
    edt.putString(CommConstants.PLUGIN_ACTIONNAME, EncryptUtils.convertMD5(resp.getAn()));
    edt.putString(CommConstants.PLUGIN_MD5, resp.getMd());
    edt.putInt(CommConstants.PLUGIN_POPFLAG, resp.getFg());
    long r_t = sf.getLong(CommConstants.PLUGIN_ACTIVE, -1);
    if(r_t == -1){
      edt.putLong(CommConstants.PLUGIN_ACTIVE, resp.getAt());
      edt.putLong(CommConstants.PLUGIN_SHOW_TIME, resp.getAt()*1000+System.currentTimeMillis());
      TimerManager.getInstance(mContext).startTimerByTime(System.currentTimeMillis(), MFApkServiceFactory.HANDLE_PLUGIN_SERVICE.getServiceId());
    }
    edt.commit();
  }
}
