package com.xdd.pay.ui;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.xdd.pay.callback.ResultCode;
import com.xdd.pay.constant.CommConstant;
import com.xdd.pay.network.object.PayInfo;
import com.xdd.pay.util.EncryptUtils;
import com.xdd.pay.util.PayUtils;
import com.xdd.pay.util.constant.MOType;
import com.xdd.pay.util.constant.ResultType;

public class SureDialog {
  static PayDialog payDialog;
  static Activity mActivity;
  private static PayInfo curPayInfo = null;
  public SureDialog(Activity activity) {
      mActivity = activity;
//      QYLog.d("ConfirmDialog----onCreate" + Config.SDK_VERSION_NAME);
      String content = "";
    //  Bundle b = this.getIntent().getExtras();
   //   if (b != null) {
          curPayInfo = PayUtils.getInstance().mCurPayInfo;
         // content = b.getString(ExtraName.CONTENT);
     // }
      creatDialog(content);
}


/*public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    try {
      QYLog.d("ConfirmDialogActivity----onCreate" + Config.SDK_VERSION_NAME);
      String content = "";
      Bundle b = this.getIntent().getExtras();
      if (b != null) {
    	  curPayInfo = PayUtils.getInstance().mCurPayInfo;
    	  content = b.getString(ExtraName.CONTENT);
      }
      creatDialog(content);
    } catch (Exception e) {
      PayUtils.getInstance().callback(ResultCode.CONFIRM_DIALOG_ERROR, "二次确认框异常");
      finish();
    }

  }*/
 /* public void ConfirmDialog(Activity activity) {
      
     
  }
  */

  public  void creatDialog(String content) {
    if (payDialog == null) {
      payDialog = new PayDialog(mActivity);
      payDialog.setCancelable(false);
      payDialog.setCancelListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
          if (payDialog.isShowing()) {
            payDialog.dismiss();
          }
//          QYLog.d("ConfirmDialogActivity----quit-" + Config.SDK_VERSION_NAME);
        quit();
        }
      });
      payDialog.setConfirmListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          if (payDialog.isShowing()) {
            payDialog.dismiss();
          }
          define();
        }
      });
    }
    if (!TextUtils.isEmpty(content)) {
      payDialog.setCp_hint(content);
    }
    payDialog.setHint(EncryptUtils.decode(CommConstant.PAY_TIP));
    payDialog.show();
    payDialog.setCanceledOnTouchOutside(false);
    payDialog.setCancelable(false);
  }

  /**
   * 确定
   */
  private void define() {
//      QYLog.d("ConfirmDialog----define" + Config.SDK_VERSION_NAME);
    if (curPayInfo != null) {
     	PayUtils.getInstance().addPayLog(MOType.REQ, ResultType.SUCCESS, curPayInfo.getPayCodeInfoList(), null, curPayInfo);
        PayUtils.getInstance().beginPay(curPayInfo);
	}
  }

  /**
   * 取消
   */
  private void quit() {
    if(curPayInfo != null) {
       PayUtils.getInstance().addPayLog(MOType.REQ, ResultType.SECOND_REFUSE, curPayInfo.getPayCodeInfoList(), null, curPayInfo);
    }
    PayUtils.getInstance().callback(ResultCode.PAY_CANCEL, EncryptUtils.decode(CommConstant.PAY_CANCEL));
  }

}
