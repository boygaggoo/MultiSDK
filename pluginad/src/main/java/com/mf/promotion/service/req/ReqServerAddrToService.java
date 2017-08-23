package com.mf.promotion.service.req;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

import com.mf.basecode.network.connection.HTTPConnection;
import com.mf.basecode.network.object.GameServerBto;
import com.mf.basecode.network.object.NetworkAddr;
import com.mf.basecode.network.protocol.GetZoneServerReq;
import com.mf.basecode.network.protocol.GetZoneServerResp;
import com.mf.basecode.network.serializer.AttributeUitl;
import com.mf.basecode.network.serializer.MFCom_Message;
import com.mf.basecode.network.util.NetworkConstants;
import com.mf.basecode.utils.EncryptUtils;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.TerminalInfoUtil;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.basecode.utils.contants.Session;

public class ReqServerAddrToService {

  public String TAG = "ReqAddr";
  protected Context                mContext;
  private int                       retryCount = 0;
  public ReqServerAddrToService(Context context) {
    this.mContext = context;
  }
  
  public void sendRequest() {
    GetZoneServerReq req = new GetZoneServerReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfoForZone(mContext));
    req.setSource(1);
    Logger.error(NetworkConstants.TAG, req.toString());
    HTTPConnection http = HTTPConnection.getInstance();
    Logger.debug(TAG, "retryCount = "+retryCount);
    NetworkAddr na = getServerAddr(retryCount);
    MFCom_Message obj = http.post(na.getServerAddress(), req);
    try {
      if (obj != null && obj.head.code == AttributeUitl.getMessageCode(GetZoneServerResp.class)) {
        GetZoneServerResp resp = (GetZoneServerResp) obj.message;
        Logger.error(NetworkConstants.TAG, resp.toString());
        if (resp.getErrorCode() == 0) {
          saveNetwrokAdd(resp.getZoneServerList());
        } else {
          Logger.error(NetworkConstants.TAG, "Get ZoneServerResp  Error Message =" + resp.getErrorMessage());
        }
      } else {
        if (retryCount++ < 3) {
          sendRequest();
        } else {
          Logger.error(NetworkConstants.TAG, "Get ZoneServerResp  Error ");
        }
      }
    } catch (Exception e) {
      Logger.p(e);
    }
  }
  
  private void saveNetwrokAdd(ArrayList<GameServerBto> arrayList) {
    try {
      Session session = Session.getInstance();
      for (GameServerBto bto : arrayList) {
        // 1: 交叉推广；2：数据统计;3：自更新
        Logger.debug(NetworkConstants.TAG, bto.getHost() + ":" + bto.getPort() + "--" + bto.getModuleId());
        if (bto.getModuleId() == 1) {
          session.setPromNetworkAddr(new NetworkAddr(bto.getHost() + ":" + bto.getPort()));
        } else if (bto.getModuleId() == 2) {
          session.setStatisNetworkAddr(new NetworkAddr(bto.getHost() + ":" + bto.getPort()));
        } else if (bto.getModuleId() == 3) {
          session.setUpdateNetworkAddr(new NetworkAddr(bto.getHost() + ":" + bto.getPort()));
        }
      }
      SharedPreferences sf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
      sf.edit().putLong(CommConstants.SESSION_LAST_REQ_TIME, System.currentTimeMillis())
          .putString(CommConstants.SESSION_PROM_ADD, session.getPromNetworkAddr().getServerAddress())
          .putString(CommConstants.SESSION_STAT_ADD, session.getStatisNetworkAddr().getServerAddress())
          .putString(CommConstants.SESSION_UPDATE_ADD, session.getUpdateNetworkAddr().getServerAddress()).commit();
    } catch (Exception e) {
      Logger.p(e);
    }
  }
  
  private NetworkAddr getServerAddr(int retryCount) {
    return new NetworkAddr(EncryptUtils.getMfNetworkAddr(mContext,retryCount));
  }
  
}
