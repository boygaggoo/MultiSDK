package com.mf.promotion.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.network.util.NetworkConstants;
import com.mf.basecode.network.util.NetworkUtils;
import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.basecode.utils.contants.FileConstants;
import com.mf.data.PromDBU;
import com.mf.download.model.DownloadInfo;
import com.mf.download.util.DownloadUtils;
import com.mf.model.AdDbInfo;
import com.mf.promotion.service.MFApkServiceFactory;
import com.mf.promotion.util.PromApkConstants;
import com.mf.promotion.util.PromUtils;
import com.mf.promotion.util.ScreenUtils;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.statistics.prom.util.StatsPromUtils;
import com.mf.utils.AppInstallUtils;
import com.mf.utils.ResourceIdUtils;
//import android.util.Log;

public class HandleDesktopAdService extends HandleService {
  public static final String TAG = "HandleDpAd";
  private static List<AdDbInfo> list;
//  private static String ads = "";
  private static TimerTask mTimerTask = null;
  private static boolean isRunning = false;
  private static AdDbInfo curAdDbInfo = null;
  private static int position   = -1;
  private static RelativeLayout rl_main = null;
  private volatile static WindowManager mWindowManager = null;
  public static final int LEFT = 1;
  public static final int RIGHT = 2;
  public static final int TOP = 3;
  public static final int BOTTOM = 4;
  private Handler             handler = new Handler();
  public int errorDspId = -1;
  public boolean dspFail = false;
//  private volatile static AdView mAdblock = null;
//  
//  private volatile static AdView mSmallblock = null;
  private volatile static RelativeLayout rl_blockview = null;
  
  public HandleDesktopAdService(int serviceId, Context context, Handler handler) {
    super(serviceId, context, handler);
  }

  @Override
  public void onStartCommand(Intent intent, int flags, int startId) {
    Logger.e(TAG, "onStartCommand"); 
    boolean connect = PromUtils.netIsConnected(mContext);
    Logger.e(TAG, "onStartCommand  1111"); 
    if(!connect){
      Logger.e(TAG, "connect"); 
      return ;
    }
    
    if(!PromUtils.getInstance(mContext).checkHost() || !HandleService.pSwitch(spf, CommConstants.SHOW_RULE_DESKTOP)){
      Logger.e(TAG, "checkHost false");
      if (mTimerTask != null) {
        mTimerTask.cancel();
        isRunning = false;
      }
      return;
    }
    errorDspId = -1;
    if(android.os.Build.VERSION.SDK_INT < 21){
    	doDesktopTimer();
    }else{
//    	doDesktopTimerAL();
    	handleAndroidLDesktopAd();
    }
  }
  
	private void handleAndroidLDesktopAd() {
		list = getHandleAdInfoList(
				StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_TOP,
				CommConstants.CONFIG_RICE_MEDIA_SHOW_LIMIT,
				CommConstants.CONFIG_RICE_MEDIA_SHOW_ONE_TIMENUM, -1,
				PromDBU.PROM_DESKTOPAD);
		if(list.size() == 0){
			return;
		}
		handledAdList(list);
		if(showAdTime()){
			showDesktopImage(false);
		}
		startNextService(CommConstants.SHOW_RULE_DESKTOP, MFApkServiceFactory.HANDLE_DESKTOP_AD_SERVICE.getServiceId());
	}
  
	private void doDesktopTimerAL() {
	    Timer t = new Timer();
	    Logger.e(TAG, "doDesktopTimer isRunning   "+isRunning);
	    if (mTimerTask != null) {
	      mTimerTask.cancel();
	      isRunning = false;
	    }

	    mTimerTask = new TimerTask() {
	      @Override
	      public void run() {
	        Logger.e(TAG, "isRunning   "+isRunning);
	        if(!isRunning){
	          isRunning = true;
	              list = getHandleAdInfoList(StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_TOP,CommConstants.CONFIG_RICE_MEDIA_SHOW_LIMIT, CommConstants.CONFIG_RICE_MEDIA_SHOW_ONE_TIMENUM,-1, PromDBU.PROM_DESKTOPAD);
	            if(list.size() == 0){
	              Logger.e(TAG, "list.size() == 0 ");
	              if (mTimerTask != null) {
	                Logger.e(TAG, "mTimerTask != null");
	                mTimerTask.cancel();
	                mTimerTask = null;
	                isRunning = false;
	                return;
	              }
	            }
	            handledAdList(list);            
	            handler.post(new Runnable() {
	              @Override
	              public void run() {
	                if(showAdTime()){
	                  showDesktopImage(false);
	                }
	              }
	            });
	          isRunning = false;
	        }
	      }
	    };
	    t.schedule(mTimerTask, 0, 1000);
	  }
	
