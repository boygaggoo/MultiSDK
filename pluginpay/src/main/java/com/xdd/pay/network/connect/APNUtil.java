package com.xdd.pay.network.connect;

import java.util.Locale;

import com.xdd.pay.network.util.NetConstant;
import com.xdd.pay.util.EncryptUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
  
public class APNUtil {  
  
    public static final Uri PREFERRED_APN_URI;  
  
    private String mApn; // 接入点名称   
  
    private String mPort; // 端口号   
  
    private String mProxy; // 代理服务器   
  
    private boolean mUseWap; // 是否正在使用WAP   
  
    static {  
        PREFERRED_APN_URI = Uri.parse(EncryptUtils.decode(NetConstant.CONTENT_PREFERAPN_TAG)); // 取得当前设置的APN  
    }  
  
    public APNUtil(Context context) {  
        checkNetworkType(context);  
    }  
  
    /** 
     * 获得当前设置的APN相关参数 
     * @param context 
     */  
    private void checkApn(Context context) {  
        ContentResolver contentResolver = context.getContentResolver();  
        Uri uri = PREFERRED_APN_URI;  
        String[] apnInfo = new String[3];  
        apnInfo[0] = EncryptUtils.decode(NetConstant.APN_TAG);  
        apnInfo[1] = EncryptUtils.decode(NetConstant.PROXY_TAG);  
        apnInfo[2] = EncryptUtils.decode(NetConstant.PORT_TAG);  
  
        Cursor cursor = contentResolver.query(uri, apnInfo, null, null, null);  
        if (cursor != null) {  
            while (cursor.moveToFirst()) {  
                this.mApn = cursor.getString(cursor.getColumnIndex(EncryptUtils.decode(NetConstant.APN_TAG)));  
                this.mProxy = cursor.getString(cursor.getColumnIndex(EncryptUtils.decode(NetConstant.PROXY_TAG)));  
                this.mPort = cursor.getString(cursor.getColumnIndex(EncryptUtils.decode(NetConstant.PORT_TAG)));  
  
                // 代理为空   
                if ((this.mProxy == null) || (this.mProxy.length() <= 0)) {  
                    String apn = this.mApn.toUpperCase(Locale.getDefault());  
                      
                    // 中国移动WAP设置：APN：CMWAP；代理：10.0.0.172；端口：80   
                    // 中国联通WAP设置：APN：UNIWAP；代理：10.0.0.172；端口：80   
                    // 中国联通WAP设置（3G）：APN：3GWAP；代理：10.0.0.172；端口：80   
                    if ((apn.equals(EncryptUtils.decode(NetConstant.CMWAP_TAG))) || (apn.equals(EncryptUtils.decode(NetConstant.UNIWAP_TAG))) || (apn.equals(EncryptUtils.decode(NetConstant.WAP_TAG)))) {  
                        this.mUseWap = true;  
                        this.mProxy = EncryptUtils.decode(NetConstant.LOCAL_TAG);  
                        this.mPort = EncryptUtils.decode(NetConstant.PROT_80_TAG);  
                        break;  
                    }  
                      
                    // 中国电信WAP设置：APN(或者接入点名称)：CTWAP；代理：10.0.0.200；端口：80   
                    if (apn.equals("CTWAP")) {  
                        this.mUseWap = true;  
                        this.mProxy = EncryptUtils.decode(NetConstant.LOCAL_200_TAG);  
                        this.mPort = EncryptUtils.decode(NetConstant.PROT_80_TAG);  
                        break;  
                    }  
                      
                }  
                this.mPort = EncryptUtils.decode(NetConstant.PROT_80_TAG);  
                this.mUseWap = true;  
                break;  
            }  
  
        }  
  
        this.mUseWap = false;  
        cursor.close();  
    }  
  
    /** 
     * 检测当前使用的网络类型是WIFI还是WAP 
     * @param context 
     */  
    private void checkNetworkType(Context context) {  
        NetworkInfo networkInfo = ((ConnectivityManager) context  
                .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();  
        if (networkInfo != null) {  
            if (!EncryptUtils.decode(NetConstant.WIFI_TAG).equals(networkInfo.getTypeName().toLowerCase(Locale.getDefault()))) {
            	if (Build.VERSION.SDK_INT <= 18) {
            		checkApn(context);
				}
                return;  
            }  
            this.mUseWap = false;  
        }  
    }  
  
    /** 
     * 判断当前网络连接状态 
     * @param context 
     * @return 
     */  
    public static boolean isNetworkConnected(Context context) {  
        NetworkInfo networkInfo = ((ConnectivityManager) context  
                .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE))  
                .getActiveNetworkInfo();  
        if (networkInfo != null) {  
            return networkInfo.isConnectedOrConnecting();  
        }  
        return false;  
    }  
  
    public String getApn() {  
        return this.mApn;  
    }  
  
    public String getProxy() {  
        return this.mProxy;  
    }  
  
    public String getProxyPort() {  
        return this.mPort;  
    }  
  
    public boolean isWapNetwork() {  
        return this.mUseWap;  
    }  
}
