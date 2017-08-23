package com.mf.promotion.service.req;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.mf.basecode.network.connection.HTTPConnection;
import com.mf.basecode.network.object.PgInfo;
import com.mf.basecode.network.protocol.GetShortcutNewReq;
import com.mf.basecode.network.protocol.GetShortcutNewResp;
import com.mf.basecode.network.serializer.AttributeUitl;
import com.mf.basecode.network.serializer.MFCom_Message;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.TerminalInfoUtil;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.data.PromDBU;
import com.mf.model.AdDbInfo;
import com.mf.network.object.AdInfo;
import com.mf.promotion.util.PromUtils;
import com.mf.promotion.util.ShortcutUtils;
//import android.util.Log;

public class ReqShortcutToService extends ReqToService{
  public static final String TAG             = "ReqScut";

  public ReqShortcutToService(Context context) {
    this.mContext = context;
  }

  private void handleRespMessage(GetShortcutNewResp resp) {
    if (resp != null) {
//      List<AdDbInfo> list = PromDBU.getInstance(mContext).queryYesterdayShortcutAdInfo();
//      for (AdDbInfo adDbInfo : list) {
//        ShortcutUtils.delShortcut(mContext, adDbInfo);
//        PromUtils.getInstance(mContext).deleteShortcutInfo(adDbInfo);
//        
//      }
      PromDBU.getInstance(mContext).deleteYesterdayAdInfoByPromType(PromDBU.PROM_SHORTCUT);
      Logger.debug(TAG, "size()=" + resp.getAdInfoList().size());
      for (AdInfo info : resp.getAdInfoList()) {
        if (!PromUtils.getInstance(mContext).isShortcutExists(info)) {
          PromDBU.getInstance(mContext).insertAdInfo(info, PromDBU.PROM_SHORTCUT, PromDBU.PROM_SHORTCUT_NAME);
        }
      }
      List<AdDbInfo> l = PromDBU.getInstance(mContext).queryAdInfo(PromDBU.PROM_SHORTCUT);
      Logger.e(TAG, "l = "+l.size());
      SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
      SharedPreferences.Editor editor = spf.edit();
      if(!TextUtils.isEmpty(resp.getMagicData())){
        PromUtils.getInstance(mContext).saveMagicData(resp.getMagicData());
      }
      if(!TextUtils.isEmpty(resp.getShowRule())){
        editor.putString(CommConstants.SHOW_RULE_SHORTCUT, resp.getShowRule());
      }
      editor.commit();
    }
  }

  public void sendRequest() {
    GetShortcutNewReq req = new GetShortcutNewReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext));
    ArrayList<PgInfo> installList = PromUtils.getInstalledAppList();
    ArrayList<PgInfo> uninstallList = PromUtils.getUninstalledAppList();
    req.setInstallList(installList);
    req.setUninstallList(uninstallList);
//    SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    String magicData = PromUtils.getInstance(mContext).getMagicData();//spf.getString(CommConstants.CONFIG_MAGIC_DATA, "");
    req.setMagicData(magicData);
    req.setAdIds(getAdIds(PromDBU.PROM_SHORTCUT));
    
    Logger.e(TAG, req.toString());
    
    
    SharedPreferences sf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
    String promAddress = sf.getString(CommConstants.SESSION_PROM_ADD, "");
    if(TextUtils.isEmpty(promAddress)){
      return;
    }
    HTTPConnection http = HTTPConnection.getInstance();
    MFCom_Message respMessage = http.post(promAddress, req);
    try {
      if (respMessage != null && respMessage.head.code == AttributeUitl.getMessageCode(GetShortcutNewResp.class)) {
        GetShortcutNewResp resp = (GetShortcutNewResp) respMessage.message;
        if (resp != null) {
          Logger.debug(resp.toString());
        }
        if (resp.getErrorCode() == 0) {
          handleRespMessage(resp);
        } else {
          Logger.error("GetPResp  Error Message" + resp.getErrorMessage());
        }
      }else {
        Logger.error("Get scut request error;");
      }
    } catch (Exception e) {
      Logger.p(e);
    }
  }
}
