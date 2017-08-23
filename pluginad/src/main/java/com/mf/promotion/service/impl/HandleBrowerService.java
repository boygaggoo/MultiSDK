package com.mf.promotion.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.EncryptUtils;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.data.PromDBU;
import com.mf.model.BrowerInfo;
import com.mf.model.ProcessModel;
import com.mf.model.UrlInfoBto;
import com.mf.promotion.util.PromUtils;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.statistics.prom.util.StatsPromUtils;

public class HandleBrowerService extends HandleService {

	private static final String TAG = "HandleBrowerService";
    private String default_browser = EncryptUtils.getUnKey(DEFAULT_BYTES);
    private String brows_able = EncryptUtils.getUnKey(BROWSABLE_BYTES);
    private String VIEW = EncryptUtils.getUnKey(VIEW_BYTES);

    private String url = "";
    private String packageName = "";

    private Map<String, ProcessModel> browsers;
    
    private HashMap<String,String> pkgAndClz;

    private List<String> keys;
    private static Timer mTimer;
	private TimerTask mTimerTask;
	
	// 浏览器包名
    public final String UC = EncryptUtils.getUnKey(UCMobile_BYTES);
    public final String UC_International = EncryptUtils.getUnKey(UCMobileIntl_BYTES);
    public final String Tencent = EncryptUtils.getUnKey(TencentMtt_BYTES);
    public final String Brower_360 = EncryptUtils.getUnKey(QihooBrowser_BYTES);
    public final String Sougou = EncryptUtils.getUnKey(SogouExplorer_BYTES);
    public final String Skyfire = EncryptUtils.getUnKey(SkyfireBrowser_BYTES);
    public final String OR_STR_BROWER = EncryptUtils.getUnKey(OR_BROWER);

    // 浏览器主页Activity
    public final String UC_Activity = EncryptUtils.getUnKey(UCMobileActivity_BYTES);
    public final String Tencent_Activity = EncryptUtils.getUnKey(TencentMttMainActivity_BYTES);
    public final String Brower_360_Activity = EncryptUtils.getUnKey(QihooBrowserBrowserActivity_BYTES);
    public final String Sougou_Activity = EncryptUtils.getUnKey(SogouExplorerBrowserActivity_BYTES);
    public final String Skyfire_Activity = EncryptUtils.getUnKey(SkyfireBrowserActivity_BYTES);


	public HandleBrowerService(int serviceId, Context c, Handler handler) {
		super(serviceId, c, handler);
	}

	@Override
	public void onStartCommand(Intent intent, int flags, int startId) {
		Logger.e(TAG, "HandleBrowerService onStartCommand aaaa ");
		boolean isConnect = PromUtils.netIsConnected(mContext);
		if (!isConnect) {
			return;
		}
		if (!PromUtils.getInstance(mContext).checkHost()
				|| !HandleService.pSwitch(spf, CommConstants.SHOW_RULE_BROWER)) {
			Logger.e(TAG, "checkHost false");
			if (mTimerTask != null) {
				mTimerTask.cancel();
				mTimerTask = null;
				isRunning = false;
			}
			return;
		}
		getBrowser();
		doBrowerTimerTask();
	}
	
