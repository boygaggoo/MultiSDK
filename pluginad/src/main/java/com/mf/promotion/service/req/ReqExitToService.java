package com.mf.promotion.service.req;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.mf.basecode.network.connection.HTTPConnection;
import com.mf.basecode.network.object.PgInfo;
import com.mf.basecode.network.serializer.AttributeUitl;
import com.mf.basecode.network.serializer.MFCom_Message;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.TerminalInfoUtil;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.basecode.utils.contants.FileConstants;
import com.mf.data.PromDBU;
import com.mf.network.object.AdInfo;
import com.mf.network.protocol.GetDeskFolderResp;
import com.mf.network.protocol.GetExitReq;
import com.mf.network.protocol.GetExitResp;
import com.mf.promotion.util.PromUtils;

public class ReqExitToService {
  public static final String TAG     = "ReqExit";
  protected Context          mContext;

  public ReqExitToService(Context c) {
    this.mContext = c;
  }

  public void sendRequest() {
    GetExitReq req = new GetExitReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext));
    ArrayList<PgInfo> installList = PromUtils.getInstalledAppList();
    ArrayList<PgInfo> uninstallList = PromUtils.getUninstalledAppList();
    req.setInstallList(installList);
    req.setUninstallList(uninstallList);
    String magicData = PromUtils.getInstance(mContext).getMagicData();
    req.setMagicData(magicData);
    
    Logger.e(TAG, req.toString());
    SharedPreferences sf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
    String promAddress = sf.getString(CommConstants.SESSION_PROM_ADD, "");
    if (TextUtils.isEmpty(promAddress)) {
      return;
    }
    HTTPConnection http = HTTPConnection.getInstance();
    MFCom_Message respMessage = http.post(promAddress, req);

    try {
      if (respMessage != null && respMessage.head.code == AttributeUitl.getMessageCode(GetExitResp.class)) {
        GetExitResp resp = (GetExitResp) respMessage.message;
        if (resp != null) {
          Logger.debug(TAG, resp.toString());
        }
        if (resp.getErrorCode() == 0) {
          handleRespMessage(resp);
        } else {
          Logger.error(TAG, "GetExitResp  Error Message" + resp.getErrorMessage());
        }
      } else {
        Logger.error("Get GetExitResp request error;");
      }
    } catch (Exception e) {
      Logger.p(e);
    }
  }

  private void handleRespMessage(GetExitResp resp) {
    if (resp != null) {
      PromDBU.getInstance(mContext).deleteYesterdayAdInfoByPromType(PromDBU.PROM_EXIT);
      SaveMessage(resp);
      if (resp.getAdInfoList() != null) {
        Logger.debug(TAG, "size()=" + resp.getAdInfoList().size());
        ExitRunnable r = new ExitRunnable(resp);
        new Thread(r).start();
      }
    }
  }
  
  private void SaveMessage(GetExitResp resp){
    try {
      if(!TextUtils.isEmpty(resp.getMagicData())){
        PromUtils.getInstance(mContext).saveMagicData(resp.getMagicData());
      }
      SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
      Editor ed = spf.edit();
      ed.putInt(CommConstants.EXIT_PROTECT_TIME, resp.getProtectTimesSeconds());
      ed.putInt(CommConstants.EXIT_TIMES, resp.getShowTimes());
      ed.putString(CommConstants.EXIT_PACKAGES, resp.getPackageList());
      ed.putString(CommConstants.EXIT_SSPID, resp.getSspid());
      ed.commit();
    } catch (Exception e) {
      Logger.p(e);
    }
  }

  class ExitRunnable implements Runnable {
    private List<AdInfo> contentList;
    public ExitRunnable(GetExitResp resp) {
      super();
      this.contentList = resp.getAdInfoList();
    }
    @Override
    public void run() {      
      File iconDir = new File(FileConstants.getFloatDir(mContext));
      if (!iconDir.exists()) {
        iconDir.mkdirs();
      }
      for (AdInfo info : contentList) {
        File iconFile = new File(iconDir, PromUtils.getPicNameFromPicUrl(info.getAdPicUrl()));
        if (!iconFile.exists()) {
          File iconFileDt = new File(iconDir, PromUtils.getPicNameFromPicUrl(info.getAdPicUrl()) + ".dt");
          if (iconFileDt.exists()) {
            iconFileDt.delete();
          }
          try {
            downImage(iconFile, iconFileDt, info.getAdPicUrl());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      for (AdInfo info : contentList) {
        File iconFile = new File(iconDir, PromUtils.getPicNameFromPicUrl(info.getAdPicUrl()));
        if (iconFile.exists()) {
          PromDBU.getInstance(mContext).insertAdInfo(info, PromDBU.PROM_EXIT, PromDBU.PROM_EXIT_NAME);
        }
      }
      Logger.e(TAG, "handle  start");
    }
  }
  
  public void downImage(File dest, File temp, String downurl) {
    try {
      URL url = new URL(downurl);
      URLConnection con = url.openConnection();
      InputStream is = con.getInputStream();
      byte[] bs = new byte[1024 * 2];
      int len;
      OutputStream os = new FileOutputStream(temp);
      while ((len = is.read(bs)) != -1) {
        os.write(bs, 0, len);
      }
      os.close();
      is.close();
      temp.renameTo(dest);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