  private void doDesktopTimer() {
    Timer t = new Timer();
    Logger.e(TAG, "doDesktopTimer isRunning   "+isRunning);
    if (mTimerTask != null) {
      mTimerTask.cancel();
      isRunning = false;
    }

    mTimerTask = new TimerTask() {
      @Override
      public void run() {
        Logger.e(TAG, "isRunning   "+isRunning);
        if(!isRunning){
          isRunning = true;
          boolean inapp = PromUtils.getInstance(mContext).isInApp();
          String topPgName = PromUtils.getInstance(mContext).getTopPackageName();
          Logger.e(TAG, "inapp = "+inapp+"   topPgName = "+topPgName+"   mCurrentPgName = "+mCurrentPgName);
          if(inapp && TextUtils.isEmpty(mCurrentPgName)){
            mCurrentPgName = topPgName;
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            if(dspFail){
              list = getHandleAdInfoList(StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_TOP,CommConstants.CONFIG_RICE_MEDIA_SHOW_LIMIT, CommConstants.CONFIG_RICE_MEDIA_SHOW_ONE_TIMENUM,1, PromDBU.PROM_DESKTOPAD);
            }else{
              list = getHandleAdInfoList(StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_TOP,CommConstants.CONFIG_RICE_MEDIA_SHOW_LIMIT, CommConstants.CONFIG_RICE_MEDIA_SHOW_ONE_TIMENUM,-1, PromDBU.PROM_DESKTOPAD);
            }
            dspFail = false;
            Logger.m(TAG, list.toString());
            if(list.size() == 0){
              Logger.e(TAG, "list.size() == 0 ");
              if (mTimerTask != null) {
                Logger.e(TAG, "mTimerTask != null");
                mTimerTask.cancel();
                mTimerTask = null;
                isRunning = false;
                return;
              }
            }
            handledAdList(list);            
            handler.post(new Runnable() {
              @Override
              public void run() {
                if(showAdTime()){
                  showDesktopImage(false);
                }
              }
            });
          }else if(!inapp){
            Logger.m(TAG, "not in app");
            mCurrentPgName = "";
            handler.post(new Runnable() {
              @Override
              public void run() {
                removeDesktopView(true);
              }
            });
            if(list != null){
              list.clear();
            }
          }
          isRunning = false;
        }
      }
    };
    t.schedule(mTimerTask, 0, 1000);
  }

  public static void stopService(){
    if (mTimerTask != null) {
      Logger.m(TAG, "stopService");
      mTimerTask.cancel();
      mTimerTask = null;
      isRunning = false;
      removeDesktopView(true);
      return;
    }
  }
  
