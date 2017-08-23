package com.mf.promotion.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.mf.basecode.utils.FileUtils;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.FileConstants;
import com.mf.network.object.AdInfo;
import com.mf.promotion.util.PromUtils;

public class DownloadImageAdTask extends AsyncTask<AdInfo, Void, Void> {
  private final String      TAG = "prom";
  private OnPostExecuteBack callBack;
  private Context mContext;
  
  public DownloadImageAdTask(Context context) {
    this.mContext = context;
  }
  @Override
  public Void doInBackground(AdInfo... params) {
    if (params != null && params.length > 0) {
      for (AdInfo adInfo : params) {
        Logger.e(TAG, "doInBackground and url = " + adInfo.getAdPicUrl());
        // 显示图片，默认icon
        Bitmap bitmap = null;
        File imagePathFile = new File(FileConstants.getAdsDir(mContext)/*PromApkConstants.PROM_AD_IMAGES_PATH*/);
        if (!imagePathFile.exists()) {
          imagePathFile.mkdirs();
        }
        File f = new File(imagePathFile, PromUtils.getPicNameFromPicUrl(adInfo.getAdPicUrl()));
        if(f.exists()){
          bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        }else{
          try {
            f.createNewFile();
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        if (bitmap == null) {
          try {
            InputStream is = null;
            is = new URL(adInfo.getAdPicUrl()).openStream();
            OutputStream os = new FileOutputStream(f);
            FileUtils.copyStream(is, os);
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
          } catch (Exception ex) {
            ex.printStackTrace();
            bitmap = null;
          }
        }
      }
    }
    return null;
  }
  @Override
  protected void onPostExecute(Void result) {
    super.onPostExecute(result);
    if (callBack != null) {
      callBack.onPostExecute();
    }
  }

  public OnPostExecuteBack getCallBack() {
    return callBack;
  }
  public void setCallBack(OnPostExecuteBack callBack) {
    this.callBack = callBack;
  }

  public interface OnPostExecuteBack {
    public void onPostExecute();
  }

}