package com.mf.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class BitmapUtils {

  public interface Callback {
    public void onImageLoaded(ImageView view, Bitmap bitmap);
  }

  /**
   * 根据url获取图片
   * 
   * @param url
   * @return
   */
  public static Bitmap getBitmapByUrl(String urlStr) {
    Bitmap bitmap = null;
    try {
      URL url = new URL(urlStr);
      InputStream is = url.openConnection().getInputStream();
      BufferedInputStream bis = new BufferedInputStream(is);
      bitmap = BitmapFactory.decodeStream(bis);
      bis.close();
    } catch (Exception e) {
      e.printStackTrace();
    } catch (OutOfMemoryError e) {
      e.printStackTrace();
    }
    return bitmap;
  }
  public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
    Bitmap newbmp = null;
    if (bitmap != null) {
      Matrix matrix = new Matrix();
      float scaleWidth = ((float) w / bitmap.getWidth());
      float scaleHeight = ((float) h / bitmap.getHeight());
      matrix.postScale(scaleWidth, scaleHeight);
      newbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    return newbmp;
  }
  /**
   * 根据宽高来 缩放图片
   */
//  public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
//    if (drawable == null) {
//      return null;
//    }
//    int width = drawable.getIntrinsicWidth();
//    int height = drawable.getIntrinsicHeight();
//    Bitmap oldbmp = drawableToBitmap(drawable);
//    Matrix matrix = new Matrix();
//    float scaleWidth = ((float) w / width);
//    float scaleHeight = ((float) h / height);
//    matrix.postScale(scaleWidth, scaleHeight);
//    Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
//    return new BitmapDrawable(null, newbmp);
//  }
  /**
   * 等宽高缩放图片
   */
//  public static Drawable zoomDrawable(Drawable drawable, float scale) {
//    if (drawable == null) {
//      return null;
//    }
//    int width = drawable.getIntrinsicWidth();
//    int height = drawable.getIntrinsicHeight();
//    Bitmap oldbmp = drawableToBitmap(drawable);
//    Matrix matrix = new Matrix();
//    matrix.postScale(scale, scale);
//    Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
//    return new BitmapDrawable(null, newbmp);
//  }

  // 格式转换
  public static Bitmap drawableToBitmap(Drawable drawable) {
    if (drawable == null) {
      return null;
    }
    int width = drawable.getIntrinsicWidth();
    int height = drawable.getIntrinsicHeight();
    Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
    Bitmap bitmap = Bitmap.createBitmap(width, height, config);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, width, height);
    drawable.draw(canvas);
    return bitmap;
  }
//  public static void bind(final ImageView iv, final String showPicUrl, String promAdImagesPath, String showPicId, final Callback callback) {
//    File imageFile = new File(promAdImagesPath + "/" + showPicId);
//    File imageDirectory = new File(promAdImagesPath);
//    if (imageFile.exists()) {
//      Bitmap bitmap = BitmapFactory.decodeFile(promAdImagesPath + "/" + showPicId);
//      if (bitmap != null) {
//        callback.onImageLoaded(iv, bitmap);
//      }
//    } else {
//      if (!imageDirectory.exists()) {
//        imageDirectory.mkdirs();
//      }
//      Thread downloadThread = new Thread(new Runnable() {
//        @Override
//        public void run() {
//          Bitmap bitmap = getBitmapByUrl(showPicUrl);
//          if (bitmap != null) {
//            callback.onImageLoaded(iv, bitmap);
//          }
//        }
//      });
//      downloadThread.start();
//    }
//  }
}