  public void handledAdList(final List<AdDbInfo> adlist) {
    try {
      File iconDir = new File(FileConstants.getAdsDir(mContext)/*PromApkConstants.PROM_AD_IMAGES_PATH*/);
      if (!iconDir.exists()) {
        iconDir.mkdirs();
      }
      
      for (AdDbInfo info : adlist) {
        File iconFile = new File(iconDir, info.getPicName());
        if (!iconFile.exists()) {
          File iconFileDt = new File(iconDir, info.getPicName() + ".dt");
          if (iconFileDt.exists()) {
            iconFileDt.delete();
          }
          try {
            URL url = new URL(info.getAdPicUrl());
            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();
            byte[] bs = new byte[1024 * 2];
            int len;
            OutputStream os = new FileOutputStream(iconFileDt);
            while ((len = is.read(bs)) != -1) {
              os.write(bs, 0, len);
            }
            os.close();
            is.close();
            iconFileDt.renameTo(iconFile);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      
      Iterator<AdDbInfo> iter = list.iterator();  
      while(iter.hasNext()){  
        AdDbInfo info = iter.next();  
        File iconFile = new File(iconDir, info.getPicName());
        if (!iconFile.exists()) {
          iter.remove();
        }
      }  
      
      Iterator<AdDbInfo> it = list.iterator();  
      while(it.hasNext()){  
        doDesktop(it);
      }
    } catch (Exception e) {
      // TODO: handle exception
    }
        
  }
  
  private synchronized static void removeDesktopView(boolean all){
    try {
//      if(mAdblock != null){
//        mAdblock.closeAd();
//      }
      if(all){
        cleanSmallBlock();
      }
      if(mWindowManager != null){
        Logger.m(TAG, "mWindowManager != null");
        if(rl_main != null){
          Logger.m(TAG, "mWindowManager removeView(rl_main)");
          rl_main.removeAllViews();
          mWindowManager.removeView(rl_main);
          rl_main = null;
        }
      }
    } catch (Exception e) {
      Logger.e(TAG, "aaa   removeDesktopView  "+e.toString());
    }
  }
  
  private void showDesktopImage(boolean closeclick){
    if(closeclick){
      removeDesktopView(false);
    }else{
      removeDesktopView(true);
    }
    Logger.e(TAG, "*********list.size() = "+list.size()+"  closeclick="+closeclick);
    try {
      if(list != null && list.size() > 0){
        curAdDbInfo = list.get(0);
        cleanSmallBlock();
        if(android.os.Build.VERSION.SDK_INT >= 21){
        	if(curAdDbInfo != null){
                Logger.m(TAG, "show "+curAdDbInfo.toString());
                Logger.e(TAG, "*********show ");
                showImageAd();
              }
        }else{
        	if(curAdDbInfo != null && !TextUtils.isEmpty(mCurrentPgName)){
                Logger.m(TAG, "show "+curAdDbInfo.toString());
                Logger.e(TAG, "*********show ");
                showImageAd();
              }
        }
        
        list.remove(curAdDbInfo);
      }else{
        if(rl_blockview != null){
          Logger.e(TAG, "*********rl_blockview ");
//          smallAdView();
        }
      }
    } catch (Exception e) {
      // TODO: handle exception
    }
  }
  
  
  private void showImageAd() {
    Logger.e(TAG, "doInBackground and url = " + curAdDbInfo.getAdPicUrl());
    // 显示图片，默认icon
    Bitmap bitmap = null;
    try {
      File imagePathFile = new File(FileConstants.getAdsDir(mContext)/*PromApkConstants.PROM_AD_IMAGES_PATH*/);
      if (!imagePathFile.exists()) {
        imagePathFile.mkdirs();
      }
      File f = new File(imagePathFile, curAdDbInfo.getPicName());
      bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
    } catch (Exception e) {
      Logger.p(e);
    } catch (OutOfMemoryError e) {
      Logger.p(e);
    }
//    if (bitmap != null) {
      Logger.e(TAG, "showAdView");
//      initSmallblock(curAdDbInfo);
      showAdView(bitmap);
//    } else {
//      Logger.e(TAG, " bitmap == null.");
//    }
  }
  
  /**
   * 展示图片广告
   * 
   * @param bitmap
   */
  @SuppressWarnings("deprecation")
  private void showAdView(Bitmap bitmap) {
    if(rl_main == null){
      rl_main = new RelativeLayout(mContext.getApplicationContext());
    }
    rl_main.removeAllViews();
    ImageView iv_ad = null;
    ImageView iv_close = null;
    WindowManager.LayoutParams wparams = null;
    rl_main.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    rl_main.setBackgroundResource(android.R.color.transparent);
    if (curAdDbInfo.getAdDisplayType() == PromApkConstants.PROM_AD_INFO_AD_TYPE_IMAGE){
      Logger.e(TAG, "111");
      RelativeLayout.LayoutParams params1 = null;
      View rl = null;
      if(curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_WAP || curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_APK){
        rl = LayoutInflater.from(mContext.getApplicationContext()).inflate(
            mContext.getResources().getLayout(ResourceIdUtils.getInstance().getResourceId("R.layout.mf_prom_desktop_ad_layout")), null);
        iv_ad = (ImageView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.mf_iv_bg"));
        iv_close = (ImageView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.mf_iv_close"));
        iv_close.setBackgroundDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_btn_close")));
        iv_ad.setScaleType(ScaleType.FIT_XY);
        if(bitmap != null){
        	iv_ad.setImageBitmap(bitmap);
        }
        iv_close.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            showDesktopImage(true);
          }
        });
  
        params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params1.addRule(RelativeLayout.CENTER_IN_PARENT);
        addMiddleCloseView(iv_close);
      }
      
//      else if(curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_DSP){
//        rl = LayoutInflater.from(mContext.getApplicationContext()).inflate(
//            mContext.getResources().getLayout(ResourceIdUtils.getInstance().getResourceId("R.layout.mf_prom_desktop_ad_layout_dsp")), null);
//        RelativeLayout iv_dsp = (RelativeLayout) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.mf_dsp"));
//        iv_close = (ImageView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.mf_iv_close"));
//        iv_close.setBackgroundDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.ad_btn_close")));
//        View dspview = dspView(curAdDbInfo,AdSize.INNER,false,false,rl_main); 
//        iv_dsp.addView(dspview);
//        iv_close.setOnClickListener(new OnClickListener() {
//          @Override
//          public void onClick(View v) {
//            showDesktopImage(true);
//          }
//        });
//  
//        params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params1.addRule(RelativeLayout.CENTER_IN_PARENT);
//        addMiddleCloseView(iv_close);
//        rl_main.setVisibility(View.GONE);
//      }
      if(rl != null && params1 != null){
        rl_main.addView(rl, params1);
      }
      wparams = getCenterWindPatams();
      position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_MIDDLE;
    }else if(curAdDbInfo.getAdDisplayType() == PromApkConstants.PROM_AD_INFO_AD_TYPE_IMAGE_ICON){
      if (curAdDbInfo.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_LEFT) {
        Logger.e(TAG, "222");
        // 左侧广告
        RelativeLayout top = new RelativeLayout(mContext.getApplicationContext());
        int width = ScreenUtils.dip2px(mContext.getApplicationContext(), 48);
        iv_ad = new ImageView(mContext.getApplicationContext());
        iv_ad.setScaleType(ScaleType.FIT_XY);
        if(bitmap != null){
        	iv_ad.setImageBitmap(bitmap);
        }
        top.addView(iv_ad, width, width);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width, width);
        param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        param.addRule(RelativeLayout.CENTER_VERTICAL);
        addIconCloseView(top,RIGHT);
        rl_main.addView(top, param);
        wparams = getIconWindPatams(LEFT, width);
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_LEFT;
      } else if (curAdDbInfo.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_RIGHT) {
        Logger.e(TAG, "333");
        // 右侧广告
        RelativeLayout top = new RelativeLayout(mContext.getApplicationContext());
        int width = ScreenUtils.dip2px(mContext.getApplicationContext(), 48);
        iv_ad = new ImageView(mContext.getApplicationContext());
        iv_ad.setScaleType(ScaleType.FIT_XY);
        if(bitmap != null){
        	iv_ad.setImageBitmap(bitmap);
        }
        top.addView(iv_ad, width, width);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width, width);
        param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        param.addRule(RelativeLayout.CENTER_VERTICAL);
        rl_main.addView(top, param);
        addIconCloseView(top,LEFT);
        wparams = getIconWindPatams(RIGHT, width);
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_RIGHT;
      }
    }else if(curAdDbInfo.getAdDisplayType() == PromApkConstants.PROM_AD_INFO_AD_TYPE_IMAGE_HF){
      if (curAdDbInfo.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_TOP) {
        Logger.e(TAG, "444");
        // 顶部广告
        int h = ScreenUtils.dip2px(mContext.getApplicationContext(), 64);
        RelativeLayout top = null;
        if(curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_WAP || curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_APK){
          top = new RelativeLayout(mContext.getApplicationContext());
          iv_ad = new ImageView(mContext.getApplicationContext());
          iv_ad.setScaleType(ScaleType.FIT_XY);
          if(bitmap != null){
        	  iv_ad.setImageBitmap(bitmap);
          }
          top.addView(iv_ad, RelativeLayout.LayoutParams.MATCH_PARENT, h);
            addCenterCloseView(top);
        }
//        else if(curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_DSP){
//          top = dspView(curAdDbInfo,AdSize.BANNER,true,true,null);
//        }     
        if(top != null){
          rl_main.addView(top, RelativeLayout.LayoutParams.MATCH_PARENT, h);
        }
        wparams = getBannerWindPatams(TOP, h);
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_TOP;
      } else if (curAdDbInfo.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_BOTTOM) {
        Logger.e(TAG, "555");
        // 底部广告
        int h = ScreenUtils.dip2px(mContext.getApplicationContext(), 64);
        RelativeLayout top = null;
        RelativeLayout.LayoutParams param = null;
        if(curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_WAP || curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_APK){
          top = new RelativeLayout(mContext.getApplicationContext());
          iv_ad = new ImageView(mContext.getApplicationContext());
          iv_ad.setScaleType(ScaleType.FIT_XY);
          if(bitmap != null){
        	  iv_ad.setImageBitmap(bitmap);
          }
          top.addView(iv_ad, RelativeLayout.LayoutParams.MATCH_PARENT, h);
          addCenterCloseView(top);
        }
//        else if(curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_DSP){
//          top = dspView(curAdDbInfo,AdSize.BANNER,true,true,null);
//        }
        
        param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, h);
        param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if(top != null && param != null){
          rl_main.addView(top, param);
        }
        wparams = getBannerWindPatams(BOTTOM, h);
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_BOTTOM;
      }
    }else if(curAdDbInfo.getAdDisplayType() == PromApkConstants.PROM_AD_INFO_AD_TYPE_IMAGE_AND_TEXT){
      if (curAdDbInfo.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_TOP) {
        Logger.e(TAG, "666");
        // 顶部广告
        View rl = null;
        RelativeLayout blockView = null;
        if(curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_WAP || curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_APK){
          rl = LayoutInflater.from(mContext.getApplicationContext()).inflate(
              mContext.getResources().getLayout(ResourceIdUtils.getInstance().getResourceId("R.layout.desktop_textimage")), null);
          rl.setBackgroundDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_banner_bg")));
          iv_ad = (ImageView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_icon"));
          TextView name = (TextView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_app_name"));
          name.setText(curAdDbInfo.getAdName());
          Button bt = (Button)rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.download_button"));
          bt.setBackgroundDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_banner_botton_bg")));
          bt.setClickable(false);
          TextView content = (TextView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_app_content"));
          content.setText(curAdDbInfo.getAdLanguage());
          iv_close = (ImageView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_close"));
          iv_close.setBackgroundDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_btn_close")));
          iv_ad.setScaleType(ScaleType.FIT_XY);
          if(bitmap != null){
        	  iv_ad.setImageBitmap(bitmap);
          }
          iv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              showDesktopImage(true);
            }
          });
          addMiddleCloseView(iv_close);
          rl.setOnClickListener(imageAdClickListener);
        }
//        else if(curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_DSP){
//          blockView = dspView(curAdDbInfo,AdSize.BANNER,true,true,null);
//        }        
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        if(rl != null){
          rl_main.addView(rl, params1);
        }else if(blockView != null){
          rl_main.addView(blockView, params1);
        }
        wparams = getBannerWindPatams(TOP, ScreenUtils.dip2px(mContext, 64));
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_TOP;
      } else if (curAdDbInfo.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_BOTTOM) {
        Logger.e(TAG, "777");
        // 底部广告
        View rl = null;
        RelativeLayout blockView = null;
        if(curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_WAP || curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_APK){
          rl = LayoutInflater.from(mContext.getApplicationContext()).inflate(
            mContext.getResources().getLayout(ResourceIdUtils.getInstance().getResourceId("R.layout.desktop_textimage")), null);
          rl.setBackgroundDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_banner_bg")));
          iv_ad = (ImageView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_icon"));
          TextView name = (TextView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_app_name"));
          name.setText(curAdDbInfo.getAdName());
          Button bt = (Button)rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.download_button"));
          bt.setBackgroundDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_banner_botton_bg")));
          bt.setClickable(false);
          TextView content = (TextView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_app_content"));
          content.setText(curAdDbInfo.getAdLanguage());
          iv_close = (ImageView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_close"));
          iv_close.setBackgroundDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_btn_close")));
          iv_ad.setScaleType(ScaleType.FIT_XY);
          if(bitmap != null){
        	  iv_ad.setImageBitmap(bitmap);
          }
          iv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              showDesktopImage(true);
            }
          });
          addMiddleCloseView(iv_close);
          rl.setOnClickListener(imageAdClickListener);
        }
//        else if(curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_DSP){
//          blockView = dspView(curAdDbInfo,AdSize.BANNER,true,true,null);
//        }        
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dip2px(mContext, 64));
        params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if(rl != null){
          rl_main.addView(rl, params1);
        }else if(blockView != null){
          rl_main.addView(blockView, params1);
        }
        wparams = getBannerWindPatams(BOTTOM, ScreenUtils.dip2px(mContext, 64));
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_BOTTOM;
      }
    }
    Logger.e(TAG, "adInfo.getIconUrl()" + curAdDbInfo.getAdPicUrl());
    if(iv_ad != null){
      iv_ad.setOnClickListener(imageAdClickListener);
    }
    Logger.e(TAG, "adInfo.getRemainTime() = " + curAdDbInfo.getRemainTimes());
    if(curAdDbInfo.getRemainTimes() > 0){
      autoClose(curAdDbInfo.getRemainTimes());
    }
    if(mWindowManager == null){
      mWindowManager = (WindowManager)mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE); 
    }
    
    
    mWindowManager.addView(rl_main, wparams); 
    if(curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_WAP || curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_APK){
      errorDspId = -1;
      PromDBU.getInstance(mContext).updateAdInfoHasShowTimes(curAdDbInfo, PromDBU.PROM_DESKTOPAD, PromDBU.PROM_DESKTOPAD_NAME);
      StatsPromUtils.getInstance(mContext).addDisplayAction(curAdDbInfo.getAdId()+"/"+curAdDbInfo.getPackageName(),position);
      PromDBU.getInstance(mContext).resetShowmark(PromDBU.PROM_DESKTOPAD);
      PromDBU.getInstance(mContext).updateAdInfoShowmark(curAdDbInfo, PromDBU.PROM_DESKTOPAD);
    }
    
  }
  