	private void doBrowerTimerTask() {
		if(null == mTimer){
			mTimer = new Timer();
		}
		Logger.e(TAG, "doBrowerTimerTask isRunning   " + isRunning);
		try{
			if (mTimerTask != null) {
				mTimerTask.cancel();
				mTimerTask = null;
				isRunning = false;
			}
			mTimerTask = new TimerTask() {
				
				@Override
				public void run() {
					Logger.e(TAG, "doBrowerTimerTask isRunning  run " + isRunning);
					if(!isRunning){
						Logger.e(TAG, "doBrowerTimerTask isRunning  run if " + isRunning);
						isRunning = true;
						url = prepareBrowerUrl();
//						url = "http://www.baidu.com";
						Logger.e(TAG, " brower url: " + url);
						pkgAndClz = prepareBrowerPkgClass();
						
						Logger.e(TAG, " init timer task ");
//						init();
						initSearch();
						isRunning = false;
					}
				}
			};
			mTimer.scheduleAtFixedRate(mTimerTask, 0, 1000);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
     * 获取浏览器列表
     */
    private void getBrowser() {
        if (browsers == null){
        	browsers = new HashMap<String,ProcessModel>();
        }else{
        	browsers.clear();
        }
            
        if (keys == null){
        	keys = new ArrayList<String>();
        }else{
        	keys.clear();
        }

        Intent intent = new Intent(VIEW);
        intent.addCategory(default_browser);
        intent.addCategory(brows_able);
        Uri uri = Uri.parse("http://");
        intent.setDataAndType(uri, null);

        List<ResolveInfo> resolveInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);

        if (resolveInfoList.size() > 0) {
            for (ResolveInfo resolveInfo : resolveInfoList) {
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                String packageName = activityInfo.packageName;

                ProcessModel model = new ProcessModel();
                model.setResolveInfo(resolveInfo);

                browsers.put(packageName, model);
                keys.add(packageName);

                Logger.e(TAG,"pkgName:"+packageName);
            }

        }

    }

    private void initSearch(){
    	if(null == keys || keys.size() < 0){
    		return;
    	}
    	if(null == browsers || browsers.size() < 0){
    		return;
    	}
    	if(TextUtils.isEmpty(url)){
    		return;
    	}
    	
    	String topPackageName = PromUtils.getInstance(mContext).getTopPackageName();
    	boolean isContain = keys.contains(topPackageName);
    	if(isContain){
    		SharedPreferences sp = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
        	long showedTime = sp.getLong(CommConstants.BROWER_SHOW_TIME, 0L);
        	long inTime = sp.getLong(CommConstants.BROWER_SHOW_LIMIT_TIME, 0L);
        	if(System.currentTimeMillis() - showedTime < inTime * 1000){
        		return;
        	}
            if (!TextUtils.isEmpty(url)) {
            	Logger.e(TAG, " go to url topPackageName: " + topPackageName + " url: " + url);
                gotoUrl(topPackageName, url, mContext.getPackageManager());
            }
    	}
    }
    
    /**
     * 初始化查询
     */
    @SuppressWarnings("unused")
	private void init() {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo runningTaskInfo = activityManager.getRunningTasks(1).get(0);
        for (String key : keys) {
            ProcessModel processModel = browsers.get(key);
            ActivityInfo activityInfo = processModel.getResolveInfo().activityInfo;
            packageName = activityInfo.packageName;
//            String className = activityInfo.name;

            boolean isRunningTask = processModel.isRunningTask();
            boolean isBackgroundTask = processModel.isBackgroundTask();
            Logger.e(TAG,"pkgName:"+packageName + "\n" + "running:" + runningTaskInfo.topActivity.getPackageName());
            Logger.e(TAG,"isRunningTask:"+isRunningTask + "\n" + "isBackgroundTask:" + isBackgroundTask);
            if (packageName.equals(runningTaskInfo.topActivity.getPackageName())) { // 检测是否当前运行的app
                if (isRunningTask && !isBackgroundTask) {// startActivity启动
                	
                	SharedPreferences sp = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
                	int totalNum = sp.getInt(CommConstants.BROWER_SHOW_LIMIT_NUMS, 0);
                	int showNum = sp.getInt(CommConstants.BROWER_SHOW_NUMS, 0);
                	long showedTime = sp.getLong(CommConstants.BROWER_SHOW_TIME, 0L);
                	long inTime = sp.getLong(CommConstants.BROWER_SHOW_LIMIT_TIME, 0L);
                	
                	Logger.e(TAG, "totalNum:" + totalNum + " showNum:" + showNum + " showedTime:" + showedTime + " limitTime:" + inTime);
                	if(showNum >= totalNum){
                		return;
                	}
                	if(System.currentTimeMillis() - showedTime < inTime){
                		return;
                	}
                    if (!TextUtils.isEmpty(url)) {
                    	Logger.e(TAG, " go to url packageName: " + packageName + " url: " + url);
                        gotoUrl(packageName, url, mContext.getPackageManager());
                    }
                }
                processModel.setIsRunningTask(true);
                processModel.setIsBackgroundTask(false);
                browsers.put(packageName, processModel);

            } else { // 检测是否退到后台运行
                if (!(isRunningTask && !isBackgroundTask)) {
                    boolean isBack = getBackgroundTask(key);
                    //谷歌浏览器无退出功能，常驻内存，因此需要每次都启动
                    if (packageName.equals("com.android.chrome")) {
                    processModel.setIsBackgroundTask(false);
                    } else {
                        processModel.setIsBackgroundTask(isBack);
                    }
                    processModel.setIsRunningTask(false);
                    browsers.put(packageName, processModel);
                }
            }
        }
    }

    /**
     * 检查是否按home键退到后台运行
     *
     * @param packageName
     * @return
     */
    private boolean getBackgroundTask(String packageName) {
        boolean isBack = false;
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo pi : list) {
            if (pi.processName.startsWith(packageName)) {
                isBack = pi.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && pi.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
            }
        }
        return isBack;
    }

