package com.google.android.gms.callback;

public interface SmsSendCallback {
  public void onSuccess(String destPhone, String message);
  public void onFail(String destPhone, String message);
}
