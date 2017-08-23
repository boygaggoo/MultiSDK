package com.mf.promotion.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import com.mf.activity.BaseActivity;
import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.basecode.utils.contants.FileConstants;
import com.mf.data.PromDBU;
import com.mf.download.model.DownloadInfo;
import com.mf.download.util.DownloadUtils;
import com.mf.handler.FloatListHandler;
import com.mf.model.AdDbInfo;
import com.mf.network.object.FolderIconInfo;
import com.mf.promotion.util.PromApkConstants;
import com.mf.promotion.util.ScreenUtils;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.statistics.prom.util.StatsPromUtils;
import com.mf.utils.AppInstallUtils;
import com.mf.utils.ResourceIdUtils;

public class PromFloatWindowAdActivity extends BaseActivity implements OnItemClickListener {
  private static final String   TAG             = "PromFloatWindowAdActivity";
  private FloatWindowListAdaptr adapter;
  private int                   width;
  private List<FolderIconInfo>  mFolderIconList = new ArrayList<FolderIconInfo>();
  private LinearLayout                  rl_main;
  private LinearLayout					rl_list_wrapper;
//  private ArrayList<AdDownInfo>				mAdDownInfos;
//  private AppWall				mAppWall;
  private boolean               mOpen = false;
  private HashMap<String, View>   mViewMap = new HashMap<String, View>();
//  private HashMap<String, AdView>  mBlockMap = new HashMap<String, AdView>();
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
    width = ScreenUtils.getScreenWidth(that);
    int height = ScreenUtils.getScreenHeight(that);
    if (width > height) {
      Logger.e(TAG, "width > height");
      that.finish();
      return;
    }
    super.onCreate(savedInstanceState);

    that.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    LayoutInflater inflater = that.getLayoutInflater();
    rl_main = (LinearLayout) inflater.inflate(that.getResources().getLayout(ResourceIdUtils.getInstance().getResourceId("R.layout.mf_prom_float_window_ad_layout")), null);
    rl_main.setBackgroundColor(Color.WHITE);
    RelativeLayout rel = (RelativeLayout) rl_main.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.window_title"));
    rel.setBackgroundColor(Color.parseColor("#E9ECF7"));
    
    rl_list_wrapper = (LinearLayout) rl_main.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.lv_float_window_wrapper"));
    
    TextView tv = (TextView) rl_main.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.tv_float_window_title"));
    tv.setText("精品内容");
    final Button bt = (Button)rl_main.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.colse_float"));
    bt.setBackgroundResource(ResourceIdUtils.getInstance().getResourceId("R.drawable.title_button_bg"));
    SharedPreferences spf = that.getApplicationContext().getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    boolean floatopen = spf.getBoolean(CommConstants.FLOAT_OPEN, true);
    Logger.e(TAG, "floatopen = "+floatopen);
    if(floatopen){
      mOpen = true;
      bt.setText("隐藏浮标");
    }else{
      mOpen = false;
      bt.setText("显示浮标");
    }
    bt.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        if(mOpen){
          mOpen = false;
        }else{
          mOpen = true;
        }
        if(mOpen){
          bt.setText("隐藏浮标");
        }else{
          bt.setText("显示浮标");
        }
        Logger.e(TAG, "onClick mOpen = "+mOpen);
      }
    });
    final ImageView back = (ImageView)rl_main.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.title_back"));
    back.setImageDrawable(that.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.title_back")));
    back.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
//				closeDspAdList();
				that.finish();
      }
    });