//  public RelativeLayout dspView(AdDbInfo info,int type,boolean isbanner,final boolean adclose,final View parent){
//    int h = ScreenUtils.dip2px(mContext.getApplicationContext(), 64);
//    
//    final RelativeLayout blockView = new RelativeLayout(mContext.getApplicationContext());
//    String a = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.APPID_METADATA_KEY);
//    String c = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.CHANNELID_METADATA_KEY);
//    String pi = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.CPID_METADATA_KEY);
//    mAdblock = new AdView(mContext, AdSize.BANNER, info.getAdId(),pi+"@"+a,pi+"@"+c);
//    mAdblock.loadAd();
//    mAdblock.setAdCallback(new AdCallback() {
//		
//		@Override
//		public void onSuccess() {
//			errorDspId = -1;
//	        if(adclose){
//	        	Logger.e(TAG, " add closed view ");	     
//		        addLeftTopCloseView(blockView,false);
//	        }
//	        if(parent != null){
//	          parent.setVisibility(View.VISIBLE);
//	        }
//	        PromDBU.getInstance(mContext).updateAdInfoHasShowTimes(curAdDbInfo, PromDBU.PROM_DESKTOPAD, PromDBU.PROM_DESKTOPAD_NAME);
//	        StatsPromUtils.getInstance(mContext).addDisplayAction(curAdDbInfo.getAdId()+"/"+curAdDbInfo.getPackageName(),position);
//	        PromDBU.getInstance(mContext).resetShowmark(PromDBU.PROM_DESKTOPAD);
//	        PromDBU.getInstance(mContext).updateAdInfoShowmark(curAdDbInfo, PromDBU.PROM_DESKTOPAD);
//		}
//		
//		@Override
//		public void onFailed(boolean arg0, String arg1) {
//		       PromDBU.getInstance(mContext).resetShowmark(PromDBU.PROM_DESKTOPAD);
//		        PromDBU.getInstance(mContext).updateAdInfoShowmark(curAdDbInfo, PromDBU.PROM_DESKTOPAD);
//		        if(errorDspId == -1){
//		          errorDspId = curAdDbInfo.getKeyid();
//		        }
//		        if(list != null && list.size() > 0){
//		          showDesktopImage(false);
//		        }else{
//		          removeDesktopView(true);
//		          list = getHandleAdInfoList(StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_TOP,CommConstants.CONFIG_RICE_MEDIA_SHOW_LIMIT, CommConstants.CONFIG_RICE_MEDIA_SHOW_ONE_TIMENUM,1, PromDBU.PROM_DESKTOPAD);
//		          boolean exist = false;
//		          for (AdDbInfo info : list) {
//		            if(info.getKeyid() == curAdDbInfo.getKeyid() || info.getKeyid() == errorDspId){
//		              exist = true;
//		            }
//		          }
//		          if(!exist){
//		            mCurrentPgName = "";
//		            dspFail = true;
//		          }
//		        }
//		}
//	});
//  
//    if(isbanner){
//    	Logger.e(TAG, " blockView add mAdblock ");
//      blockView.addView(mAdblock,RelativeLayout.LayoutParams.MATCH_PARENT, h);
//    }else{
//      blockView.addView(mAdblock,RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//    }
//    return blockView;
//  }
  
  
  View.OnClickListener imageAdClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      Logger.e(TAG, "click image ad.");
      if (curAdDbInfo == null) {
        Logger.e(TAG, "adInfo is null.");
        return;
      }
