package com.mf.promotion.activity;

import android.app.Activity;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.basecode.utils.contants.FileConstants;
import com.mf.data.PromDBU;
import com.mf.download.model.DownloadInfo;
import com.mf.download.util.DownloadUtils;
import com.mf.handler.ExitListHandler;
import com.mf.model.AdDbInfo;
import com.mf.promotion.util.PromApkConstants;
import com.mf.promotion.util.PromUtils;
import com.mf.promotion.util.ScreenUtils;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.statistics.prom.util.StatsPromUtils;
import com.mf.utils.AppInstallUtils;
import com.mf.utils.ResourceIdUtils;

public class ExitActivity extends Activity implements OnItemClickListener {
  private static final String   TAG             = "ExitActivity";
  private FloatWindowListAdaptr adapter;
  private int                   width;
  private List<AdDbInfo>  mFolderIconList = new ArrayList<AdDbInfo>();
  private View                  rl_main;
  private boolean               mOpen = false;
  private HashMap<String, View>   mViewMap = new HashMap<String, View>();
//  private HashMap<String, AdView>  mBlockMap = new HashMap<String, AdView>();
//  
//  private ArrayList<AdDownInfo>				mAdDownInfos;
//  private AppWall				mAppWall;
  
  private Handler mHandler = new Handler(){

    @Override
    public void handleMessage(Message msg) {
      if (adapter != null) {
        adapter.setDataList(mFolderIconList);
        adapter.notifyDataSetChanged();
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Logger.e(TAG, "oncreate");
    width = ScreenUtils.getScreenWidth(this);
    int height = ScreenUtils.getScreenHeight(this);
    if (width > height) {
      Logger.e(TAG, "width > height");
      this.finish();
      return;
    }
    super.onCreate(savedInstanceState);

    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    LayoutInflater inflater = this.getLayoutInflater();
    rl_main = inflater.inflate(this.getResources().getLayout(ResourceIdUtils.getInstance().getResourceId("R.layout.exit_layout")), null);
    RelativeLayout exit_lay = (RelativeLayout) rl_main.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.exit_lay"));
    exit_lay.setBackgroundDrawable(this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_banner_bg")));
    ImageView close = (ImageView) rl_main.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.exit_close"));
    close.setBackgroundDrawable(this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.exit_cancle")));
    close.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
//    	  closeDspAdList();
        ExitActivity.this.finish();
      }
    });
    LinearLayout list_title = (LinearLayout) rl_main.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.exit_list_title"));
    list_title.setBackgroundDrawable(this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.exit_header_bg")));
    
//    boolean isShowDspList = isShowDspList();
//    if(isShowDspList){
//    	LinearLayout ll_wrapper = (LinearLayout)rl_main.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.exit_list_wrapper"));
//    	ll_wrapper.removeAllViews();
//    	String a = DBUtils.getInstance(this).queryCfgValueByKey(CommConstants.APPID_METADATA_KEY);
//      String c = DBUtils.getInstance(this).queryCfgValueByKey(CommConstants.CHANNELID_METADATA_KEY);
//      String pi = DBUtils.getInstance(this).queryCfgValueByKey(CommConstants.CPID_METADATA_KEY);
//      SharedPreferences spf = this.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
//      String sspid = spf.getString(CommConstants.EXIT_SSPID, "");
//    	mAppWall = new AppWall(this, AdInType.APP_WALL, sspid, pi+"@"+a,pi+"@"+c);
//    	ll_wrapper.addView(mAppWall);
//    	mAppWall.setAppWallData(mAdDownInfos);
//    	setContentView(rl_main);
//        StatsPromUtils.getInstance(this).addDisplayAction("exit_1/",StatsPromConstants.STATS_PROM_AD_INFO_POSITION_EXIT);
//    }else
//    {
    	ListView listView = (ListView) rl_main.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.exit_list"));
        adapter = new FloatWindowListAdaptr(inflater, mFolderIconList);;
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        new Thread(new SearchRunnable()).start();
        setContentView(rl_main);
        StatsPromUtils.getInstance(this).addDisplayAction("exit/",StatsPromConstants.STATS_PROM_AD_INFO_POSITION_EXIT);
//    }
    
    
    
  }
  
//  private boolean isShowDspList(){
//	  mAdDownInfos = HandleExitService.getDspAppWallData();
//	  if(null == mAdDownInfos){
//		  return false;
//	  }
//	  return true;
//  }
  
//  private void closeDspAdList(){
//	  if(null == mAppWall){
//		 return; 
//	  }
//	  mAppWall.closeAppWall();
//  }
  
  private class SearchRunnable implements Runnable{

    @Override
    public void run() {
      mFolderIconList = PromDBU.getInstance(ExitActivity.this).queryAdInfo(PromDBU.PROM_EXIT);
      if (mFolderIconList.size() <= 0) {
       Logger.e(TAG, "lis size = 0");
        ExitActivity.this.finish();
      }
      Message m = new Message();
      mHandler.sendMessage(m);
    }
    
  };

  private class ViewHolder {
    ImageView icon,tvNumber;
    TextView tvTitle, tvSize,tvStatus;
    Button tvbutton;
}


  private class FloatWindowListAdaptr extends BaseAdapter {
    private List<AdDbInfo> mList;
    private LayoutInflater       mInflater;

    public void setDataList(List<AdDbInfo> mList) {
      this.mList = mList;

    }

    public FloatWindowListAdaptr(LayoutInflater mInflater, List<AdDbInfo> list) {
      super();
      this.mList = list;
      this.mInflater = mInflater;
    }

    @Override
    public int getCount() {
      return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int arg0) {
      return arg0;
    }

    @Override
    public long getItemId(int arg0) {
      return arg0;
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {
      final AdDbInfo info = mList.get(arg0);
      if(info != null && info.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_DSP){
        
        View ad_dsp = null;
        if(mViewMap.get(info.getAdId()) == null){
        	
//          ad_dsp = mInflater.inflate(ResourceIdUtils.getInstance().getResourceId("R.layout.exit_item_dsp"),null);
//          final RelativeLayout dsp = (RelativeLayout) ad_dsp.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.exit_dsp"));
//          AdView adblock =  new AdView(this.getApplicationContext());
//          mBlockMap.put(info.getAdId(), adblock);
//          String a = DBUtils.getInstance(this).queryCfgValueByKey(CommConstants.APPID_METADATA_KEY);
//          String c = DBUtils.getInstance(this).queryCfgValueByKey(CommConstants.CHANNELID_METADATA_KEY);
//          String pi = DBUtils.getInstance(this).queryCfgValueByKey(CommConstants.CPID_METADATA_KEY);
//          View ad = adblock.getAdView(info.getAdId(),pi+"@"+a,pi+"@"+c, new AdCallback() {
//            @Override
//            public void onSuccess() {
//              dsp.setVisibility(View.VISIBLE);
//            }
//            @Override
//            public void onFailed(boolean arg0, String arg1) {
//              
//            }
//          });
//          dsp.addView(ad, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//          dsp.setVisibility(View.GONE);
//          mViewMap.put(info.getAdId(), ad_dsp);
        
        }else{
          ad_dsp = mViewMap.get(info.getAdId());
        }
   
        return ad_dsp;     
      }

        ViewHolder holder = null;
        
        if(arg1 != null){
          holder = (ViewHolder)arg1.getTag();
        }
        
        if (null == arg1 || holder == null) {
            holder = new ViewHolder();
            arg1 = mInflater.inflate(ResourceIdUtils.getInstance().getResourceId("R.layout.exit_item"),null);
            holder.icon = (ImageView) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.exit_item_icon"));
            holder.tvNumber = (ImageView) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.top_tag"));
            holder.tvbutton = (Button) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.exit_button"));
            holder.tvTitle = (TextView) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.exit_title"));
            holder.tvSize = (TextView) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.exit_size"));
            holder.tvStatus = (TextView) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.status_text"));
            arg1.setTag(holder);
        } else {
            holder = (ViewHolder) arg1.getTag();
        }
        if(info != null){
        Bitmap bitmap = null;
        try {
          File f = new File(FileConstants.getFloatDir(ExitActivity.this) + "/" + info.getPicName());
          if (f.exists()) {
            bitmap = BitmapFactory.decodeFile(FileConstants.getFloatDir(ExitActivity.this) + "/" + info.getPicName());
          } 
          holder.icon.setBackgroundDrawable(new BitmapDrawable(bitmap));
        } catch (Exception e) {
          Logger.p(e);
        } catch (OutOfMemoryError e) {
          Logger.p(e);
        }
        
//        holder.tvbutton.setBackgroundDrawable(this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.exit_download_image")));
        holder.tvTitle.setText(info.getAdName());
        holder.tvSize.setText(""+PromUtils.getFileSizeStr(info.getFileSize()));
        
        ExitListHandler handler = DownloadUtils.getInstance(ExitActivity.this).getExitListHandler(info.getPackageName(), info.getVersionCode());
        if (handler != null) {
          handler.setmView(holder.tvStatus);
          handler.setmButton(holder.tvbutton);
          holder.tvbutton.setBackgroundDrawable(ExitActivity.this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.exit_download_image")));
          
        }else {
          holder.tvStatus.setText("体验");
          holder.tvbutton.setBackgroundDrawable(ExitActivity.this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.exit_download_image")));
      }
          String apkPath = DownloadUtils.getInstance(ExitActivity.this).getApkDownloadFilePath(info.getPackageName(), info.getVersionCode());
          File downloadFile = new File(apkPath);
          PackageInfo pInfo = AppInstallUtils.getPgInfoByPackageName(ExitActivity.this.getApplicationContext(), info.getPackageName());
          if(pInfo != null){
            holder.tvbutton.setBackgroundDrawable(ExitActivity.this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.exit_open")));
            holder.tvStatus.setText("打开");
          }else if(downloadFile.exists()){
            holder.tvbutton.setBackgroundDrawable(ExitActivity.this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.exit_finish")));
            holder.tvStatus.setText("安装");
          }
        
        if (arg0 == 0){
            holder.tvNumber.setBackgroundDrawable(ExitActivity.this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.exit_cover_one")));;
        }else if (arg0 == 1){
            holder.tvNumber.setBackgroundDrawable(ExitActivity.this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.exit_cover_two")));;
        }else if (arg0 == 2){
            holder.tvNumber.setBackgroundDrawable(ExitActivity.this.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.exit_cover_three")));;
        }else{
          holder.tvNumber.setBackgroundDrawable(null);
        }
        }

        return arg1;
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    AdDbInfo info = mFolderIconList.get(position);
    if (null == info) {
      return;
    } else {
      handlerApp(info, view);
    }
  }

  public void handlerApp(AdDbInfo info, View arg1) {
    StatsPromUtils.getInstance(this).addClickAction(info.getAdId()+"/"+info.getPackageName(),  StatsPromConstants.STATS_PROM_AD_INFO_POSITION_EXIT);
    int status = DownloadUtils.getInstance(this).getAppStatus(info.getPackageName(), info.getVersionCode());
    PackageInfo pInfo = AppInstallUtils.getPgInfoByPackageName(this.getApplicationContext(), info.getPackageName());
    TextView dtext = (TextView) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.status_text"));
    Button button = (Button) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.exit_button"));
    if (pInfo != null && pInfo.versionCode >= info.getVersionCode()) {
      AppInstallUtils.launchOtherActivity(this, info.getPackageName(), null);
      return;
    }
    if (status != CommConstants.APP_STATUS_INSTALLED) {
      if (status == CommConstants.APP_STATUS_DOWNLOAD_WAITING || status == CommConstants.APP_STATUS_DOWNLOADING) {
      } else {
        String apkPath = DownloadUtils.getInstance(this).getApkDownloadFilePath(info.getPackageName(), info.getVersionCode());
        File downloadFile = new File(apkPath);
        ExitListHandler  handler = DownloadUtils.getInstance(this).getExitListHandler(info.getPackageName(), info.getVersionCode());
        if (handler == null) {
          handler = new ExitListHandler(this, apkPath, info.getAdId(),info.getPackageName(), info.getVersionCode(), 0,StatsPromConstants.STATS_PROM_AD_INFO_POSITION_EXIT,0);
          DownloadUtils.getInstance(this).addExitListHandler(handler, info.getPackageName(), info.getVersionCode());
        }
        handler.setmView(dtext);
        handler.setmButton(button);
        if (!downloadFile.exists()) {
          checkNetworkInfo();
          apkPath = DownloadUtils.getInstance(this).getApkDownloadFilePath(info.getPackageName(), info.getVersionCode());
          if (!DownloadUtils.getInstance(this).getDownloadApkThreadMap().containsKey(new MyPackageInfo(info.getPackageName(), info.getVersionCode()))) {
            dtext.setText("下载中");
            DownloadUtils.getInstance(this).addDownloadApkThread(
                new DownloadInfo(handler, info.getAdId(),info.getPackageName(), info.getVersionCode(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_EXIT,
                    0, info.getAdDownUrl(), info.getFileMd5(), true,false));
          }
        } else {
          AppInstallUtils.installApp(this, apkPath, new MyPackageInfo(info.getAdId(),info.getPackageName(), info.getVersionCode(),
              StatsPromConstants.STATS_PROM_AD_INFO_POSITION_EXIT, 0));
        }
      }
    }
  }

  public void checkNetworkInfo() {
    ConnectivityManager conMan = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
    State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
    if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
      Toast.makeText(this.getApplicationContext(), ResourceIdUtils.getInstance().getStringByResId("R.string.mf_folder_mobile_hint"), Toast.LENGTH_LONG).show();
    } else if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
    } else {
      Toast.makeText(this.getApplicationContext(), ResourceIdUtils.getInstance().getStringByResId("R.string.mf_folder_no_network_hint"), Toast.LENGTH_LONG)
          .show();
    }
  }
  
  @Override
  protected void onRestart() {
    super.onRestart();
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mHandler.sendEmptyMessage(1);
  }
  

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
  }
  
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    mFolderIconList.clear();
    mFolderIconList = null;
//    if (mBlockMap != null) {
//      Logger.e(TAG, "mAdBlockMap = "+mBlockMap.toString());
//      Iterator iter = mBlockMap.entrySet().iterator();
//      while (iter.hasNext()) {
//        Map.Entry entry = (Map.Entry) iter.next();
//        AdView val = (AdView) entry.getValue();
//        val.closeAd();
//      }
//    }
    mViewMap.clear();
//    mBlockMap.clear();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      this.finish();
      return false;
    }
    return false;
  }
  

}