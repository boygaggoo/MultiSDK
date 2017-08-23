package com.mf.promotion.activity;

import android.app.Activity;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.BundleConstants;
import com.mf.basecode.utils.contants.FileConstants;
import com.mf.data.PromDBU;
import com.mf.model.AdDbInfo;
import com.mf.promotion.util.PromApkConstants;
import com.mf.promotion.util.PromUtils;
import com.mf.promotion.util.ScreenUtils;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.statistics.prom.util.StatsPromUtils;
import com.mf.utils.ResourceIdUtils;

public class PromDesktopAdActivity extends Activity {
  private static final String TAG        = "PromDesktopAdActivity";
  private Handler             mHandler   = new Handler();
  private RelativeLayout      rl_main;
  private ImageView           iv_ad, iv_close;
  private AdDbInfo            adInfo;
  private Intent              intent;
//  private MediaPlayer         mMediaPlayer;
  private int                 exit       = 0;
  private int                 position   = -1;
  private String              adid;
  private List<String>        list = new ArrayList<String>();
  private boolean showNext = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Logger.e(TAG, "oncreate");
//    int width = ScreenUtils.getScreenWidth(that);
//    int height = ScreenUtils.getScreenHeight(that);
//    if (width > height) {
//      Logger.e(TAG, "width > height");
//      that.finish();
//      return;
//    }
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    super.onCreate(savedInstanceState);
    