//      StatsPromUtils.getInstance(that).addClickAction(adid+"/"+adInfo.getPackageName(),position);
      Intent intent = PromUtils.getInstance(mContext.getApplicationContext()).clickPromAppInfoListener(curAdDbInfo.getAdType(),curAdDbInfo.getAdId(),PromDBU.PROM_DESKTOPAD,
          position);
      if (intent != null) {
        if (curAdDbInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_APK) {
          Logger.e(TAG, "startService image ad.");
          mContext.startService(intent);
        } else {
          Logger.e(TAG, "startActivity image ad.");
          mContext.startActivity(intent);
        }
      } else {
        Logger.e(TAG, "intent is null;");
      }
      showDesktopImage(false);
    }
  };
  
  
  private void autoClose(int remainTime) {
    if (remainTime > 0) {
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          showDesktopImage(false);
        }
      }, remainTime * 1000);
    }
  }
  
  
  private void addCenterCloseView(final RelativeLayout rl) {
    rl.postDelayed(new Runnable() {

      @Override
      public void run() {
        // TODO Auto-generated method stub
        try {
          Logger.debug(TAG, "addCenterCloseView");
          RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
              RelativeLayout.LayoutParams.WRAP_CONTENT);
          params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
          params1.addRule(RelativeLayout.CENTER_VERTICAL);
          ImageView iv_close = new ImageView(mContext.getApplicationContext());
          iv_close.setImageDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_btn_close")));
          iv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              showDesktopImage(true);
            }
          });
          rl.addView(iv_close, params1);
        } catch (Exception e) {
          Logger.p(e);
        }
      }
    }, 3000);
  }
  
  private void addLeftTopCloseView(final RelativeLayout rl,final boolean all) {
    if(rl != null){
      rl.postDelayed(new Runnable() {
  
        @Override
        public void run() {
          try {
            Logger.debug(TAG, "addLeftTopCloseView");
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
            params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params1.addRule(RelativeLayout.CENTER_VERTICAL);
            ImageView iv_close = new ImageView(mContext.getApplicationContext());
            iv_close.setImageDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.ad_btn_close")));
            iv_close.setOnClickListener(new OnClickListener() {
              @Override
              public void onClick(View v) {
                if(all){
                  showDesktopImage(false);
                }else{
                  showDesktopImage(true);
                }
                
              }
            });
            rl.addView(iv_close, params1);
          } catch (Exception e) {
            Logger.p(e);
          }
        }
      }, 1000);
    }
  }
  
  private void addIconCloseView(final RelativeLayout rl,final int direction) {
    rl.postDelayed(new Runnable() {

      @Override
      public void run() {
        try {
          Logger.debug(TAG, "addIconCloseView");
          RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
              RelativeLayout.LayoutParams.WRAP_CONTENT);
          if(direction == RIGHT){
            params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
          }else{
            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
          }
          params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
          ImageView iv_close = new ImageView(mContext.getApplicationContext());
          iv_close.setImageDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_btn_close")));
          iv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              showDesktopImage(true);
              Logger.e(TAG, "click  addIconCloseView");
            }
          });
          rl.addView(iv_close, params1);
        } catch (Exception e) {
          Logger.p(e);
        }

      }
    }, 3000);
  }
  
  private WindowManager.LayoutParams getIconWindPatams(int direction, int width) {
    Logger.e(TAG, "getIconWindPatams direction = " + direction);
    WindowManager.LayoutParams params = new WindowManager.LayoutParams();  
    params.type = LayoutParams.TYPE_PHONE;
    params.format = PixelFormat.RGBA_8888;
    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//
    if (direction == LEFT) {
      params.gravity = Gravity.CENTER | Gravity.LEFT;
    } else {
      params.gravity = Gravity.CENTER | Gravity.RIGHT;
    }
    params.width = width;
    params.height = width;
    return params;
  }
  
  private WindowManager.LayoutParams getCenterWindPatams() {
    Logger.e(TAG, "getCenterWindPatams ");
    WindowManager.LayoutParams params = new WindowManager.LayoutParams();  
    params.type = WindowManager.LayoutParams.TYPE_PHONE;
    params.format = PixelFormat.RGBA_8888;
    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//
    params.gravity = Gravity.CENTER;
    params.width = ScreenUtils.dip2px(mContext.getApplicationContext(), 300);
    params.height = params.width;
    return params;
  }
  private WindowManager.LayoutParams getBannerWindPatams(int direction, int height) {
    Logger.e(TAG, "getBannerWindPatams direction = " + direction);
    WindowManager.LayoutParams params = new WindowManager.LayoutParams();  
    params.type = WindowManager.LayoutParams.TYPE_PHONE;
    params.format = PixelFormat.RGBA_8888;
    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//
    if (direction == TOP) {
      params.gravity = Gravity.CENTER | Gravity.TOP;
    } else {
      params.gravity = Gravity.CENTER | Gravity.BOTTOM;
    }
    params.width = RelativeLayout.LayoutParams.MATCH_PARENT;;
    params.height = height;
    return params;
  }
  
  private void addMiddleCloseView(final ImageView image){
    image.setVisibility(View.GONE);
    image.postDelayed(new Runnable() {

      @Override
      public void run() {
        // TODO Auto-generated method stub
        if(image != null){
          image.setVisibility(View.VISIBLE);
        }
      }
    }, 3000);
  }
  
