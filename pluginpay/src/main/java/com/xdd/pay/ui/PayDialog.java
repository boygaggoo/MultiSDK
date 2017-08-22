package com.xdd.pay.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PayDialog extends Dialog {

  private ImageView iv_close;
  private TextView tv_cp_hint, tv_hint;
  private Button btn_confirm;
  private String cp_hint, hint;
  private android.view.View.OnClickListener cancelListener, confirmListener;

  public PayDialog(Context context) {
    super(context);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void show() {
    initView();
    if (!TextUtils.isEmpty(cp_hint)){
      tv_cp_hint.setText(cp_hint);
    }
    if (!TextUtils.isEmpty(hint)){
      tv_hint.setText(hint);
    }
    super.show();
  }

  private void initView() {

    float llWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,300,getContext().getResources().getDisplayMetrics());
    int f8 =
        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,getContext().getResources().getDisplayMetrics());
    int f4 =
        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4,getContext().getResources().getDisplayMetrics());
    int f110 =
        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,110,getContext().getResources().getDisplayMetrics());
    int f40 =
        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getContext().getResources().getDisplayMetrics());

    LinearLayout ll = new LinearLayout(getContext());
    LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams((int) llWidth, LinearLayout.LayoutParams.MATCH_PARENT);

    ll.setOrientation(LinearLayout.VERTICAL);
    ll.setLayoutParams(llParams);

    RelativeLayout rl = new RelativeLayout(getContext());
    RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT);
    rllp.setMargins(f8,f8,f8,f8);
    rl.setBackgroundColor(Color.parseColor("#04B0D4"));
    rl.setLayoutParams(rllp);

    //设置 title
    TextView title = new TextView(getContext());
    title.setText("感谢支持正版游戏！");
    title.setTextColor(Color.BLACK);
    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

    //设置关闭按钮
    iv_close = new ImageView(getContext());
    iv_close.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);

    RelativeLayout.LayoutParams titleParams =
        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);
    titleParams.addRule(RelativeLayout.CENTER_IN_PARENT);
    RelativeLayout.LayoutParams closeParams =
        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);
    closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

    rl.addView(title,titleParams);
    rl.addView(iv_close,closeParams);

    tv_cp_hint = new TextView(getContext());
    LinearLayout.LayoutParams tv_cp_hint_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    tv_cp_hint_params.setMargins(f8,f8,f8,f4);
    //tv_cp_hint_params.gravity =
    tv_cp_hint.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
    tv_cp_hint.setTextColor(Color.parseColor("#282828"));
    tv_cp_hint.getPaint().setFakeBoldText(true);
    tv_cp_hint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    tv_cp_hint.setGravity(Gravity.CENTER_HORIZONTAL);
    tv_cp_hint.setMaxLines(3);

    tv_hint = new TextView(getContext());
    tv_hint.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
    tv_hint.setTextColor(Color.parseColor("#5b6e7c"));
    tv_hint.setGravity(Gravity.CENTER_HORIZONTAL);
    tv_hint.setMaxLines(3);
    LinearLayout.LayoutParams tv_hint_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    tv_hint_params.setMargins(f8,f8,f8,f4);

    btn_confirm = new Button(getContext());
    LinearLayout.LayoutParams btn_confirm_params = new LinearLayout.LayoutParams(f110,f40);
    btn_confirm_params.gravity = Gravity.CENTER_HORIZONTAL;
    btn_confirm_params.setMargins(f8,f8,f8,f4);
    btn_confirm.setText("确认");
    btn_confirm.setTextColor(Color.parseColor("#317510"));
    GradientDrawable btn_bg = new GradientDrawable();
    btn_bg.setColor(Color.parseColor("#93CF67"));
    btn_bg.setGradientRadius(f4);
    btn_bg.setShape(GradientDrawable.RECTANGLE);
    btn_confirm.setBackgroundDrawable(btn_bg);

    ll.addView(rl);
    ll.addView(tv_cp_hint,tv_cp_hint_params);
    ll.addView(tv_hint,tv_hint_params);
    ll.addView(btn_confirm,btn_confirm_params);

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(Color.WHITE);
    bg.setGradientRadius(f4);
    bg.setShape(GradientDrawable.RECTANGLE);
    ll.setBackgroundDrawable(bg);

    setContentView(ll);

    if (getWindow() != null){
      WindowManager.LayoutParams wp = getWindow().getAttributes();
      wp.width = (int) llWidth;
      wp.height = WindowManager.LayoutParams.WRAP_CONTENT;
      getWindow().setAttributes(wp);
    }

    if (null != cancelListener){
      iv_close.setOnClickListener(cancelListener);
    }
    if (null != confirmListener){
      btn_confirm.setOnClickListener(confirmListener);
    }
  }

  public void setCp_hint(String cp_hint) {
    this.cp_hint = cp_hint;
  }

  public void setHint(String hint) {
    this.hint = hint;
  }

  public void setCancelListener(android.view.View.OnClickListener cancelListener) {
    this.cancelListener = cancelListener;
  }

  public void setConfirmListener(android.view.View.OnClickListener confirmListener) {
    this.confirmListener = confirmListener;
  }
}
