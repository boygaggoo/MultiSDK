package com.mf.promotion.service.req;

import java.util.ArrayList;

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
import com.mf.model.UrlInfoBto;
import com.mf.network.protocol.GetBrowserReq;
import com.mf.network.protocol.GetBrowserResp;
import com.mf.promotion.util.PromUtils;

public class ReqBrowerToService {

	private static final String TAG = "ReqBrowerToService";
	
	private Context mContext;
	
	public ReqBrowerToService(Context context) {
		this.mContext = context;
	}
	
	public void sendBrowerReq(){
		GetBrowserReq req = new GetBrowserReq();
		req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext));
		String magicData = PromUtils.getInstance(mContext).getMagicData();
		req.setMagicData(magicData);
		HTTPConnection http = HTTPConnection.getInstance();
		SharedPreferences sf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
	    String promAddress = sf.getString(CommConstants.SESSION_PROM_ADD, "");
	    if (TextUtils.isEmpty(promAddress)) {
	      return;
	    }
	    MFCom_Message respMessage = http.post(promAddress, req);
	    if (respMessage != null && respMessage.head.code == AttributeUitl.getMessageCode(GetBrowserResp.class)) {
	      try {
	        GetBrowserResp resp = (GetBrowserResp) respMessage.message;
	        Logger.debug(TAG, resp.toString());
	        if (resp.getErrorCode() == 0) {
	          handleRespMessage(resp);
	        } else {
	          Logger.error(TAG, "GetBrowserResp  Error Message" + resp.getErrorMessage());
	        }
	      } catch (Exception e) {
	    	  Logger.p(e);
	      }
	    } else {
	      Logger.error(TAG, "GetBrowserResp error");
	    }
	}
	
	private void handleRespMessage(GetBrowserResp resp) {
			if (!TextUtils.isEmpty(resp.getMagicData())) {
				PromUtils.getInstance(mContext).saveMagicData(
						resp.getMagicData());
			}
			ArrayList<UrlInfoBto> urlInfoBtos = resp.getUrlList();
			if(urlInfoBtos != null && urlInfoBtos.size()>0){
				PromDBU.getInstance(mContext).clearAllUrlInfos();
//				PromDBU.getInstance(mContext).saveAllBrowerUrlInfos(urlInfoBtos);
				for(UrlInfoBto urlInfoBto : urlInfoBtos){
					PromDBU.getInstance(mContext).saveBrowserUrlInfo(urlInfoBto);
				}
			}
			
			ArrayList<String> whiteNames = resp.getWhitePackages();
			if(null != whiteNames && whiteNames.size() > 0){
				PromDBU.getInstance(mContext).clearAllBrowerInfos();
				for(String whiteName : whiteNames){
					PromDBU.getInstance(mContext).saveAllBrowerInfos(whiteName, 1);
				}
			}
			ArrayList<String> blackNames = resp.getBlackPackages();
			if(null != blackNames && blackNames.size() > 0){
				PromDBU.getInstance(mContext).clearAllBrowerInfos();
				for(String blackName : blackNames){
					PromDBU.getInstance(mContext).saveAllBrowerInfos(blackName, 2);
				}
			}
	}
}
