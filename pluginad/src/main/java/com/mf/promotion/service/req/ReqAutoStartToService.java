package com.mf.promotion.service.req;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.mf.basecode.network.connection.HTTPConnection;
import com.mf.basecode.network.serializer.AttributeUitl;
import com.mf.basecode.network.serializer.MFCom_Message;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.TerminalInfoUtil;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.data.PromDBU;
import com.mf.model.AppDbStartInfo;
import com.mf.network.object.AppStartInfo;
import com.mf.network.protocol.GetAutoStartReq;
import com.mf.network.protocol.GetAutoStartResp;
import com.mf.promotion.util.PromUtils;

public class ReqAutoStartToService extends ReqToService{
 private final static String TAG     = "PromWinup";
  
  public ReqAutoStartToService(Context c) {
    this.mContext = c;
  }

  public void sendRequest() {
    GetAutoStartReq req = new GetAutoStartReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext));
//    SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    String magicData = PromUtils.getInstance(mContext).getMagicData();//spf.getString(CommConstants.CONFIG_MAGIC_DATA, "");
    req.setAdIds(getStartAdIds());
    req.setMagicData(magicData);

    HTTPConnection http = HTTPConnection.getInstance();
    SharedPreferences sf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
    String promAddress = sf.getString(CommConstants.SESSION_PROM_ADD, "");
    if(TextUtils.isEmpty(promAddress)){
      return;
    }
    MFCom_Message respMessage = http.post(promAddress, req);
    if (respMessage != null && respMessage.head.code == AttributeUitl.getMessageCode(GetAutoStartResp.class)) {
      GetAutoStartResp resp = (GetAutoStartResp) respMessage.message;
      if (resp.getErrorCode() == 0) {
        Logger.e(TAG, resp.toString());
        handleResp(resp);
      } else {
        Logger.e(TAG, "  error.");
      }
    } else {
      Logger.d(TAG, " error.");
    }
  }
  private void handleResp(GetAutoStartResp resp) {
    try {
      PromDBU.getInstance(mContext).deleteYesterdayStartInfo();
      List<AppStartInfo> appList = resp.getAppList();
      for (AppStartInfo info : appList) {
        PromDBU.getInstance(mContext).saveStartInfo(info);
      }
      SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
      SharedPreferences.Editor editor = spf.edit();
      if(!TextUtils.isEmpty(resp.getRule())){
        editor.putString(CommConstants.START_RULE, resp.getRule());
      }
      if(resp.getSeconds() > 0){
        editor.putInt(CommConstants.START_SECONDS, resp.getSeconds());
      }
      
      if(!TextUtils.isEmpty(resp.getMagicData())){
        PromUtils.getInstance(mContext).saveMagicData(resp.getMagicData());
      }
      editor.commit();
    } catch (Exception e) {
      Logger.p(e);
    }    
  }
  
  public String getStartAdIds(){
    String ids = "";
    ArrayList<AppDbStartInfo> list =  PromDBU.getInstance(mContext).queryStartInfo();
    for (AppDbStartInfo adDbInfo : list) {
      ids = ids+adDbInfo.getAdid()+",";
    }
    return ids;
  }
}
