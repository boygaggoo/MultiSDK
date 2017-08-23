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
import com.mf.network.protocol.GetDeskFolderReq;
import com.mf.network.protocol.GetDeskFolderResp;
import com.mf.promotion.util.PromUtils;



public class ReqFolderIconToService {

  public static final String TAG     = "ReqFolder";

  protected Context          mContext;

  public ReqFolderIconToService(Context c) {
    this.mContext = c;
  }

  public void sendRequest() {
    GetDeskFolderReq req = new GetDeskFolderReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext));
    ArrayList<PgInfo> installList = PromUtils.getInstalledAppList();
    ArrayList<PgInfo> uninstallList = PromUtils.getUninstalledAppList();
    req.setInstallList(installList);
    req.setUninstallList(uninstallList);
//    SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    String magicData = PromUtils.getInstance(mContext).getMagicData();//spf.getString(CommConstants.CONFIG_MAGIC_DATA, "");
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
      if (respMessage != null && respMessage.head.code == AttributeUitl.getMessageCode(GetDeskFolderResp.class)) {
        GetDeskFolderResp resp = (GetDeskFolderResp) respMessage.message;
        if (resp != null) {
          Logger.debug(TAG, resp.toString());
        }
        if (resp.getErrorCode() == 0) {
          handleRespMessage(resp);
        } else {
          Logger.error(TAG, "GetDeskFolderResp  Error Message" + resp.getErrorMessage());
        }
      } else {
        Logger.error("Get shortcut request error;");
      }
    } catch (Exception e) {
      Logger.p(e);
    }
  }

  private void handleRespMessage(GetDeskFolderResp resp) {
    if (resp != null) {
      PromDBU.getInstance(mContext).deleteYesterdayAdInfoByPromType(PromDBU.PROM_DESKFOLDER);
      PromDBU.getInstance(mContext).deleteYesterdayAdInfoByPromType(PromDBU.PROM_EXTRA);
      SaveMessage(resp);
      if (resp.getAdInfoList() != null) {
        Logger.debug(TAG, "size()=" + resp.getAdInfoList().size());
        FolderRunnable r = new FolderRunnable(mContext, resp);
        new Thread(r).start();
      }
    }
  }
  
  private void SaveMessage(GetDeskFolderResp resp){
    try {
      if(!TextUtils.isEmpty(resp.getMagicData())){
        PromUtils.getInstance(mContext).saveMagicData(resp.getMagicData());
      }
      SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
      Editor ed = spf.edit();
      ed.putString(CommConstants.FLOAT_TIMES, resp.getDisplayRule());
      ed.putInt(CommConstants.FLOAT_LOC, resp.getLocation());
      ed.putString(CommConstants.FLOAT_ICON_URL, resp.getIconUrl());
      ed.putString(CommConstants.EXTRA_TIMES, resp.getThirdDisplayRule());
      ed.putString(CommConstants.FLOAT_SSPID, resp.getSspid());
      AdInfo in = resp.getThirdAdInfoBto();
      if(in != null){
        ed.putString(CommConstants.EXTRA_ICON_URL, in.getAdPicUrl());
        PromDBU.getInstance(mContext).insertAdInfo(in, PromDBU.PROM_EXTRA, PromDBU.PROM_EXTRA_NAME);
      }else{
        ed.putString(CommConstants.EXTRA_ICON_URL, "");
      }
      ed.commit();
    } catch (Exception e) {
      Logger.p(e);
    }
  }

  class FolderRunnable implements Runnable {
    private Context          context;
    private List<AdInfo> contentList;
    private GetDeskFolderResp resp;
    public FolderRunnable(Context context, GetDeskFolderResp resp) {
      super();
      this.context = context;
      this.contentList = resp.getAdInfoList();
      this.resp = resp;
    }
    @Override
    public void run() {
      File iconDir = new File(FileConstants.getFloatIconDir(mContext));
      if (!iconDir.exists()) {
        iconDir.mkdirs();
      }
      File iconFile = new File(iconDir, PromUtils.getPicNameFromPicUrl(resp.getIconUrl()));
      if (!iconFile.exists()) {
        File iconFileDt = new File(iconDir, PromUtils.getPicNameFromPicUrl(resp.getIconUrl()) + ".dt");
        if (iconFileDt.exists()) {
          iconFileDt.delete();
        }
        try {
          downImage(iconFile, iconFileDt, resp.getIconUrl());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      
      AdInfo in = resp.getThirdAdInfoBto();
      if(in != null && !TextUtils.isEmpty(in.getAdPicUrl())){        
        iconFile = new File(iconDir, PromUtils.getPicNameFromPicUrl(in.getAdPicUrl()));
        if (!iconFile.exists()) {
          File iconFileDt = new File(iconDir, PromUtils.getPicNameFromPicUrl(in.getAdPicUrl()) + ".dt");
          if (iconFileDt.exists()) {
            iconFileDt.delete();
          }
          try {
            downImage(iconFile, iconFileDt, in.getAdPicUrl());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      
      iconDir = new File(FileConstants.getFloatDir(mContext));
      if (!iconDir.exists()) {
        iconDir.mkdirs();
      }
      for (AdInfo info : contentList) {
        iconFile = new File(iconDir, PromUtils.getPicNameFromPicUrl(info.getAdPicUrl()));
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
        iconFile = new File(iconDir, PromUtils.getPicNameFromPicUrl(info.getAdPicUrl()));
        if (iconFile.exists()) {
          PromDBU.getInstance(mContext).insertAdInfo(info, PromDBU.PROM_DESKFOLDER, PromDBU.PROM_DESKFOLDER_NAME);
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
