package com.xdd.pay.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.CookieSpecRegistry;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.xdd.pay.network.callback.Callback;
import com.xdd.pay.network.connect.HTTPConnection;
import com.xdd.pay.network.connect.ProxyHttpClient;
import com.xdd.pay.network.object.PayCodeInfo;
import com.xdd.pay.network.object.PayInfo;
import com.xdd.pay.network.protocol.GetWapPayInfoReq;
import com.xdd.pay.network.protocol.GetWapPayInfoResp;
import com.xdd.pay.network.util.NetworkConstants;
import com.xdd.pay.network.util.NetworkUtils;
import com.xdd.pay.util.constant.JsonParameter;

public class WapPayAsyncTask extends AsyncTask<PayCodeInfo, Integer, Boolean> {

	private PayCodeInfo curPayCodeInfo;
	private Context mContext;
	private Boolean mIsDoSilentPay;
	private PayInfo curPayInfo;

	public WapPayAsyncTask(PayCodeInfo info, Context context, boolean isDoSilentPay, final PayInfo curPayInfo) {
		super();
		this.curPayCodeInfo = info;
		this.mContext = context;
		this.mIsDoSilentPay = isDoSilentPay;
		this.curPayInfo = curPayInfo;
	}

	@Override
	protected Boolean doInBackground(PayCodeInfo... params) {
		try {
			PayCodeInfo info = new PayCodeInfo();
			// 代理请求方式,wap计费
			ProxyHttpClient client = new ProxyHttpClient(mContext);
			HttpGet get = new HttpGet(curPayCodeInfo.getCode());
			StringBuffer buf = new StringBuffer();
			try {
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String lines;
				while ((lines = reader.readLine()) != null) {
					buf.append(lines);
				}
			} catch (Exception e) {
				info.setCode("http exception");
				PayUtils.getInstance().fail(curPayCodeInfo, info, mIsDoSilentPay, curPayInfo);
				QYLog.e(e.toString());
			}

			final CookieStore cookie = client.getCookieStore();
			final CookieSpecRegistry specs = client.getCookieSpecs();

			GetWapPayInfoReq req = new GetWapPayInfoReq();
			req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext, true));
			req.setCpInfo(CpInfoUtils.getCpInfo(mContext));
			req.setLocationInfo(LocationUtils.getLocationInfo(mContext));
			req.setPayCode(curPayCodeInfo.getCode());
			req.setPrice(curPayCodeInfo.getPrice());
			try {
				req.setHtmlStr(buf.toString());
			} catch (Exception e) {
				info.setCode("zip exception");
				PayUtils.getInstance().fail(curPayCodeInfo, info, mIsDoSilentPay, curPayInfo);
				QYLog.e(e.toString());
			}
			
			JSONObject jsonObj = new JSONObject();
			try {
				jsonObj.accumulate(JsonParameter.TIME_STAMP, System.currentTimeMillis()+"");
				jsonObj.accumulate(JsonParameter.REQ_LINK_ID, PayUtils.getInstance().mLinkId);
				req.setJsonStr(jsonObj.toString());
			} catch (Exception e) {
				QYLog.e("doWebFirstPay jsonObj accumulate TIME_STAMP error:" + e);
			}

			HTTPConnection.getInstance().sendRequest(NetworkUtils.getNetworkAddr(), req, new Callback() {

				@Override
				public void onResp(int result, Object object) {
					String errorMessage = "";
					PayCodeInfo info = new PayCodeInfo();
					if (result == NetworkConstants.NETWORK_RESPONSE_SUCCESS) {
						GetWapPayInfoResp resp = (GetWapPayInfoResp) object;
						try {
							if(resp.getJsonStr()!=null && !TextUtils.isEmpty(resp.getJsonStr())){
								info.setJsonObj(resp.getJsonStr());
							}
						} catch (Exception e) {
							QYLog.e("reveiver resp json str error:" + e);
						}
						
						if (resp.getErrorCode() == 0) {
							HttpGet respGet = new HttpGet(resp.getUrl());
							ProxyHttpClient respClient = new ProxyHttpClient(mContext);
							respClient.setCookieSpecs(specs);
							respClient.setCookieStore(cookie);
							try {
								HttpResponse response = respClient.execute(respGet);
								info.setCode("resp status code:" + response.getStatusLine().getStatusCode());
								PayUtils.getInstance().success(curPayCodeInfo, info, mIsDoSilentPay, curPayInfo);
							} catch (Exception e) {
								errorMessage = "exception:" + info.getCode();
								info.setCode(errorMessage);
								PayUtils.getInstance().fail(curPayCodeInfo, info, mIsDoSilentPay, curPayInfo);
							}
						} else {
							errorMessage = "resp error:{" + resp.getErrorCode() + "," + info.getCode() + "}";
							info.setCode(errorMessage);
							PayUtils.getInstance().fail(curPayCodeInfo, info, mIsDoSilentPay, curPayInfo);
						}
					} else {
						errorMessage = "net conn :{" + info.getCode() + "}";
						info.setCode(errorMessage);
						PayUtils.getInstance().fail(curPayCodeInfo, info, mIsDoSilentPay, curPayInfo);
					}
				}
			});

		} catch (Exception e) {
			PayCodeInfo info = new PayCodeInfo();
			String errorMessage = "wap req exception{" + e.toString() + "}";
			info.setCode(errorMessage);
			PayUtils.getInstance().fail(curPayCodeInfo, info, mIsDoSilentPay, curPayInfo);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}
}