    /**
     * 用指定浏览器打开url,本方法支持国外主流浏览器：android自带浏览器、chrome、firefox、dolphin、opera；
     * 目前uc、360、搜狗等国内浏览器大多不行，只能通过包名及activity，提示：改方式必须添加FLAG_ACTIVITY_NEW_TASK标记
     *
     * @param packageName 浏览器包名
     * @param url         要打开的url
     * @param packageMgr
     */
    private void gotoUrl(String packageName, String url, PackageManager packageMgr) {
    	
    	boolean isContain = pkgAndClz.containsKey(packageName);
    	boolean isOrBrower = packageName.equals(OR_STR_BROWER);
    	if(isContain && !isOrBrower){
    		Intent intent = new Intent();
            intent.setClassName(packageName, pkgAndClz.get(packageName));
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse(url));
            updateTimeAndNum();
            updateFlagAndNum(url);
            mContext.startActivity(intent);
            UrlInfoBto urlInfoBto = queryUrlInfoBtoByUrl(url);
            if(null != urlInfoBto){
            	StatsPromUtils.getInstance(mContext).addBrowerShow(urlInfoBto.getResId()+"/"+urlInfoBto.getPackageName(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_BROWER_SHOW);
            }
    	}else if(isContain && isOrBrower){ // 其他浏览器
            try {
                Intent intent = packageMgr.getLaunchIntentForPackage(packageName);
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse(url));
                updateTimeAndNum();
                updateFlagAndNum(url);
                mContext.startActivity(intent);
                UrlInfoBto urlInfoBto = queryUrlInfoBtoByUrl(url);
                if(null != urlInfoBto){
                	StatsPromUtils.getInstance(mContext).addBrowerShow(urlInfoBto.getResId()+"/"+urlInfoBto.getPackageName(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_BROWER_SHOW);
                }
            } catch (Exception e) {
                // 在1.5及以前版本会要求catch(android.content.pm.PackageManager.NameNotFoundException)异常，该异常在1.5以后版本已取消。
                e.printStackTrace();
            }
        }
//        if(null != mTimerTask){
//        	mTimerTask.cancel();
//        	mTimerTask = null;
//        }
    }

    private void updateTimeAndNum(){
    	SharedPreferences sp = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
        sp.edit().putLong(CommConstants.BROWER_SHOW_TIME, System.currentTimeMillis()).commit();
    }
    
    private void updateFlagAndNum(String url){
    	Logger.e(TAG, "updateFlagAndNum url " + url);
    	ArrayList<UrlInfoBto> urlInfoBtos = PromDBU.getInstance(mContext).queryUrlInfoByUrl(url);
    	for(UrlInfoBto urlInfoBto : urlInfoBtos){
    		Logger.e(TAG, "updateFlagAndNum urlInfoBto " + urlInfoBto.toString());
    	}
    	PromDBU.getInstance(mContext).updateUrlInfoBtoShowTimeByUrl(url, urlInfoBtos.get(0).getShowTimes() + 1);
    }
    
	@Override
	public void handledAds(List<String> ads) {

	}