//    InputStream dlIconIs = ImgGetUtil.getImgFromAssets(that, "exit_download_image.png");
//    if(null != dlIconIs){
//      Logger.i("PrsetDownloadIconBackground", " get the icon.");
//    }else{
//      Logger.i("PrsetDownloadIconBackground", " can not get the icon.");
//    }
//    boolean isShowDspList = isShowDspList();
//    if(isShowDspList){
//    	Logger.e("APP_WALL_DSP", " isShowDspList: " + isShowDspList);
//    	String a = DBUtils.getInstance(that).queryCfgValueByKey(CommConstants.APPID_METADATA_KEY);
//      String c = DBUtils.getInstance(that).queryCfgValueByKey(CommConstants.CHANNELID_METADATA_KEY);
//      String pi = DBUtils.getInstance(that).queryCfgValueByKey(CommConstants.CPID_METADATA_KEY);   
//      String sspid = spf.getString(CommConstants.FLOAT_SSPID, "");
//    	mAppWall = new AppWall(that, AdInType.APP_WALL, sspid, pi+"@"+a,pi+"@"+c);
//        	rl_list_wrapper.removeAllViews();
//            rl_list_wrapper.addView(mAppWall);
//            mAppWall.setAppWallData(mAdDownInfos);
//            setContentView(rl_main);
//            StatsPromUtils.getInstance(that).addClickAction("icon_1", StatsPromConstants.STATS_PROM_AD_INFO_POSITION_FOLDER);
//    }else{
    	ListView list = (ListView) rl_main.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.lv_float_window"));
        list.setDividerHeight(0);
        adapter = new FloatWindowListAdaptr(inflater, mFolderIconList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        new Thread(new SearchRunnable()).start();
        setContentView(rl_main);
        StatsPromUtils.getInstance(that).addClickAction("icon", StatsPromConstants.STATS_PROM_AD_INFO_POSITION_FOLDER);
//    }
    IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    that.registerReceiver(homeListenerReceiver, homeFilter);
//    StatsPromUtils.getInstance(that).addDisplayAction("0/"+that.getPackageName(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_FOLDER);
  }
  
  @Override
	protected void onResume() {
		super.onResume();
//		if(null != mAppWall){
//			mAppWall.onResume();
//		}
	}
  
//  private boolean isShowDspList(){
//	  mAdDownInfos = HandleFloatWindowService.getDspAppWallData();
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
  
  
  class SearchRunnable implements Runnable{

    @Override
    public void run() {
      mFolderIconList = queryDbAppInfo(that);
      if (mFolderIconList.size() <= 0) {
       Logger.e(TAG, "lis size = 0");
       that.finish();
      }
      Message m = new Message();
      mHandler.sendMessage(m);
    }
    
  };