    if (intent != null) {
      initadListFromString(intent.getStringExtra(BundleConstants.BUNDLE_DESKTOP_ADS));
      startShowDesktopAd();
    }
    IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    registerReceiver(homeListenerReceiver, homeFilter);

  }
  
  public void startShowDesktopAd(){
    if(list.size() <= 0){
      finish();
    }
    Iterator<String> iter = list.iterator();  
    while(iter.hasNext()){  
      String str = iter.next();  
      if(!TextUtils.isEmpty(str)){
        adid = str;
        Logger.e(TAG, "startShowDesktopAd  adid = "+adid);
        adInfo = PromDBU.getInstance(this).getAdInfobyAdid(adid, PromDBU.PROM_DESKTOPAD);
        iter.remove();
        showImageAd();
        return ;
      }else{
        iter.remove();
      }
    }   
  }
  
  private void initadListFromString(String ads) {
    Logger.e(TAG, "initadListFromString  ads = "+ads);
    if (!TextUtils.isEmpty(ads)) {
      String[] adids = ads.split(",");
      for (int i = 0; i < adids.length; i++) {
        if (!TextUtils.isEmpty(adids[i]) && !list.contains(adids[i])) {
          list.add(adids[i]);
        }
      }
    }
  }
  

  /**
   * 展示图片广告
   * 
   * @param bitmap
   */
  @SuppressWarnings("deprecation")
  private void showAdView(Bitmap bitmap) {
    rl_main = new RelativeLayout(this);
    rl_main.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    rl_main.setBackgroundResource(android.R.color.transparent);
    
    if (adInfo.getAdDisplayType() == PromApkConstants.PROM_AD_INFO_AD_TYPE_IMAGE){
      View rl = LayoutInflater.from(this).inflate(
          getResources().getLayout(ResourceIdUtils.getInstance().getResourceId("R.layout.mf_prom_desktop_ad_layout")), null);
      iv_ad = (ImageView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.mf_iv_bg"));
      iv_close = (ImageView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.mf_iv_close"));
      iv_close.setBackgroundDrawable(getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_btn_close")));
      iv_ad.setScaleType(ScaleType.FIT_XY);
      iv_ad.setImageBitmap(bitmap);
      iv_close.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          showNext = true;
          Logger.e(TAG, "140 showNext = "+showNext);
          finish();
        }
      });

      RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
      params1.addRule(RelativeLayout.CENTER_IN_PARENT);
      rl_main.addView(rl, params1);
      addCenterView();
      addMiddleCloseView(iv_close);
      position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_MIDDLE;
    }else if(adInfo.getAdDisplayType() == PromApkConstants.PROM_AD_INFO_AD_TYPE_IMAGE_ICON){
      if (adInfo.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_LEFT) {
        // 左侧广告
        RelativeLayout top = new RelativeLayout(this);
        int width = ScreenUtils.dip2px(this, 48);
        iv_ad = new ImageView(this);
        iv_ad.setScaleType(ScaleType.FIT_XY);
        iv_ad.setImageBitmap(bitmap);
        top.addView(iv_ad, width, width);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width, width);
        param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        param.addRule(RelativeLayout.CENTER_VERTICAL);
        rl_main.addView(top, param);
        addRightCloseView(top);
        addIconView(1, width);
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_LEFT;
      } else if (adInfo.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_RIGHT) {
        // 右侧广告
        RelativeLayout top = new RelativeLayout(this);
        int width = ScreenUtils.dip2px(this, 48);
        iv_ad = new ImageView(this);
        iv_ad.setScaleType(ScaleType.FIT_XY);
        iv_ad.setImageBitmap(bitmap);
        top.addView(iv_ad, width, width);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width, width);
        param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        param.addRule(RelativeLayout.CENTER_VERTICAL);
        rl_main.addView(top, param);
        addLeftCloseView(top);
        addIconView(2, width);
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_RIGHT;
      }
    }else if(adInfo.getAdDisplayType() == PromApkConstants.PROM_AD_INFO_AD_TYPE_IMAGE_HF){
      if (adInfo.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_TOP) {
        // 顶部广告
        RelativeLayout top = new RelativeLayout(this);
        iv_ad = new ImageView(this);
        iv_ad.setScaleType(ScaleType.FIT_XY);
        iv_ad.setImageBitmap(bitmap);
        top.addView(iv_ad, RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dip2px(this, 64));
        rl_main.addView(top, RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dip2px(this, 64));
        addCenterCloseView(top);
        addBannerView(1, ScreenUtils.dip2px(this, 64));
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_TOP;
      } else if (adInfo.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_BOTTOM) {
        // 底部广告
        RelativeLayout top = new RelativeLayout(this);
        iv_ad = new ImageView(this);
        iv_ad.setScaleType(ScaleType.FIT_XY);
        iv_ad.setImageBitmap(bitmap);
        top.addView(iv_ad, RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dip2px(this, 64));
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dip2px(this, 64));
        param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rl_main.addView(top, param);
        addCenterCloseView(top);
        addBannerView(2, ScreenUtils.dip2px(this, 64));
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_BOTTOM;
      }
    }else if(adInfo.getAdDisplayType() == PromApkConstants.PROM_AD_INFO_AD_TYPE_IMAGE_AND_TEXT){
      if (adInfo.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_TOP) {
        // 顶部广告
        View rl = LayoutInflater.from(this).inflate(
            getResources().getLayout(ResourceIdUtils.getInstance().getResourceId("R.layout.desktop_textimage")), null);
        iv_ad = (ImageView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_icon"));
        TextView name = (TextView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_app_name"));
        name.setText(adInfo.getAdName());
        Button bt = (Button)rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.download_button"));
        bt.setBackgroundDrawable(getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_banner_botton_bg")));
        TextView content = (TextView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_app_content"));
        content.setText(adInfo.getAdLanguage());
        iv_close = (ImageView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_close"));
        iv_close.setBackgroundDrawable(getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_btn_close")));
        iv_ad.setScaleType(ScaleType.FIT_XY);
        iv_ad.setImageBitmap(bitmap);
        iv_close.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            showNext = true;
            Logger.e(TAG, "229 showNext = "+showNext);
            finish();
          }
        });
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rl_main.addView(rl, params1);
        addMiddleCloseView(iv_close);
        rl.setOnClickListener(imageAdClickListener);
        addBannerView(1, ScreenUtils.dip2px(this, 64));
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_TOP;
      } else if (adInfo.getPosition() == PromApkConstants.PROM_AD_INFO_AD_IMAGE_BOTTOM) {
        // 底部广告
        View rl = LayoutInflater.from(this).inflate(
            getResources().getLayout(ResourceIdUtils.getInstance().getResourceId("R.layout.desktop_textimage")), null);
        iv_ad = (ImageView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_icon"));
        TextView name = (TextView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_app_name"));
        name.setText(adInfo.getAdName());
        Button bt = (Button)rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.download_button"));
        bt.setBackgroundDrawable(getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_banner_botton_bg")));
        TextView content = (TextView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_app_content"));
        content.setText(adInfo.getAdLanguage());
        iv_close = (ImageView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.desktop_close"));
        iv_close.setBackgroundDrawable(getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_btn_close")));
        iv_ad.setScaleType(ScaleType.FIT_XY);
        iv_ad.setImageBitmap(bitmap);
        iv_close.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            showNext = true;
            Logger.e(TAG, "259 showNext = "+showNext);
            finish();
          }
        });
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rl_main.addView(rl, params1);
        addMiddleCloseView(iv_close);
        rl.setOnClickListener(imageAdClickListener);
        addBannerView(2, ScreenUtils.dip2px(this, 64));
        position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_BOTTOM;
      }
     
    }
    Logger.e(TAG, "adInfo.getIconUrl()" + adInfo.getAdPicUrl());
    iv_ad.setOnClickListener(imageAdClickListener);
    Logger.e(TAG, "adInfo.getRemainTime() = " + adInfo.getRemainTimes());
    if(adInfo.getRemainTimes() > 0){
      autoClose(adInfo.getRemainTimes());
    }
    setContentView(rl_main);
    // 展现数据统计
//    PromDBU.getInstance(that).updateAdInfoHasShowTimes(adInfo, PromDBU.PROM_DESKTOPAD, PromDBU.PROM_DESKTOPAD_NAME);
//    StatsPromUtils.getInstance(that).addDisplayAction(adid+"/"+adInfo.getPackageName(),position);
//    if(adInfo != null){
//      PromDBU.getInstance(that).resetShowmark(PromDBU.PROM_DESKTOPAD);
//      PromDBU.getInstance(that).updateAdInfoShowmark(adInfo, PromDBU.PROM_DESKTOPAD);
//    }
    
  }
  private void autoClose(int remainTime) {
    if (remainTime > 0) {
      Handler h = new Handler();
      h.postDelayed(new Runnable() {
        
        @Override
        public void run() {
          showNext = true;
          Logger.e(TAG, "297 showNext = "+showNext);
          finish();;
        }
      }, remainTime*1000);
    }
  }

  View.OnClickListener imageAdClickListener = new OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                Logger.e(TAG, "click image ad.");
                                                showNext = true;
                                                Logger.e(TAG, "309 showNext = "+showNext);
                                                if (adInfo == null) {
                                                  Logger.e(TAG, "adInfo is null.");
                                                  return;
                                                }
//                                                StatsPromUtils.getInstance(that).addClickAction(adid+"/"+adInfo.getPackageName(),position);
                                                Intent intent = PromUtils.getInstance(PromDesktopAdActivity.this).clickPromAppInfoListener(adInfo.getAdType(),adid,PromDBU.PROM_DESKTOPAD,
                                                    position);
                                                if (intent != null) {
                                                  if (adInfo.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_APK) {
                                                    Logger.e(TAG, "startService image ad.");
                                                    PromDesktopAdActivity.this.startService(intent);
                                                    PromDesktopAdActivity.this.finish();
                                                  } else {
                                                    Logger.e(TAG, "startActivity image ad.");
                                                    PromDesktopAdActivity.this.finish();
                                                    PromDesktopAdActivity.this.startActivity(intent);
                                                  }
                                                } else {
                                                  Logger.e(TAG, "intent is null;");
                                                  PromDesktopAdActivity.this.finish();
                                                }
                                              }
                                            };

  /**
   * 添加右上角关闭按钮
   */
  private void addCloseView() {
    mHandler.postDelayed(new Runnable() {

      @Override
      public void run() {
        // TODO Auto-generated method stub
        try {
          Logger.debug(TAG, "addCloseView");
          RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
              RelativeLayout.LayoutParams.WRAP_CONTENT);
          params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
          params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
          iv_close = new ImageView(PromDesktopAdActivity.this);
          iv_close.setImageDrawable(PromDesktopAdActivity.this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_btn_close")));
          iv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              Logger.e(TAG, "354 showNext = "+showNext);
              showNext = true;
              PromDesktopAdActivity.this.finish();
            }
          });
          rl_main.addView(iv_close, params1);
        } catch (Exception e) {
          Logger.p(e);
        }
      }
    }, 2000);
  }
  
  private void addMiddleCloseView(final ImageView image){
    image.setVisibility(View.GONE);
    mHandler.postDelayed(new Runnable() {

      @Override
      public void run() {
        if(image != null){
          image.setVisibility(View.VISIBLE);
        }
      }
    }, 2000);
  }

  private void addCenterCloseView(final RelativeLayout rl) {
    mHandler.postDelayed(new Runnable() {

      @Override
      public void run() {
        try {
          Logger.debug(TAG, "addCloseView");
          RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
              RelativeLayout.LayoutParams.WRAP_CONTENT);
          params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
          params1.addRule(RelativeLayout.CENTER_VERTICAL);
          iv_close = new ImageView(PromDesktopAdActivity.this);
          iv_close.setImageDrawable(PromDesktopAdActivity.this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_btn_close")));
          iv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              showNext = true;
              Logger.e(TAG, "398 showNext = "+showNext);
              PromDesktopAdActivity.this.finish();
            }
          });
          rl.addView(iv_close, params1);
        } catch (Exception e) {
          Logger.p(e);
        }
      }
    }, 2000);
  }

  private void addLeftCloseView(final RelativeLayout rl) {
    mHandler.postDelayed(new Runnable() {

      @Override
      public void run() {
        try {
          Logger.debug(TAG, "addCenterCloseView");
          RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
              RelativeLayout.LayoutParams.WRAP_CONTENT);
          params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
          params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
          iv_close = new ImageView(PromDesktopAdActivity.this);
          iv_close.setImageDrawable(PromDesktopAdActivity.this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_btn_close")));
          iv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              showNext = true;
              Logger.e(TAG, "428 showNext = "+showNext);
              PromDesktopAdActivity.this.finish();
              Logger.e(TAG, "click  addLeftCloseView");
            }
          });
          rl.addView(iv_close, params1);
        } catch (Exception e) {
          Logger.p(e);
        }
      }
    }, 2000);
  }

  private void addRightCloseView(final RelativeLayout rl) {
    mHandler.postDelayed(new Runnable() {

      @Override
      public void run() {
        try {
          Logger.debug(TAG, "addCenterCloseView");
          RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
              RelativeLayout.LayoutParams.WRAP_CONTENT);
          params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
          params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
          iv_close = new ImageView(PromDesktopAdActivity.this);
          iv_close.setImageDrawable(PromDesktopAdActivity.this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_btn_close")));
          iv_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              showNext = true;
              Logger.e(TAG, "460 showNext = "+showNext);
              PromDesktopAdActivity.this.finish();
              Logger.e(TAG, "click  addRightCloseView");
            }
          });
          rl.addView(iv_close, params1);
        } catch (Exception e) {
          Logger.p(e);
        }

      }
    }, 2000);
  }

  private void addIconView(int direction, int width) {
    Logger.e(TAG, "addIconView direction = " + direction);
    WindowManager.LayoutParams params = getWindow().getAttributes();
    params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    params.format = PixelFormat.RGBA_8888;
    params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | 
                   WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;//
    if (direction == 1) {
      params.gravity = Gravity.CENTER | Gravity.LEFT;
    } else {
      params.gravity = Gravity.CENTER | Gravity.RIGHT;
    }
    params.width = width;
    params.height = width;
    int color = Color.argb(0x00, 0x00, 0x00, 0x00);
    ColorDrawable drawable = new ColorDrawable(color);
    getWindow().setBackgroundDrawable(drawable);
    getWindow().setAttributes(params);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
  }

  private void addBannerView(int direction, int height) {
    Logger.e(TAG, "addBannerView direction = " + direction);
    WindowManager.LayoutParams params = getWindow().getAttributes();
    params.type = WindowManager.LayoutParams.TYPE_PHONE;
    params.format = PixelFormat.RGBA_8888;
    params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;//
    if (direction == 1) {
      params.gravity = Gravity.CENTER | Gravity.TOP;
    } else {
      params.gravity = Gravity.CENTER | Gravity.BOTTOM;
    }
    params.width = ScreenUtils.getScreenWidth(this);
    params.height = height;
    int color = Color.argb(0x00, 0x00, 0x00, 0x00);
    ColorDrawable drawable = new ColorDrawable(color);
    getWindow().setBackgroundDrawable(drawable);
    getWindow().setAttributes(params);
//    that.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
  }

  private void addCenterView() {
    Logger.e(TAG, "addCenterView ");
    WindowManager.LayoutParams params = getWindow().getAttributes();
    params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    params.format = PixelFormat.RGBA_8888;
    params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;//
    params.gravity = Gravity.CENTER;
    params.width = ScreenUtils.dip2px(this, 300);
    params.height = params.width;
    int color = Color.argb(0x00, 0x00, 0x00, 0x00);
    ColorDrawable drawable = new ColorDrawable(color);
    getWindow().setBackgroundDrawable(drawable);
    getWindow().setAttributes(params);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
  }

  private void startNextDesktopAd() {
    String ads = "";
    if(list.size() <= 0){
      return;
    }
    for (String aid : list) {
      if(!TextUtils.isEmpty(aid)){
        ads = ads+aid+",";
      }
    }
    Logger.e(TAG, "startNextDesktopAd  ads = "+ads);
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.setComponent(new ComponentName(getPackageName(), PromApkConstants.HOST_PROXY_ACTIVITY));
    intent.putExtra(PromApkConstants.EXTRA_CLASS, PromDesktopAdActivity.class.getCanonicalName());
    intent.putExtra(BundleConstants.BUNDLE_DESKTOP_ADS, ads);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Logger.debug(TAG, "startNextDesktopAd ");
    startActivity(intent);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Logger.e(TAG, " onDestroy");
    if(showNext){
      PromDBU.getInstance(this).updateAdInfoHasShowTimes(adInfo, PromDBU.PROM_DESKTOPAD, PromDBU.PROM_DESKTOPAD_NAME);
      StatsPromUtils.getInstance(this).addDisplayAction(adid+"/"+adInfo.getPackageName(),position);
      if(adInfo != null){
        PromDBU.getInstance(this).resetShowmark(PromDBU.PROM_DESKTOPAD);
        PromDBU.getInstance(this).updateAdInfoShowmark(adInfo, PromDBU.PROM_DESKTOPAD);
      }
      startNextDesktopAd();
    }
    if (homeListenerReceiver != null) {
      unregisterReceiver(homeListenerReceiver);
   }
  }

  private void showImageAd() {
    Logger.e(TAG, "doInBackground and url = " + adInfo.getAdPicUrl());
    // 显示图片，默认icon
    Bitmap bitmap = null;
    try {
      File imagePathFile = new File(FileConstants.getAdsDir(this)/*PromApkConstants.PROM_AD_IMAGES_PATH*/);
      if (!imagePathFile.exists()) {
        imagePathFile.mkdirs();
      }
      File f = new File(imagePathFile, adInfo.getPicName());
      bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
    } catch (Exception e) {
      Logger.p(e);
    } catch (OutOfMemoryError e) {
      Logger.p(e);
    }
    if (bitmap != null) {
      Logger.e(TAG, "bitmap success.");
      showAdView(bitmap);
    } else {
      Logger.e(TAG, " bitmap == null.");
      finish();
    }
  }


  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      exit++;
      if (exit > 1) {
        Logger.e(TAG, "662 showNext = "+showNext);
        showNext = true;
        finish();
      }
      return false;
    }
    return true;
  }
  
  
  
  public void setFoucsChange() {
    // TODO Auto-generated method stub
    Logger.error(TAG, "setFoucsChange = !showNext =  "+!showNext);
    if( !showNext){
      finish();
      Handler h = new Handler();
      h.postDelayed(new Runnable() {
        
        @Override
        public void run() {
          String ads = "";
          Logger.e(TAG, "onWindowFocusChanged  handler ");
          for (String aid : list) {
            if(!TextUtils.isEmpty(aid)){
              ads = ads+aid+",";
            }
          }
          ads = adid+","+ads;
          Logger.e(TAG, "startNextDesktopAd  ads = "+ads);
          Intent intent = new Intent(Intent.ACTION_MAIN);
          intent.setComponent(new ComponentName(getPackageName(), PromApkConstants.HOST_PROXY_ACTIVITY));
          intent.putExtra(PromApkConstants.EXTRA_CLASS, PromDesktopAdActivity.class.getCanonicalName());
          intent.putExtra(BundleConstants.BUNDLE_DESKTOP_ADS, ads);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          Logger.debug(TAG, "startNextDesktopAd ");
          startActivity(intent);
        }
      }, 1000);
    }
  }

  private BroadcastReceiver homeListenerReceiver = new BroadcastReceiver() {
    final String SYSTEM_DIALOG_REASON_KEY = "reason";

    final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (reason != null && reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                  Logger.e(TAG, "717 showNext = "+showNext);
                  showNext = true;
                  finish();
            }
        }
    }
};

}
