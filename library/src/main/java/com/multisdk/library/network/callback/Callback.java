package com.multisdk.library.network.callback;

import android.os.Handler;
import android.os.Looper;
import com.multisdk.library.network.exception.HttpException;
import com.multisdk.library.network.serializer.CommMessage;

public class Callback implements NetworkCallback{

  private Handler handler = new Handler(Looper.getMainLooper());

  private HttpCallback httpCallback;

  public Callback(HttpCallback httpCallback) {
    this.httpCallback = httpCallback;
  }

  @Override public void onSuccess(final CommMessage resp) {
    handler.post(new Runnable() {
      @Override public void run() {
        httpCallback.onSuccess(resp);
      }
    });
  }

  @Override public void onFailure(final HttpException e) {
    handler.post(new Runnable() {
      @Override public void run() {
        httpCallback.onFailure(e);
      }
    });
  }
}