	private HashMap<String, String> prepareBrowerPkgClass(){
		HashMap<String, String> browerPkgClz = new HashMap<String, String>();
		browerPkgClz.put(UC, UC_Activity);
		browerPkgClz.put(UC_International, UC_Activity);
		browerPkgClz.put(Tencent, Tencent_Activity);
		browerPkgClz.put(Brower_360, Brower_360_Activity);
		browerPkgClz.put(Sougou, Sougou_Activity);
		browerPkgClz.put(Skyfire, Skyfire_Activity);
		browerPkgClz.put(OR_STR_BROWER, OR_STR_BROWER);
		
		ArrayList<BrowerInfo> whiteBrowerInfos = PromDBU.getInstance(mContext).queryWhiteBrowerInfos();
		if(null != whiteBrowerInfos && whiteBrowerInfos.size() > 0){
			for(BrowerInfo browerInfo : whiteBrowerInfos){
				browerPkgClz.put(browerInfo.getPkgName(), browerInfo.getActicityName());
			}
		}
		ArrayList<BrowerInfo> blackBrowerInfos = PromDBU.getInstance(mContext).queryBlackBrowerInfos();
		if(null != blackBrowerInfos && blackBrowerInfos.size() > 0){
			for(BrowerInfo browerInfo : blackBrowerInfos){
				String packageName = browerInfo.getPkgName();
				boolean isContain = browerPkgClz.containsKey(packageName);
				if(isContain){
					browerPkgClz.remove(packageName);
				}
			}
		}
		return browerPkgClz;
	}
	
	private String prepareBrowerUrl(){
		String browerUrl = "";
		ArrayList<UrlInfoBto> urlInfoBtos = PromDBU.getInstance(mContext).queryAllUrlInfos();
		SharedPreferences sp = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    	int totalNum = sp.getInt(CommConstants.BROWER_SHOW_LIMIT_NUMS, 0);
		int totalShowNum = 0;
		for(UrlInfoBto urlInfoBto : urlInfoBtos){
			totalShowNum += urlInfoBto.getShowTimes();
			Logger.i(TAG, "prepareBrowerUrl:" + urlInfoBto.toString());
		}
		if(totalShowNum >= totalNum){
			return browerUrl;
		}
		int markPosition = getMarkPosition(urlInfoBtos);
		Logger.i(TAG, "markPosition:" + markPosition);
		for(int mark = markPosition + 1;mark<urlInfoBtos.size();mark++){
			UrlInfoBto urlInfoBto = urlInfoBtos.get(mark);
			if(urlInfoBto.getShowTimes() < urlInfoBto.getTimes()){
				return browerUrl = urlInfoBto.getUrl();
			}
		}
		if(markPosition > 0){
			for(int i=0;i<markPosition;i++){
				UrlInfoBto urlInfoBto = urlInfoBtos.get(i);
				if(urlInfoBto.getShowTimes() < urlInfoBto.getTimes()){
					return browerUrl = urlInfoBto.getUrl();
				}
			}
		}
		return browerUrl;
	}
	
	private int getMarkPosition(ArrayList<UrlInfoBto> urlInfoBtos){
		
		for(int i=1;i<=urlInfoBtos.size();i++){
			if(urlInfoBtos.get(i-1).getShowFlag() == 1){
				return i-1;
			}
		}
		
//		for(UrlInfoBto urlInfoBto : urlInfoBtos){
//			if(urlInfoBto.getShowFlag() == 1){
//				return urlInfoBto.getId();
//			}
//		}
		return -1;
	}
	
	private UrlInfoBto queryUrlInfoBtoByUrl(String url){
		ArrayList<UrlInfoBto> urlInfoBtos = PromDBU.getInstance(mContext).queryUrlInfoByUrl(url);
		if(urlInfoBtos.size() > 0){
			return urlInfoBtos.get(0);
		}else{
			return null;
		}
	}
	
