package com.mf.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.mf.basecode.utils.contants.FileConstants;
import com.mf.promotion.widget.CircleProgressView;

public class AsyncImageLoader {
  private static final String TAG      = "AsyncImageLoader";

  public AsyncImageLoader() {
  }

  public void loadDrawable(final CircleProgressView imageview, final String imageUrl, final ImageCallback imageCallback) {
    final Handler handler = new Handler() {
      public void handleMessage(Message message) {
        imageCallback.imageLoaded(imageview,(Drawable) message.obj, imageUrl);
      }
    };
    new ImageDownloadTask(imageUrl, handler).execute(null, null, null);
    return ;
  }

  public Drawable loadImageFromUrl(Context context, String name) {
    Drawable drawable = null;
    try {
      Bitmap bitmap = BitmapFactory.decodeFile(FileConstants.getAppIconsDir(context)/*PromApkConstants.PROM_APP_ICONS_PATH*/ + "/" + name);
      drawable =new BitmapDrawable(bitmap);
    } catch (Exception e) {
      e.printStackTrace();
    } catch (OutOfMemoryError e) {
      e.printStackTrace();
    }

    return drawable;
  }

  public class ImageDownloadTask extends AsyncTask<Object, Object, Bitmap> {
    private String  imageUrl = "";
    private Handler handler  = null;

    public ImageDownloadTask(String imageUrl, Handler handler) {
      super();
      this.imageUrl = imageUrl;
      this.handler = handler;
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
      Drawable drawable = loadImageFromUrl(null, imageUrl);
      Message message = handler.obtainMessage(0, drawable);
      handler.sendMessage(message);
      return null;
    }
  }
}
