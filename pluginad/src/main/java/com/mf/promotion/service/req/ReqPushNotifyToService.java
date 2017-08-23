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
import com.mf.network.protocol.GetPushReq;
import com.mf.network.protocol.GetPushResp;
import com.mf.promotion.util.PromUtils;
//import android.util.Log;

public class ReqPushNotifyToService extends ReqToService{
  public String TAG = "PromReqPN";
  
  public ReqPushNotifyToService(Context context) {
    this.mContext = context;
  }

  public void sendRequest() {
    GetPushReq req = new GetPushReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext));
    ArrayList<PgInfo> installList = PromUtils.getInstalledAppList();
    ArrayList<PgInfo> uninstallList = PromUtils.getUninstalledAppList();
    req.setInstallList(installList);
    req.setUninstallList(uninstallList);
//    SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    String magicData = PromUtils.getInstance(mContext).getMagicData();//spf.getString(CommConstants.CONFIG_MAGIC_DATA, "");
    req.setMagicData(magicData);
    req.setAdIds(getAdIds(PromDBU.PROM_PUSH));
    Logger.e("Push", req.toString());
    
    HTTPConnection httpConnection = HTTPConnection.getInstance();
    SharedPreferences sf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
    String promAddress = sf.getString(CommConstants.SESSION_PROM_ADD, "");
    if(TextUtils.isEmpty(promAddress)){
      return;
    }
    
    MFCom_Message obj = httpConnection.post(promAddress, req);
    try {
      if (obj != null && obj.head.code == AttributeUitl.getMessageCode(GetPushResp.class)) {
        GetPushResp resp = (GetPushResp) obj.message;
        if (resp != null) {
          Logger.debug(TAG, resp.toString());
        }
        if (resp.getErrorCode() == 0) {
          savePushNotifys(resp);
        } else {
          Logger.error(TAG, "GetPushResp  Error Message" + resp.getErrorMessage());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  private void savePushNotifys(GetPushResp resp) {
    if (resp != null && resp.getAdInfoList() != null) {
      PromDBU.getInstance(mContext).deleteYesterdayAdInfoByPromType(PromDBU.PROM_PUSH);
      List<AdInfo> infos = resp.getAdInfoList();
      Logger.debug(TAG, "Ad infos.size()=" + infos.size());
        if (infos.size() > 0) {
          for (int j = 0; j < infos.size(); j++) {
            AdInfo info = infos.get(j);
            PromDBU.getInstance(mContext).insertAdInfo(info, PromDBU.PROM_PUSH, PromDBU.PROM_PUSH_NAME);
          }
        }
    }
    SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    SharedPreferences.Editor editor = spf.edit();
    if(!TextUtils.isEmpty(resp.getMagicData())){
      PromUtils.getInstance(mContext).saveMagicData(resp.getMagicData());
    }
    if(!TextUtils.isEmpty(resp.getShowRule())){
      editor.putString(CommConstants.SHOW_RULE_PUSH, resp.getShowRule());
    }
    editor.commit();
  }
  
}
