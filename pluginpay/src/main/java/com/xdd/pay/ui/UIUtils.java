package com.xdd.pay.ui;

import android.app.Activity;
import android.app.ProgressDialog;

import com.xdd.pay.util.QYLog;

public class UIUtils {
  private static ProgressDialog pDialog;

  /**
   * 展现dialog
   * 
   * @param activity
   * @param content
   */
  public static void showProgressDialog(final Activity activity, final String content) {
    dismissProgressDialog();
    try {
      pDialog = new ProgressDialog(activity);
      pDialog.setMessage(content);
      pDialog.setCancelable(false);
      pDialog.setCanceledOnTouchOutside(false);
      pDialog.show();
    } catch (Exception e) {
      QYLog.p(e);
    }
  }
  
  public static void changeProgressDialog(final Activity activity, final String content) {
    if (pDialog != null && pDialog.isShowing()) {
      pDialog.setMessage(content);
    } else {
      showProgressDialog(activity, content);
    }
  }

  /**
   * 取消dialog
   */
  public static void dismissProgressDialog() {
    if (pDialog != null && pDialog.isShowing()) {
      pDialog.dismiss();
    }
  }

  /**
   * 展现二次确认弹框界面
   * 
   * @param activity
   * @param tip
   */
  public static void showConfirmDialog(final Activity activity, final String tip) {
     SureDialog mConfirmDialog = new SureDialog(activity);
     mConfirmDialog.creatDialog(tip);
     //Intent intent = new Intent(activity, ConfirmDialogActivity.class);
     //intent.putExtra(ExtraName.CONTENT, tip);
     //activity.startActivity(intent);
  }
  
  public static boolean dialogIsShowing(){
	  if(pDialog == null){
		  return false;
	  }
	  return pDialog.isShowing();
  }
}
