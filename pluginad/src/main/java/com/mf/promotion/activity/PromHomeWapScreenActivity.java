package com.mf.promotion.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.BundleConstants;
import com.mf.data.PromDBU;
import com.mf.model.AdDbInfo;
import com.mf.statistics.prom.util.StatsPromUtils;
import com.mf.utils.ResourceIdUtils;

public class PromHomeWapScreenActivity extends Activity {
  public static final String TAG  = "PromHomeWapScreenActivity";

  private WebView            wv_wap_screen;

  private LinearLayout       ll_general_loading;

  private int                position, source;

  private AdDbInfo           adInfo;

  private long               startActivityTime;
  private int                exit = 0;
  private View rl = null;
  private boolean            register = true; 
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Logger.debug(TAG, "PromHomeWapScreenActivity onCreate");
//    rl = LayoutInflater.from(that).inflate(
//        that.getResources().getLayout(ResourceIdUtils.getInstance().getResourceId("R.layout.mf_prom_home_wap_screen_activity")), null);
//    setContentView(rl);
    startActivityTime = System.currentTimeMillis();
    Intent i = getIntent();
    if (i != null) {      
      String adid = i.getStringExtra(BundleConstants.BUNDLE_AD_INFO_ADID);
      int promType = i.getIntExtra(BundleConstants.BUNDLE_AD_INFO_PROM_TYPE, -1);
      if(TextUtils.isEmpty(adid) || promType == -1){
        return;
      }
      adInfo = PromDBU.getInstance(this).getAdInfobyAdid(adid, promType);
      
      position = getIntent().getIntExtra(BundleConstants.BUNDLE_APP_INFO_POSITION, 1);
    }
    if (adInfo == null) {
      finish();
      return;
    }
    if(!TextUtils.isEmpty(adInfo.getReserved2()) && adInfo.getReserved2().equals("1")){
      register = false;
      Intent intent = new Intent();        
      intent.setAction("android.intent.action.VIEW");    
      Uri content_url = Uri.parse(adInfo.getAdDownUrl());
      intent.setData(content_url);           
      intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");   
      startActivity(intent);
      StatsPromUtils.getInstance(this).addClickAction(adInfo.getAdId()+"/"+adInfo.getPackageName(), position);
      finish();
      Logger.e(TAG, "browser "+adInfo.getAdDownUrl());
      return;
    }

    rl = LayoutInflater.from(this).inflate(
          getResources().getLayout(ResourceIdUtils.getInstance().getResourceId("R.layout.mf_prom_home_wap_screen_activity")), null);
    setContentView(rl);
    findViews();
    initViews();
    loadData();
    IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    registerReceiver(homeListenerReceiver, homeFilter);
    StatsPromUtils.getInstance(this).addClickAction(adInfo.getAdId()+"/"+adInfo.getPackageName(), position);
  }

  private void findViews() {
    wv_wap_screen = (WebView) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.mf_wv_wap_screen"));
    ll_general_loading = (LinearLayout) rl.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.mf_ll_general_loading"));
  }

  private void initViews() {
    WebSettings webSettings = wv_wap_screen.getSettings();
    webSettings.setSavePassword(false);
    webSettings.setSaveFormData(false);
    webSettings.setJavaScriptEnabled(true);
    webSettings.setSupportZoom(true);
    webSettings.setBuiltInZoomControls(true);
    wv_wap_screen.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.replaceAll(" ", "").contains("about:blank")) {
          return false;
        }
        view.loadUrl(url);
        return true;
      }
    });

    wv_wap_screen.setWebChromeClient(new WebChromeClient() {
      @Override
      public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress == 100)
          ll_general_loading.setVisibility(View.GONE);
      }
    });
  }

  protected void loadData() {
    wv_wap_screen.loadUrl(adInfo.getAdDownUrl());
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if ((keyCode == KeyEvent.KEYCODE_BACK) && wv_wap_screen.canGoBack()) {
      wv_wap_screen.goBack();
      return true;
    } else {
      exit++;
      if (exit > 1) {
        finish();
      }
      return false;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (homeListenerReceiver != null && register) {
      unregisterReceiver(homeListenerReceiver);
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
                  finish();
            }
        }
    }
};

}