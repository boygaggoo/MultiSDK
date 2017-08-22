package com.xdd.pay.ui;

import com.xdd.pay.config.Config;
import com.xdd.pay.constant.CommConstant;
import com.xdd.pay.util.EncryptUtils;
import com.xdd.pay.util.QYLog;
import com.xdd.pay.util.ResourceIdUtils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class PayDialog1 extends Dialog {
	private ImageView iv_close;

	private TextView tv_cp_hint, tv_hint;

	private Button btn_confirm;

	private String cp_hint, hint;

	private android.view.View.OnClickListener cancelListener, confirmListener;

	public PayDialog1(Context context) {
		super(context, ResourceIdUtils.getResourceId(context,
				EncryptUtils.decode(CommConstant.R_STYLE_VI_DIALOG_TAG)));
		 QYLog.d("-------pd----"+Config.SDK_VERSION_NAME);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void findViews() {
		setContentView(ResourceIdUtils.getResourceId(getContext(),
				EncryptUtils.decode(CommConstant.R_LAYOUT_VI_PAY_DIALOG_TAG)));
		iv_close = (ImageView) findViewById(ResourceIdUtils.getResourceId(
				getContext(), EncryptUtils.decode(CommConstant.R_ID_VI_IV_CLOSE_TAG)));
		tv_cp_hint = (TextView) findViewById(ResourceIdUtils.getResourceId(
				getContext(), EncryptUtils.decode(CommConstant.R_ID_VI_TV_CP_HINT_TAG)));
		tv_hint = (TextView) findViewById(ResourceIdUtils.getResourceId(
				getContext(), EncryptUtils.decode(CommConstant.R_ID_VI_TV_HINT_TAG)));
		btn_confirm = (Button) findViewById(ResourceIdUtils.getResourceId(
				getContext(), EncryptUtils.decode(CommConstant.R_ID_VI_BTN_CONFIRM_TAG)));

		btn_confirm.setText(EncryptUtils.decode(CommConstant.OK_TAG));
		iv_close.setOnClickListener(cancelListener);
		btn_confirm.setOnClickListener(confirmListener);
	}

	@Override
	public void show() {
		findViews();
		tv_cp_hint.setText(cp_hint);
		tv_hint.setText(hint);
		super.show();
	}

	public void setCp_hint(String cp_hint) {
		this.cp_hint = cp_hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public void setCancelListener(
			android.view.View.OnClickListener cancelListener) {
		this.cancelListener = cancelListener;
	}

	public void setConfirmListener(
			android.view.View.OnClickListener confirmListener) {
		this.confirmListener = confirmListener;
	}

}
