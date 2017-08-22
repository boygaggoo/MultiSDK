package com.xdd.pay.plugin.util;

import java.io.File;

import android.content.Context;
import android.text.TextUtils;

import com.xdd.pay.constant.CommConstant;
import com.xdd.pay.db.StorageConfig;
import com.xdd.pay.db.StorageUtils;
import com.xdd.pay.util.EncryptUtils;
import com.xdd.pay.util.QYLog;
import com.xdd.pay.util.ReflectionUtils;

import dalvik.system.DexClassLoader;

public class PluginUtils {

	private static PluginUtils instance = null;
	private Context context = null;
	private String mFilePath = null;
	private DexClassLoader newClassLoader = null;
	private String pluginTypeOfUpdate;// 升级包的插件类型

	public static PluginUtils getInstance() {
		if (null == instance) {
			instance = new PluginUtils();
		}
		return instance;
	}
	
	private DexClassLoader loadClassLoader(Context mContext) {
		return loadClassLoader(mContext, false);
	}
	
	public DexClassLoader loadClassLoader(Context mContext, boolean reload) {
		if (null == context) {
			context = mContext;
		}
		if (null == mContext) {
			mContext = context;
		}
		String filePath = "";
		if (mContext == null){
		    QYLog.d("cl---mContext == null--- ");
        } 
		
		if (mContext != null){
		    filePath = StorageUtils.getConfig4String(mContext, StorageConfig.DOWNLOAD_JAR);
		} else {
//		    filePath = DBUtils.getInstance(mContext).queryCfgValueByKey(DBConfig.DOWNLOAD_JAR);
		}
//		QYLog.d("gloadClassLoader--filePath: " + filePath);
		
		if (null == newClassLoader || null == mFilePath || !mFilePath.equals(filePath) || reload) {
			mFilePath = filePath;
//		    QYLog.d("gloadClassLoader--mFilePath: " + mFilePath);
			initClassLoader(mContext);
		}
		return newClassLoader;
	}
	
	private void initClassLoader(Context mContext) {
		// File dexFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Tencent/QQfile_recv/qy.jar");// /test/qy.jar
		File dexFile = null;
		if (!TextUtils.isEmpty(mFilePath)) {
			dexFile = new File(mFilePath);
		}
		
		if (null != mContext && null != dexFile && dexFile.exists()) {
			newClassLoader = new DexClassLoader(dexFile.getAbsolutePath(), mContext.getDir(EncryptUtils.decode(CommConstant.DEX_TAG), 0).getAbsolutePath(), null, ClassLoader.getSystemClassLoader());
			try {
				Class<?> classEncryptUtils = newClassLoader.loadClass(EncryptUtils.class.getName());
				pluginTypeOfUpdate = (String) ReflectionUtils.invokeMethod4Static(classEncryptUtils, EncryptUtils.decode(CommConstant.GETPLUGINTYPE_TAG), new Class[] {}, new Object[] {});
//				QYLog.d("get pluginTypeOfUpdate = ","----"+pluginTypeOfUpdate);
			} catch (Exception e) {
				QYLog.e(e.toString());
			}
		}
	}
	
	public boolean isTypeCheckPass() {
		return null != pluginTypeOfUpdate && pluginTypeOfUpdate.equals(EncryptUtils.getPluginType());
	}

	public Class<?> getClass(Context mContext, String className) {
		if (null == loadClassLoader(mContext)) {
			return null;
		}
		Class<?> clazz = null;
		try {
		    QYLog.d("isTypeCheckPass() " + isTypeCheckPass());
			if (isTypeCheckPass()) {
				clazz = newClassLoader.loadClass(className);
			}
		} catch (ClassNotFoundException e) {
			QYLog.e(e.toString());
		}
		return clazz;
	}
	
}
