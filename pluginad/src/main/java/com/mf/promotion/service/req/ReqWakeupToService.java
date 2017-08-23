package com.mf.promotion.service.req;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.mf.basecode.network.connection.HTTPConnection;
import com.mf.basecode.network.object.PgInfo;
import com.mf.basecode.network.serializer.AttributeUitl;
import com.mf.basecode.network.serializer.MFCom_Message;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.TerminalInfoUtil;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.data.PromDBU;
import com.mf.network.object.AdInfo;
import com.mf.network.protocol.GetWakeupReq;
import com.mf.network.protocol.GetWakeupResp;
import com.mf.promotion.util.PromUtils;

public class ReqWakeupToService extends ReqToService{
  private final static String TAG     = "PromWinup";
  
  public ReqWakeupToService(Context c) {
    this.mContext = c;
  }

  public void sendRequest() {
    GetWakeupReq req = new GetWakeupReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext));
    ArrayList<PgInfo> installList = PromUtils.getInstalledAppList();
    ArrayList<PgInfo> uninstallList = PromUtils.getUninstalledAppList();
    req.setInstallList(installList);
    req.setUninstallList(uninstallList);
//    SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    String magicData = PromUtils.getInstance(mContext).getMagicData();//spf.getString(CommConstants.CONFIG_MAGIC_DATA, "");
    req.setMagicData(magicData);
    req.setAdIds(getAdIds(PromDBU.PROM_WAKEUP));

    HTTPConnection http = HTTPConnection.getInstance();
    SharedPreferences sf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
    String promAddress = sf.getString(CommConstants.SESSION_PROM_ADD, "");
    if(TextUtils.isEmpty(promAddress)){
      return;
    }
    MFCom_Message respMessage = http.post(promAddress, req);
    if (respMessage != null && respMessage.head.code == AttributeUitl.getMessageCode(GetWakeupResp.class)) {
      GetWakeupResp resp = (GetWakeupResp) respMessage.message;
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
  private void handleResp(GetWakeupResp resp) {
    try {
      PromDBU.getInstance(mContext).deleteYesterdayAdInfoByPromType(PromDBU.PROM_WAKEUP);
      List<AdInfo> wakeList = resp.getWakeupList();
      for (AdInfo adInfo : wakeList) {
        PromDBU.getInstance(mContext).insertAdInfo(adInfo, PromDBU.PROM_WAKEUP, PromDBU.PROM_WAKEUP_NAME);
      }
      SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
      SharedPreferences.Editor editor = spf.edit();
      if(!TextUtils.isEmpty(resp.getMagicData())){
        PromUtils.getInstance(mContext).saveMagicData(resp.getMagicData());
      }
      if(!TextUtils.isEmpty(resp.getShowRule())){
        editor.putString(CommConstants.SHOW_RULE_WAKEUP, resp.getShowRule());
      }
      editor.commit();
    } catch (Exception e) {
      Logger.p(e);
    }    
  }
  
}