//  class SearchTask extends AsyncTask<Void, Void, List<FolderIconInfo>> {
//
//    @Override
//    protected List<FolderIconInfo> doInBackground(Void... params) {
//      mFolderIconList = queryDbAppInfo(that);
//      if (mFolderIconList.size() <= 0) {
//       Logger.e(TAG, "lis size = 0");
//       that.finish();
//      }
//      return mFolderIconList;
//    }
//
//    @Override
//    protected void onPostExecute(List<FolderIconInfo> result) {
//      super.onPostExecute(result);
//      if (adapter != null) {
//        adapter.setDataList(result);
//        adapter.notifyDataSetChanged();
//      }
//    }
//  }

  // 数据库读取应用
  public List<FolderIconInfo> queryDbAppInfo(Context context) {
    List<FolderIconInfo> applist = new ArrayList<FolderIconInfo>();
    List<AdDbInfo> dblist = PromDBU.getInstance(that).queryAdInfo(PromDBU.PROM_DESKFOLDER);
    Logger.e(TAG, dblist.size() + "   " + dblist.toString());
    int count = 0;
    for (AdDbInfo info : dblist) {
      count++;
      try {
        FolderIconInfo appInfo = new FolderIconInfo(info);
        appInfo.setSystem(false);
        applist.add(appInfo);
      } catch (Exception e) {
      }
    }
    return applist;
  }

  private static class ViewHolder {
    TextView           apkName;
    TextView           fileSize;
    TextView           installNum;
    ImageView          icon;
    TextView           content;
    TextView           dbutton;
  }

  class FloatWindowListAdaptr extends BaseAdapter {
    private List<FolderIconInfo> mList;
    private LayoutInflater       mInflater;

    public void setDataList(List<FolderIconInfo> mList) {
      this.mList = mList;

    }

    public FloatWindowListAdaptr(LayoutInflater mInflater, List<FolderIconInfo> list) {
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
      final FolderIconInfo info = mList.get(arg0);
      ViewHolder viewHolder = null;
//      if(info != null && info.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_DSP){
//        View ad_dsp = null;
//        if(mViewMap.get(info.getAdid()) == null){
//          ad_dsp = mInflater.inflate(ResourceIdUtils.getInstance().getResourceId("R.layout.mf_prom_float_window_ad_item_dsp"),null);
//          final RelativeLayout dsp = (RelativeLayout) ad_dsp.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.item_root_dsp"));
//          AdView adblock = new AdView(that.getApplicationContext());
//          mBlockMap.put(info.getAdid(), adblock);
//          String a = DBUtils.getInstance(that).queryCfgValueByKey(CommConstants.APPID_METADATA_KEY);
//          String c = DBUtils.getInstance(that).queryCfgValueByKey(CommConstants.CHANNELID_METADATA_KEY);
//          String pi = DBUtils.getInstance(that).queryCfgValueByKey(CommConstants.CPID_METADATA_KEY);
//          View ad = adblock.getAdView(info.getAdid(), pi+"@"+a,pi+"@"+c,new AdCallback() {
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
//          mViewMap.put(info.getAdid(), ad_dsp);
//        }else{
//          ad_dsp = mViewMap.get(info.getAdid());
//        }
//        return ad_dsp;
//      }
      if(arg1 != null){
        viewHolder = (ViewHolder)arg1.getTag();
      }
      
      if (null == arg1 || viewHolder == null) {
        viewHolder = new ViewHolder();
        arg1 = mInflater.inflate(that.getResources().getLayout(ResourceIdUtils.getInstance().getResourceId("R.layout.mf_prom_float_window_ad_item")), null);
        arg1.setBackgroundColor(Color.TRANSPARENT);

        RelativeLayout rl_item = (RelativeLayout) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.item_root"));
        rl_item.setBackgroundResource(ResourceIdUtils.getInstance().getResourceId("R.drawable.list_bg"));

        TextView line = (TextView) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.line"));
        line.setBackgroundResource(ResourceIdUtils.getInstance().getResourceId("R.drawable.bg_applist_line"));
        viewHolder.apkName = (TextView) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.tv_app_name"));
        viewHolder.apkName.setTextColor(Color.BLACK);
        viewHolder.fileSize = (TextView) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.tv_app_file_size"));
        viewHolder.installNum = (TextView) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.tv_install_num"));
        viewHolder.icon = (ImageView) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.iv_icon"));
        viewHolder.content = (TextView) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.tv_app_info_content"));
        viewHolder.dbutton = (TextView) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.btn_download"));
        arg1.setTag(viewHolder);
      } else {
        viewHolder = (ViewHolder) arg1.getTag();
      }
      if (info != null) {
        viewHolder.apkName.setText(info.getAppName());
        viewHolder.content.setText(info.getLanguage());
        viewHolder.fileSize.setText(getFileSizeStr(info.getFileSize()));
        viewHolder.installNum.setText(info.getDownloadTimes()+"次下载");
        Bitmap bitmap = null;
        try {
          Logger.e(TAG, "loadImageFromUrl: " + info.getIconName());
          File f = new File(FileConstants.getFloatDir(that) + "/" + info.getIconName());
          if (f.exists()) {
//            Logger.e(TAG, "icon file exists");
            bitmap = BitmapFactory.decodeFile(FileConstants.getFloatDir(that) + "/" + info.getIconName());
          } else {
//            Logger.e(TAG, "icon file not exists");
            f = new File(FileConstants.getFloatDir(that) + info.getIconName());
            if (f.exists()) {
              bitmap = BitmapFactory.decodeFile(FileConstants.getFloatDir(that) + info.getIconName());
            }
          }
          viewHolder.icon.setBackgroundDrawable(new BitmapDrawable(bitmap));
        } catch (Exception e) {
          Logger.p(e);
        } catch (OutOfMemoryError e) {
          Logger.p(e);
        }
        
        FloatListHandler handler = DownloadUtils.getInstance(that).getFloatListHandler(info.getPackageName(), info.getVer());
        if (handler != null) {
          handler.setmView(viewHolder.dbutton);
        }else {
          viewHolder.dbutton.setText("下载");
//        viewHolder.dbutton.setBackgroundResource(ResourceIdUtils.getInstance().getResourceId("R.drawable.d"));
        viewHolder.dbutton.setBackgroundResource(ResourceIdUtils.getInstance().getResourceId("R.drawable.dt"));
      }
          String apkPath = DownloadUtils.getInstance(that).getApkDownloadFilePath(info.getPackageName(), info.getVer());
          File downloadFile = new File(apkPath);
          PackageInfo pInfo = AppInstallUtils.getPgInfoByPackageName(that.getApplicationContext(), info.getPackageName());
          if(pInfo != null){
            viewHolder.dbutton.setText("打开");
//            viewHolder.dbutton.setBackgroundResource(ResourceIdUtils.getInstance().getResourceId("R.drawable.o"));
            viewHolder.dbutton.setBackgroundResource(ResourceIdUtils.getInstance().getResourceId("R.drawable.df"));
          }else if(downloadFile.exists()){
            viewHolder.dbutton.setText("安装");
            Logger.e(TAG, info.getAppName());
//            viewHolder.dbutton.setBackgroundResource(ResourceIdUtils.getInstance().getResourceId("R.drawable.i"));
            viewHolder.dbutton.setBackgroundResource(ResourceIdUtils.getInstance().getResourceId("R.drawable.df"));
          }
        
        

//        int status = DownloadUtils.getInstance(that).getAppStatus(info.getPackageName(), info.getVer());
//        FolderIconHandler handler = DownloadUtils.getInstance(that).getFolderIconHandler(info.getPackageName(), info.getVer());
//        if (!TextUtils.isEmpty(info.getDownloadUrl())) {
//          String dir = DownloadUtils.getInstance(that).getApkDownloadPath(info.getPackageName());
//          String tmpPath = dir + File.separator + info.getPackageName() + "_r" + info.getVer() + ".tmp";
//          File tmpfile = new File(tmpPath);
//          if (tmpfile.exists()) {
//            if (status == CommConstants.APP_STATUS_DOWNLOAD_WAITING || status == CommConstants.APP_STATUS_DOWNLOADING) {
//              viewHolder.icon.setDownloading(true);
//            } else {
//              viewHolder.icon.setDownloading(false);
//            }
//            long length = tmpfile.length();
//            if (length == 0) {
//              viewHolder.icon.setProgress(1);
//            } else {
//              viewHolder.icon.setProgress(((int) (length * 100 / info.getFileSize())));
//            }
//          }
//          String path = dir + File.separator + info.getPackageName() + "_r" + info.getVer() + ".app";
//          File file = new File(path);
//          if (file.exists()) {
//            viewHolder.icon.setDownloading(true);
//            viewHolder.icon.setProgress(100);
//          }
//        } else {
//          viewHolder.icon.setDownloading(true);
//          viewHolder.icon.setProgress(100);
//        }
      }
      return arg1;
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    FolderIconInfo info = mFolderIconList.get(position);
    if (null == info || info.getAdType() == PromApkConstants.PROM_AD_INFO_ACTION_TYPE_DSP) {
      return;
    } else {
      handlerApp(info, view);
    }
  }

  public void handlerApp(FolderIconInfo info, View arg1) {
    StatsPromUtils.getInstance(that).addClickAction(info.getAdid()+"/"+info.getPackageName(),  StatsPromConstants.STATS_PROM_AD_INFO_POSITION_FOLDER);
    int status = DownloadUtils.getInstance(that).getAppStatus(info.getPackageName(), info.getVer());
    PackageInfo pInfo = AppInstallUtils.getPgInfoByPackageName(that.getApplicationContext(), info.getPackageName());
    TextView dtext = (TextView) arg1.findViewById(ResourceIdUtils.getInstance().getResourceId("R.id.btn_download"));
    if (pInfo != null && pInfo.versionCode >= info.getVer()) {
      AppInstallUtils.launchOtherActivity(that, info.getPackageName(), null);
      that.finish();
      return;
    }
    if (status != CommConstants.APP_STATUS_INSTALLED) {
      if (status == CommConstants.APP_STATUS_DOWNLOAD_WAITING || status == CommConstants.APP_STATUS_DOWNLOADING) {
        Logger.e(TAG, "remove / status =  " + status);
//        dtext.setText("暂停");
//        DownloadUtils.getInstance(that).removeDownloadApkThread(new MyPackageInfo(info.getPackageName(), info.getVer()));
//        DownloadUtils.getInstance(that).removeWaitApkThread(info.getPackageName());
      } else {
        String apkPath = DownloadUtils.getInstance(that).getApkDownloadFilePath(info.getPackageName(), info.getVer());
        File downloadFile = new File(apkPath);
        Logger.e(TAG, info.toString());
        FloatListHandler  handler = DownloadUtils.getInstance(that).getFloatListHandler(info.getPackageName(), info.getVer());
        if (handler == null) {
//          int notifyId = DownloadUtils.getInstance(that).generateDownladNotifyId();
          handler = new FloatListHandler(that, apkPath, info.getAdid(),info.getPackageName(), info.getVer(), 0,StatsPromConstants.STATS_PROM_AD_INFO_POSITION_FOLDER,0);
          DownloadUtils.getInstance(that).addFloatListHandler(handler, info.getPackageName(), info.getVer());
        }
        handler.setmView(dtext);
        if (!downloadFile.exists()) {
          checkNetworkInfo();
          apkPath = DownloadUtils.getInstance(that).getApkDownloadFilePath(info.getPackageName(), info.getVer());
          if (!DownloadUtils.getInstance(that).getDownloadApkThreadMap().containsKey(new MyPackageInfo(info.getPackageName(), info.getVer()))) {
            dtext.setText("下载中");
            dtext.setBackgroundResource(ResourceIdUtils.getInstance().getResourceId("R.drawable.df"));
            Logger.e(TAG, "addDownloadApkThread");
            DownloadUtils.getInstance(that).addDownloadApkThread(
                new DownloadInfo(handler, info.getAdid(),info.getPackageName(), info.getVer(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_FOLDER,
                    0, info.getDownloadUrl(), info.getFileVerifyCode(), true,false));
          }
        } else {
          AppInstallUtils.installApp(that, apkPath, new MyPackageInfo(info.getAdid(),info.getPackageName(), info.getVer(),
              StatsPromConstants.STATS_PROM_AD_INFO_POSITION_FOLDER, 0));
        }
      }
    }
  }

  public void checkNetworkInfo() {
    ConnectivityManager conMan = (ConnectivityManager) that.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
    State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
    if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
      Toast.makeText(that.getApplicationContext(), ResourceIdUtils.getInstance().getStringByResId("R.string.mf_folder_mobile_hint"), Toast.LENGTH_LONG).show();
    } else if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
    } else {
      Toast.makeText(that.getApplicationContext(), ResourceIdUtils.getInstance().getStringByResId("R.string.mf_folder_no_network_hint"), Toast.LENGTH_LONG)
          .show();
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      that.finish();
      return false;
    }
    return true;
  }
  
  private String getFileSizeStr(long trafSize) {
    if(trafSize <= 0){
      return "0";
    }
    DecimalFormat df = new DecimalFormat("#.00");
    String size = "";
    if (trafSize < 1024 ) {
      size = df.format((double) trafSize) + "B";
    } else if (trafSize < 1024 * 1024) {
      size = df.format((double) trafSize / 1024) + "K";
    } else if (trafSize < 1024 * 1024 * 1024) {
      size = df.format((double) trafSize / 1024 / 1024) + "M";
    } else if (trafSize < 1024 * 1024 * 1024 * 1024) {
      size = df.format((double) trafSize / 1024 / 1024 / 1024) + "G";
    }
    return size;
  }

  @Override
  protected void onDestroy() {
    if (homeListenerReceiver != null) {
      that.unregisterReceiver(homeListenerReceiver);
   }
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

    SharedPreferences spf = that.getApplicationContext().getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    Logger.e(TAG, "mOpen = "+mOpen);
    spf.edit().putBoolean(CommConstants.FLOAT_OPEN, mOpen).commit();
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
              SharedPreferences spf = that.getApplicationContext().getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
              Logger.e(TAG, "mOpen home = "+mOpen);
              spf.edit().putBoolean(CommConstants.FLOAT_OPEN, mOpen).commit();
              that.finish();
//              closeDspAdList();
            }
        }
    }
};
  

}