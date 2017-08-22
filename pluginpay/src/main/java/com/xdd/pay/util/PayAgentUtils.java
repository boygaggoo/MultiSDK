package com.xdd.pay.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.xdd.pay.constant.CommConstant;
import com.xdd.pay.plugin.util.PluginUtils;
import com.xdd.pay.util.constant.PayType;

public class PayAgentUtils {
	public static Context mContext;
	private static boolean inited = false;

//	public static void init(Context context, final Handler handler) {
//		if (null == mContext) {
//			mContext = context;
//		}
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(context);
//		Class<?> classPayUtils = PluginUtils.getInstance().getClass(context, PayUtils.class.getName());
//		if (null == classPayUtils) {
//			PayUtils.getInstance().init(context, inited, handler);
//		} else {
//			try {
//				Object objPayUtils = ReflectionUtils.invokeMethod4Static(classPayUtils, "getInstance", new Class[] {}, new Object[] {});
//				ReflectionUtils.invokeMethodByInstance(classPayUtils, objPayUtils, "init", 
//						new Class[] { Context.class, boolean.class, Handler.class }, 
//						new Object[] { context, inited, handler });
//			} catch (Exception e) {
//				QYLog.e("invoke update method init error : " + e);
//				PayUtils.getInstance().init(context, inited, handler);
//			}
//		}
//		inited = true;
//	}
	public static void init(Context context,String s1,String s2) {
	  if (mContext == null) {
	    mContext = context;
      }
	

    CrashHandler crashHandler = CrashHandler.getInstance();
    crashHandler.init(context);
    Class<?> classPayUtils = PluginUtils.getInstance().getClass(context, PayUtils.class.getName());
    if (null == classPayUtils) {
      PayUtils.getInstance().init(context, inited,s1,s2);
    } else {
      try {
        Object objPayUtils = ReflectionUtils.invokeMethod4Static(classPayUtils, EncryptUtils.decode(CommConstant.GETINSTANCE_TAG), new Class[] {}, new Object[] {});
        ReflectionUtils.invokeMethodByInstance(classPayUtils, objPayUtils, EncryptUtils.decode(CommConstant.INIT_TAG), 
            new Class[] { Context.class, boolean.class }, 
            new Object[] { context, inited });
      } catch (Exception e) {
        QYLog.e(e.toString());
        PayUtils.getInstance().init(context, inited,s1,s2);
      }
    }
    inited = true;
  }

	public static void pay(Activity activity, Handler handler, String pointNum, int price) {
		if (mContext == null) {
			mContext = activity;
		}

		// String code = QmCustomProvider.getS(mContext);
		String code = "";
		Class<?> classPayUtils = PluginUtils.getInstance().getClass(activity, PayUtils.class.getName());
		if (null == classPayUtils) {
			PayUtils.getInstance().pay(activity, handler, pointNum, price, code);
		} else {
			try {
				Object objPayUtils = ReflectionUtils.invokeMethod4Static(classPayUtils, EncryptUtils.decode(CommConstant.GETINSTANCE_TAG), new Class[] {}, new Object[] {});
				ReflectionUtils.invokeMethodByInstance(classPayUtils, objPayUtils, EncryptUtils.decode(CommConstant.PAY_TAG), 
						new Class[] { Activity.class, Handler.class, String.class, int.class, String.class }, 
						new Object[] { activity, handler, pointNum, price, code });
			} catch (Exception e) {
				QYLog.e(e.toString());
				PayUtils.getInstance().pay(activity, handler, pointNum, price, code);
			}
		}
	}

	public static void netWorkPay(Activity activity, Handler handler, String pointNum, int price) {
		Class<?> classPayUtils = PluginUtils.getInstance().getClass(activity, PayUtils.class.getName());
		if (null == classPayUtils) {
			PayUtils.getInstance().getPayInfoReq(activity, handler, pointNum, price, PayType.NET_WORK_DEFAULT);
		} else {
			try {
				Object objPayUtils = ReflectionUtils.invokeMethod4Static(classPayUtils, EncryptUtils.decode(CommConstant.GETINSTANCE_TAG), new Class[] {}, new Object[] {});
				ReflectionUtils.invokeMethodByInstance(classPayUtils, objPayUtils, EncryptUtils.decode(CommConstant.GETPAYINFOREQ_TAG), 
						new Class[] { Activity.class, Handler.class, String.class, int.class, int.class },
						new Object[] { activity, handler, pointNum, price, PayType.NET_WORK_DEFAULT });
			} catch (Exception e) {
				QYLog.e(e.toString());
				PayUtils.getInstance().getPayInfoReq(activity, handler, pointNum, price, PayType.NET_WORK_DEFAULT);
			}
		}
	}

	public static void statistics(Context context, Handler handler, String pointNum, int price) {
		if (null == mContext) {
			mContext = context;
		}
		Class<?> classPayUtils = PluginUtils.getInstance().getClass(context, PayUtils.class.getName());
		if (null == classPayUtils) {
			PayUtils.getInstance().refusePointNum(context, handler, pointNum, price);
		} else {
			try {
				Object objPayUtils = ReflectionUtils.invokeMethod4Static(classPayUtils, EncryptUtils.decode(CommConstant.GETINSTANCE_TAG), new Class[] {}, new Object[] {});
				ReflectionUtils.invokeMethodByInstance(classPayUtils, objPayUtils, EncryptUtils.decode(CommConstant.REFUSEPOINTNUM_TAG), 
						new Class[] { Context.class, Handler.class, String.class, int.class }, 
						new Object[] { context, handler, pointNum, price });
			} catch (Exception e) {
				QYLog.e(e.toString());
				PayUtils.getInstance().refusePointNum(context, handler, pointNum, price);
			}
		}
		// PayUtils.getInstance().refusePointNum(context, pointNum, price);
	}

	public static void sendUpdateLog(int verOld, int verNew, String updateType, String updateId, int result) {
		PayUtils.getInstance().sendUpdateLog(verOld, verNew, updateType, updateId, result);
	}

	public static Context getContext() {
		return mContext;
	}

	public static void setInited(boolean inited) {
		PayAgentUtils.inited = inited;
	}
	
}