//  private void clickCloseView(){
//    if(rl_main != null){
//      mWindowManager.removeView(rl_main);
//      rl_main = null;
//    }
//    if (curAdDbInfo != null) {
//      PromDBU.getInstance(that).resetShowmark(PromDBU.PROM_DESKTOPAD);
//      PromDBU.getInstance(that).updateAdInfoShowmark(adInfo, PromDBU.PROM_DESKTOPAD);
//    }
//    if(!TextUtils.isEmpty(curAdDbInfo.getInApp()) && !curAdDbInfo.getInApp().equals("0")){
//      curAdDbInfo =  PromDBUtils.getInstance(mContext).getFirstDesktopImageAd();
//      showImageAd(curAdDbInfo);
//    }
//  }

  
  private void doDesktop(Iterator<AdDbInfo> it){
    AdDbInfo info =  it.next();
    PackageInfo pInfo = AppInstallUtils.getPackageInfoByPackageName(mContext, info.getPackageName());
    String apkPath = DownloadUtils.getInstance(mContext).getApkDownloadFilePath(info.getPackageName(), info.getVersionCode());
    boolean no_check = TextUtils.isEmpty(info.getReserved1()) && info.getReserved1().equals("1");
    if (pInfo != null && !no_check) {
      if (!PromUtils.getInstance(mContext).hasInstalled(new MyPackageInfo(info.getPackageName(), info.getVersionCode()))) {
        File f = new File(apkPath);
        if (!f.exists()) {
          it.remove();
        }
      } 
    } else {
      File f = new File(apkPath);
      if (!f.exists()) {
        if ((info.getAdType() == 1 && info.getPreDown() == 1 && NetworkUtils.getNetworkType(mContext) == NetworkConstants.NERWORK_TYPE_WIFI) || info.getPreDown()== 2){
          DownloadInfo dinfo = new DownloadInfo(null,info.getAdId(),
              info.getPackageName(), info.getVersionCode(), getDeskAdPosition(info), 0, info.getAdDownUrl(), info.getFileMd5(), true,true);
          MyPackageInfo packageInfo = new MyPackageInfo(info.getAdId(),info.getPackageName(), info.getVersionCode(), getDeskAdPosition(info), 0, true);
          if(DownloadUtils.getInstance(mContext).isDownloadOrWait(dinfo, packageInfo)){
            Logger.e(TAG, "down11111 ");
//            DownloadUtils.getInstance(mContext).addExtraHandler(downloadHandler);
          }else if(info.getAdType() == 1){
            Logger.e(TAG, "down22222 ");
            DownloadUtils.getInstance(mContext).addDownloadApkThread(dinfo);
          }
        }
      }
    }
  }
  
  
  
  public static int getDeskAdPosition(AdDbInfo info){
    int position = -2;
    if (info.getAdDisplayType() == PromApkConstants.PROM_AD_INFO_AD_TYPE_IMAGE){
      position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_MIDDLE;
    }else if(info.getAdDisplayType() == PromApkConstants.PROM_AD_INFO_AD_TYPE_IMAGE_ICON){
      if (info.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_LEFT) {
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_LEFT;
      } else if (info.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_RIGHT) {
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_RIGHT;
      }
    }else if(info.getAdDisplayType() == PromApkConstants.PROM_AD_INFO_AD_TYPE_IMAGE_HF){
      if (info.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_TOP) {
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_TOP;
      } else if (info.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_BOTTOM) {
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_BOTTOM;
      }
    }else if(info.getAdDisplayType() == PromApkConstants.PROM_AD_INFO_AD_TYPE_IMAGE_AND_TEXT){
      if (info.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_TOP) {
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_TOP;
      } else if (info.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_BOTTOM) {
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_BOTTOM;
      }
    }
    return position;
  }
  
  
