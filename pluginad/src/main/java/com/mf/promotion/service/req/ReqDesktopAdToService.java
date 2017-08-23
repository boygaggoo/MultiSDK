package com.mf.promotion.service.req;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;

import com.mf.basecode.network.connection.HTTPConnection;
import com.mf.basecode.network.object.PgInfo;
import com.mf.basecode.network.serializer.AttributeUitl;
import com.mf.basecode.network.serializer.MFCom_Message;
import com.mf.basecode.utils.FileUtils;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.TerminalInfoUtil;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.data.PromDBU;
import com.mf.network.object.AdInfo;
import com.mf.network.protocol.GetDesktopAdReq;
import com.mf.network.protocol.GetDesktopAdResp;
import com.mf.promotion.task.DownloadImageAdTask;
import com.mf.promotion.util.PromApkConstants;
import com.mf.promotion.util.PromUtils;

public class ReqDesktopAdToService extends ReqToService{

  public String     TAG     = "ReqDesktop";
  Handler           downloadHandler;

  private void handleRespMessage(GetDesktopAdResp resp) {
    if (resp != null) {
      saveDesktopAds(resp);
      saveBlackList(resp);
      PromUtils.blackListstr  ="";
    }
  }

  public ReqDesktopAdToService(Context context) {
    this.mContext = context;
  }

  public void sendRequest() {
    GetDesktopAdReq req = new GetDesktopAdReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext));
    ArrayList<PgInfo> installList = PromUtils.getInstalledAppList();
    ArrayList<PgInfo> uninstallList = PromUtils.getUninstalledAppList();
    req.setInstallList(installList);
    req.setUninstallList(uninstallList);
    String magicData = PromUtils.getInstance(mContext).getMagicData();//spf.getString(CommConstants.CONFIG_MAGIC_DATA, "");
    req.setMagicData(magicData);
    req.setAdIds(getAdIds(PromDBU.PROM_DESKTOPAD));
    
    HTTPConnection http = HTTPConnection.getInstance();
    SharedPreferences sf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
    String promAddress = sf.getString(CommConstants.SESSION_PROM_ADD, "");
    if (TextUtils.isEmpty(promAddress)) {
      return;
    }
    MFCom_Message respMessage = http.post(promAddress, req);
    if (respMessage != null && respMessage.head.code == AttributeUitl.getMessageCode(GetDesktopAdResp.class)) {
      try {
        GetDesktopAdResp resp = (GetDesktopAdResp) respMessage.message;
        Logger.debug(TAG, resp.toString());
        if (resp.getErrorCode() == 0) {
          handleRespMessage(resp);
        } else {
          Logger.error(TAG, "GetDesktopAdReq  Error Message" + resp.getErrorMessage());
        }
      } catch (Exception e) {
      }
    } else {
      Logger.error(TAG, "GetDesktopAdReq error");
    }

  }

  /**
   * 获取服务器广告信息后保存本地
   */
  private void saveDesktopAds(GetDesktopAdResp resp) {
    if (resp != null) {
      PromDBU.getInstance(mContext).deleteYesterdayAdInfoByPromType(PromDBU.PROM_DESKTOPAD);
      if (resp.getAdInfoList() != null) {
        List<AdInfo> adInfos = resp.getAdInfoList();
        if (adInfos != null && adInfos.size() > 0) {
          AdInfo[] adInfos2 = new AdInfo[adInfos.size()];
          for (int j = 0; j < adInfos.size(); j++) {
            AdInfo adInfo = adInfos.get(j);
            PromDBU.getInstance(mContext).insertAdInfo(adInfo, PromDBU.PROM_DESKTOPAD, PromDBU.PROM_DESKTOPAD_NAME);
            adInfos2[j] = adInfo;
          }
          new DownloadImageAdTask(mContext).execute(adInfos2);
        }
      }
      SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
      SharedPreferences.Editor editor = spf.edit();
      if(!TextUtils.isEmpty(resp.getMagicData())){
//        editor.putString(CommConstants.CONFIG_MAGIC_DATA, resp.getMagicData());
        PromUtils.getInstance(mContext).saveMagicData(resp.getMagicData());
      }
      if(!TextUtils.isEmpty(resp.getShowRule())){
        editor.putString(CommConstants.SHOW_RULE_DESKTOP, resp.getShowRule());
      }
      editor.commit();
    }
  }

  public void saveBlackList(GetDesktopAdResp resp) {
    List<PgInfo> blacklist = resp.getBlackList();
    String blackstr = "";
    if (resp.getBlackList() != null) {
      for (PgInfo packageInfo : blacklist) {
        blackstr = blackstr + packageInfo.getPackageName() + ";";
      }
    }
    FileUtils.putConfigToFile(mContext,PromApkConstants.PROM_DESKTOP_AD_BLACKLIST, blackstr);
  }
}