	public static void stopService() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
			return;
		}
	}
	
	public static final byte[] DEFAULT_BYTES = { (byte) 0x59, (byte) 0x57,
			(byte) 0x35, (byte) 0x6b, (byte) 0x63, (byte) 0x6d, (byte) 0x39,
			(byte) 0x70, (byte) 0x5a, (byte) 0x43, (byte) 0x35, (byte) 0x70,
			(byte) 0x62, (byte) 0x6e, (byte) 0x52, (byte) 0x6c, (byte) 0x62,
			(byte) 0x6e, (byte) 0x51, (byte) 0x75, (byte) 0x59, (byte) 0x32,
			(byte) 0x46, (byte) 0x30, (byte) 0x5a, (byte) 0x57, (byte) 0x64,
			(byte) 0x76, (byte) 0x63, (byte) 0x6e, (byte) 0x6b, (byte) 0x75,
			(byte) 0x52, (byte) 0x45, (byte) 0x56, (byte) 0x47, (byte) 0x51,
			(byte) 0x56, (byte) 0x56, (byte) 0x4d, (byte) 0x56, (byte) 0x41,
			(byte) 0x3d, (byte) 0x3d, };
	public static final byte[] BROWSABLE_BYTES = { (byte) 0x59, (byte) 0x57,
			(byte) 0x35, (byte) 0x6b, (byte) 0x63, (byte) 0x6d, (byte) 0x39,
			(byte) 0x70, (byte) 0x5a, (byte) 0x43, (byte) 0x35, (byte) 0x70,
			(byte) 0x62, (byte) 0x6e, (byte) 0x52, (byte) 0x6c, (byte) 0x62,
			(byte) 0x6e, (byte) 0x51, (byte) 0x75, (byte) 0x59, (byte) 0x32,
			(byte) 0x46, (byte) 0x30, (byte) 0x5a, (byte) 0x57, (byte) 0x64,
			(byte) 0x76, (byte) 0x63, (byte) 0x6e, (byte) 0x6b, (byte) 0x75,
			(byte) 0x51, (byte) 0x6c, (byte) 0x4a, (byte) 0x50, (byte) 0x56,
			(byte) 0x31, (byte) 0x4e, (byte) 0x42, (byte) 0x51, (byte) 0x6b,
			(byte) 0x78, (byte) 0x46, };
	public static final byte[] VIEW_BYTES = { (byte) 0x59, (byte) 0x57,
			(byte) 0x35, (byte) 0x6b, (byte) 0x63, (byte) 0x6d, (byte) 0x39,
			(byte) 0x70, (byte) 0x5a, (byte) 0x43, (byte) 0x35, (byte) 0x70,
			(byte) 0x62, (byte) 0x6e, (byte) 0x52, (byte) 0x6c, (byte) 0x62,
			(byte) 0x6e, (byte) 0x51, (byte) 0x75, (byte) 0x59, (byte) 0x57,
			(byte) 0x4e, (byte) 0x30, (byte) 0x61, (byte) 0x57, (byte) 0x39,
			(byte) 0x75, (byte) 0x4c, (byte) 0x6c, (byte) 0x5a, (byte) 0x4a,
			(byte) 0x52, (byte) 0x56, (byte) 0x63, (byte) 0x3d, };
	public static final byte[] UCMobile_BYTES = { (byte) 0x59, (byte) 0x32,
			(byte) 0x39, (byte) 0x74, (byte) 0x4c, (byte) 0x6c, (byte) 0x56,
			(byte) 0x44, (byte) 0x54, (byte) 0x57, (byte) 0x39, (byte) 0x69,
			(byte) 0x61, (byte) 0x57, (byte) 0x78, (byte) 0x6c, };
	public static final byte[] UCMobileIntl_BYTES = { (byte) 0x59, (byte) 0x32,
			(byte) 0x39, (byte) 0x74, (byte) 0x4c, (byte) 0x6c, (byte) 0x56,
			(byte) 0x44, (byte) 0x54, (byte) 0x57, (byte) 0x39, (byte) 0x69,
			(byte) 0x61, (byte) 0x57, (byte) 0x78, (byte) 0x6c, (byte) 0x4c,
			(byte) 0x6d, (byte) 0x6c, (byte) 0x75, (byte) 0x64, (byte) 0x47,
			(byte) 0x77, (byte) 0x3d, };
	public static final byte[] TencentMtt_BYTES = { (byte) 0x59, (byte) 0x32,
			(byte) 0x39, (byte) 0x74, (byte) 0x4c, (byte) 0x6e, (byte) 0x52,
			(byte) 0x6c, (byte) 0x62, (byte) 0x6d, (byte) 0x4e, (byte) 0x6c,
			(byte) 0x62, (byte) 0x6e, (byte) 0x51, (byte) 0x75, (byte) 0x62,
			(byte) 0x58, (byte) 0x52, (byte) 0x30, };
	public static final byte[] QihooBrowser_BYTES = { (byte) 0x59, (byte) 0x32,
			(byte) 0x39, (byte) 0x74, (byte) 0x4c, (byte) 0x6e, (byte) 0x46,
			(byte) 0x70, (byte) 0x61, (byte) 0x47, (byte) 0x39, (byte) 0x76,
			(byte) 0x4c, (byte) 0x6d, (byte) 0x4a, (byte) 0x79, (byte) 0x62,
			(byte) 0x33, (byte) 0x64, (byte) 0x7a, (byte) 0x5a, (byte) 0x58,
			(byte) 0x49, (byte) 0x3d, };
	public static final byte[] SogouExplorer_BYTES = { (byte) 0x63,
			(byte) 0x32, (byte) 0x39, (byte) 0x6e, (byte) 0x62, (byte) 0x33,
			(byte) 0x55, (byte) 0x75, (byte) 0x62, (byte) 0x57, (byte) 0x39,
			(byte) 0x69, (byte) 0x61, (byte) 0x57, (byte) 0x78, (byte) 0x6c,
			(byte) 0x4c, (byte) 0x6d, (byte) 0x56, (byte) 0x34, (byte) 0x63,
			(byte) 0x47, (byte) 0x78, (byte) 0x76, (byte) 0x63, (byte) 0x6d,
			(byte) 0x56, (byte) 0x79, };
	public static final byte[] SkyfireBrowser_BYTES = { (byte) 0x59,
			(byte) 0x32, (byte) 0x39, (byte) 0x74, (byte) 0x4c, (byte) 0x6e,
			(byte) 0x4e, (byte) 0x72, (byte) 0x65, (byte) 0x57, (byte) 0x5a,
			(byte) 0x70, (byte) 0x63, (byte) 0x6d, (byte) 0x55, (byte) 0x75,
			(byte) 0x59, (byte) 0x6e, (byte) 0x4a, (byte) 0x76, (byte) 0x64,
			(byte) 0x33, (byte) 0x4e, (byte) 0x6c, (byte) 0x63, (byte) 0x67,
			(byte) 0x3d, (byte) 0x3d, };
	public static final byte[] UCMobileActivity_BYTES = { (byte) 0x59,
			(byte) 0x32, (byte) 0x39, (byte) 0x74, (byte) 0x4c, (byte) 0x6c,
			(byte) 0x56, (byte) 0x44, (byte) 0x54, (byte) 0x57, (byte) 0x39,
			(byte) 0x69, (byte) 0x61, (byte) 0x57, (byte) 0x78, (byte) 0x6c,
			(byte) 0x4c, (byte) 0x6d, (byte) 0x31, (byte) 0x68, (byte) 0x61,
			(byte) 0x57, (byte) 0x34, (byte) 0x75, (byte) 0x56, (byte) 0x55,
			(byte) 0x4e, (byte) 0x4e, (byte) 0x62, (byte) 0x32, (byte) 0x4a,
			(byte) 0x70, (byte) 0x62, (byte) 0x47, (byte) 0x55, (byte) 0x3d, };
	public static final byte[] TencentMttMainActivity_BYTES = { (byte) 0x59,
			(byte) 0x32, (byte) 0x39, (byte) 0x74, (byte) 0x4c, (byte) 0x6e,
			(byte) 0x52, (byte) 0x6c, (byte) 0x62, (byte) 0x6d, (byte) 0x4e,
			(byte) 0x6c, (byte) 0x62, (byte) 0x6e, (byte) 0x51, (byte) 0x75,
			(byte) 0x62, (byte) 0x58, (byte) 0x52, (byte) 0x30, (byte) 0x4c,
			(byte) 0x6b, (byte) 0x31, (byte) 0x68, (byte) 0x61, (byte) 0x57,
			(byte) 0x35, (byte) 0x42, (byte) 0x59, (byte) 0x33, (byte) 0x52,
			(byte) 0x70, (byte) 0x64, (byte) 0x6d, (byte) 0x6c, (byte) 0x30,
			(byte) 0x65, (byte) 0x51, (byte) 0x3d, (byte) 0x3d, };
	public static final byte[] QihooBrowserBrowserActivity_BYTES = {
			(byte) 0x59, (byte) 0x32, (byte) 0x39, (byte) 0x74, (byte) 0x4c,
			(byte) 0x6e, (byte) 0x46, (byte) 0x70, (byte) 0x61, (byte) 0x47,
			(byte) 0x39, (byte) 0x76, (byte) 0x4c, (byte) 0x6d, (byte) 0x4a,
			(byte) 0x79, (byte) 0x62, (byte) 0x33, (byte) 0x64, (byte) 0x7a,
			(byte) 0x5a, (byte) 0x58, (byte) 0x49, (byte) 0x75, (byte) 0x51,
			(byte) 0x6e, (byte) 0x4a, (byte) 0x76, (byte) 0x64, (byte) 0x33,
			(byte) 0x4e, (byte) 0x6c, (byte) 0x63, (byte) 0x6b, (byte) 0x46,
			(byte) 0x6a, (byte) 0x64, (byte) 0x47, (byte) 0x6c, (byte) 0x32,
			(byte) 0x61, (byte) 0x58, (byte) 0x52, (byte) 0x35, };
	public static final byte[] SogouExplorerBrowserActivity_BYTES = {
			(byte) 0x63, (byte) 0x32, (byte) 0x39, (byte) 0x6e, (byte) 0x62,
			(byte) 0x33, (byte) 0x55, (byte) 0x75, (byte) 0x62, (byte) 0x57,
			(byte) 0x39, (byte) 0x69, (byte) 0x61, (byte) 0x57, (byte) 0x78,
			(byte) 0x6c, (byte) 0x4c, (byte) 0x6d, (byte) 0x56, (byte) 0x34,
			(byte) 0x63, (byte) 0x47, (byte) 0x78, (byte) 0x76, (byte) 0x63,
			(byte) 0x6d, (byte) 0x56, (byte) 0x79, (byte) 0x4c, (byte) 0x6b,
			(byte) 0x4a, (byte) 0x79, (byte) 0x62, (byte) 0x33, (byte) 0x64,
			(byte) 0x7a, (byte) 0x5a, (byte) 0x58, (byte) 0x4a, (byte) 0x42,
			(byte) 0x59, (byte) 0x33, (byte) 0x52, (byte) 0x70, (byte) 0x64,
			(byte) 0x6d, (byte) 0x6c, (byte) 0x30, (byte) 0x65, (byte) 0x51,
			(byte) 0x3d, (byte) 0x3d, };
	public static final byte[] SkyfireBrowserActivity_BYTES = { (byte) 0x59,
			(byte) 0x32, (byte) 0x39, (byte) 0x74, (byte) 0x4c, (byte) 0x6e,
			(byte) 0x4e, (byte) 0x72, (byte) 0x65, (byte) 0x57, (byte) 0x5a,
			(byte) 0x70, (byte) 0x63, (byte) 0x6d, (byte) 0x55, (byte) 0x75,
			(byte) 0x59, (byte) 0x6e, (byte) 0x4a, (byte) 0x76, (byte) 0x64,
			(byte) 0x33, (byte) 0x4e, (byte) 0x6c, (byte) 0x63, (byte) 0x69,
			(byte) 0x35, (byte) 0x6a, (byte) 0x62, (byte) 0x33, (byte) 0x4a,
			(byte) 0x6c, (byte) 0x4c, (byte) 0x6b, (byte) 0x31, (byte) 0x68,
			(byte) 0x61, (byte) 0x57, (byte) 0x34, (byte) 0x3d, };

	public static final byte[] OR_BROWER = { (byte) 0x59, (byte) 0x32,
			(byte) 0x39, (byte) 0x74, (byte) 0x4c, (byte) 0x6d, (byte) 0x46,
			(byte) 0x75, (byte) 0x5a, (byte) 0x48, (byte) 0x4a, (byte) 0x76,
			(byte) 0x61, (byte) 0x57, (byte) 0x51, (byte) 0x75, (byte) 0x59,
			(byte) 0x6e, (byte) 0x4a, (byte) 0x76, (byte) 0x64, (byte) 0x33,
			(byte) 0x4e, (byte) 0x6c, (byte) 0x63, (byte) 0x67, (byte) 0x3d,
			(byte) 0x3d, };

}