//  private void initSmallblock(final AdDbInfo info) {
//    cleanSmallBlock();
//    if(TextUtils.isEmpty(info.getSspid())){
//      return;
//    }
//    int type = 0;
//    int h = ScreenUtils.dip2px(mContext.getApplicationContext(), 64);
//    rl_blockview = new RelativeLayout(mContext.getApplicationContext());
//    String a = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.APPID_METADATA_KEY);
//    String c = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.CHANNELID_METADATA_KEY);
//    String pi = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.CPID_METADATA_KEY);
//    if(info.getSsptype() == PromApkConstants.DSP_STYLE_BANNER){
//      type = AdSize.BANNER;
//    }else if(info.getSsptype() == PromApkConstants.DSP_STYLE_INTERSTITIAL){
//      type = AdSize.INNER;
//    }
//    mSmallblock = new AdView(mContext, type, info.getSspid(), pi + "@" + a, pi + "@" + c);
//    mSmallblock.loadAd();
//    mSmallblock.setAdCallback(new AdCallback() {
//
//      @Override
//      public void onSuccess() {
////        errorDspId = -1;
//        if (info.getSsptype() == PromApkConstants.DSP_STYLE_BANNER) {
//          Logger.e(TAG, " add closed view ");
//          addLeftTopCloseView(rl_blockview,true);
//        }
//        StatsPromUtils.getInstance(mContext).addDisplayAction(info.getSspid() + "/", position);
//      }
//
//      @Override
//      public void onFailed(boolean arg0, String arg1) {
//        cleanSmallBlock();
//      }
//    });
//
//    if (info.getSsptype() == PromApkConstants.DSP_STYLE_BANNER) {
//      Logger.e(TAG, " blockView add mAdblock ");
//      rl_blockview.addView(mSmallblock, RelativeLayout.LayoutParams.MATCH_PARENT, h);
//    } else {
//      rl_blockview.addView(mSmallblock, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//    }
//
//  }
  
  
  
//  private void smallAdView() {
//    if(rl_main == null){
//      rl_main = new RelativeLayout(mContext.getApplicationContext());
//    }
//    rl_main.removeAllViews();
//    rl_blockview.setVisibility(View.VISIBLE);
//    rl_blockview.requestFocus();
//    if(mSmallblock != null){
//      mSmallblock.refresh();
//    }
//    ImageView iv_close = null;
//    WindowManager.LayoutParams wparams = null;
//    rl_main.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
//    rl_main.setBackgroundResource(android.R.color.transparent);
//    if (curAdDbInfo.getSsptype() == PromApkConstants.DSP_STYLE_INTERSTITIAL){
//      Logger.e(TAG, "111");
//      RelativeLayout.LayoutParams params1 = null;
//      View rl = null;
//      rl = LayoutInflater.from(mContext.getApplicationContext()).inflate(
//            mContext.getResources().getLayout(ResourceIdUtils.getInstance().getResourceId("R.layout.mf_prom_desktop_ad_layout_dsp")), null);
//        RelativeLayout iv_dsp = (RelativeLayout) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.mf_dsp"));
//        iv_close = (ImageView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.mf_iv_close"));
//        iv_close.setBackgroundDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.ad_btn_close")));
//        iv_dsp.addView(rl_blockview);
//        iv_close.setOnClickListener(new OnClickListener() {
//          @Override
//          public void onClick(View v) {
//            showDesktopImage(false);
//          }
//        });
//  
//        params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params1.addRule(RelativeLayout.CENTER_IN_PARENT);
//        addMiddleCloseView(iv_close);
//      if(rl != null && params1 != null){
//        rl_main.addView(rl, params1);
//      }
//      wparams = getCenterWindPatams();
//      position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_MIDDLE;
//    }else if(curAdDbInfo.getSsptype() == PromApkConstants.DSP_STYLE_BANNER){    
//        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dip2px(mContext, 64));
//        params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        if(rl_blockview != null){
//          rl_main.addView(rl_blockview, params1);
//        }
//        wparams = getBannerWindPatams(BOTTOM, ScreenUtils.dip2px(mContext, 64));
//        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_BOTTOM;
//    }
//    
//    if(mWindowManager == null){
//      mWindowManager = (WindowManager)mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE); 
//    }
//    mWindowManager.addView(rl_main, wparams); 
//  }
  
  private static void cleanSmallBlock(){
//    if(mSmallblock != null){
//      mSmallblock.closeAd();
//      mSmallblock = null;
//    }
    if(rl_blockview != null){
      rl_blockview.removeAllViews();
      rl_blockview = null;
    }
  }
  
  
  
  @Override
  public void handledAds(List<String> ads) {
    
  }
  
  
}
