package com.mf.promotion.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleProgressView extends ImageView {
  private static final int   DEFAULT_MAX_PROGRESS_VALUE = 100;                                        // 默认进度条最大值
  private static final int   DEFAULT_PAINT_COLOR        = 0xFF000000;                                 // 默认画笔颜色
  private static final float DEFAULT_WIDTH_BORDER       = 5;                                          // 圆形区域向里缩进的距离
  private static final float DEFAULT_ARC_BORDER_WIDTH   = 5;                                          // 默认圆形宽度
  private static final int   DEFAULT_ALPHA              = 160;                                        // 默认透明度
                                                                                                       // 0~255
  private CircleAttribute    mCircleAttribute;                                                        // 圆形进度条基本属性
  private int                mMaxProgress;                                                            // 进度条最大值
  private int                mCurProgress;                                                            // 进度条当前值
  private Drawable           mBackgroundPicture;                                                      // 背景图
  private PorterDuffXfermode mode                       = new PorterDuffXfermode(PorterDuff.Mode.XOR);
  private Paint              paint                      = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Path               path                       = new Path();
  private boolean            isDownloading              = true;
  private Context            mContext;

  public CircleProgressView(Context context) {
    super(context);
    mContext = context;
    defaultParam();
  }

  public CircleProgressView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    defaultParam();
  }

  /**
   * 默认参数
   */
  private void defaultParam() {
    mCircleAttribute = new CircleAttribute();
    mMaxProgress = DEFAULT_MAX_PROGRESS_VALUE;
    mCircleAttribute.coverColor = DEFAULT_PAINT_COLOR;
    mCircleAttribute.mArcWidth = DEFAULT_ARC_BORDER_WIDTH;
    mCircleAttribute.widthBorder = DEFAULT_WIDTH_BORDER;
    mCircleAttribute.alpha = DEFAULT_ALPHA;
    mCircleAttribute.maxProgress = mMaxProgress;
    mCurProgress = 0;
  }

  /**
   * 获取最大进度值
   * 
   * @return 最大进度值
   */
  public int getMaxProgress() {
    return mMaxProgress;
  }

  /**
   * 设置最大进度值
   * 
   * @param mMaxProgress
   *          最大进度值
   */
  public void setMaxProgress(int mMaxProgress) {
    this.mMaxProgress = mMaxProgress;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { // 设置视图大小
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);
    mBackgroundPicture = getBackground();
    if (mBackgroundPicture != null) {
      width = mBackgroundPicture.getMinimumWidth();
      height = mBackgroundPicture.getMinimumHeight();
    }
    setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
  }

  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mCircleAttribute.autoFix(w, h);
  }

  long start = 0;

  public boolean isDownloading() {
    return isDownloading;
  }

  public void setDownloading(boolean isDownloading) {
    this.isDownloading = isDownloading;
    invalidate();
  }

  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    // 进度为0或最大值的时候，不显示进度
    if (mCurProgress == 0 || mCurProgress >= mMaxProgress) {
      return;
    }
    // 绘制覆盖层
    Bitmap grayCover = makeGrayCover(mCircleAttribute.mRectBorder);
    Bitmap arcBitmap;
    // 根据进度计算扇形角度
    float rate = (float) mCurProgress / mMaxProgress;
    float sweep = 360 * rate;
    // 开始的角度，默认12点方向（270或-90）
    float startAngle = mCircleAttribute.mDrawPos;
    // 绘制中心圆环和扇形区域
    arcBitmap = makeArcCover(mCircleAttribute.mRectBorder, startAngle, sweep);
    mCircleAttribute.mArcPaint.setStyle(Paint.Style.FILL);
    // 根据覆盖层与（中心圆环+扇形区）去交集显示
    Bitmap arcRect = makeArcRect(mCircleAttribute.mRectBorder, arcBitmap, grayCover);
    mCircleAttribute.mArcPaint.setAlpha(mCircleAttribute.alpha);
    // 绘制最终效果
    canvas.drawBitmap(arcRect, 0, 0, mCircleAttribute.mArcPaint);
  }

  /**
   * 根据区域大小绘制圆角矩形
   * 
   * @param rectf
   *          覆盖区域
   * @return 覆盖层
   */
  private Bitmap makeGrayCover(RectF rectf) {
    Bitmap bm = Bitmap.createBitmap((int) rectf.width(), (int) rectf.height(), Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(bm);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(mCircleAttribute.coverColor);
    c.drawRoundRect(rectf, 10, 10, paint);
    return bm;
  }

  /**
   * 根据矩形区域画空闲圆环加扇形
   * 
   * @param rectf
   *          画内切圆区域
   * @param startAngle
   *          起始角度
   * @param sweep
   *          结束角度
   * @return
   */
  private Bitmap makeArcCover(RectF rectf, float startAngle, float sweep) {
    Bitmap bm = Bitmap.createBitmap((int) rectf.width(), (int) rectf.height(), Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(bm);
    // 计算圆半径
    float r = (Math.min(rectf.width(), rectf.height()) - mCircleAttribute.widthBorder) / 3;
    // 以区域的中心点为圆心左标，即圆心（center_X，center_Y）
    float center_X = rectf.centerX();
    float center_Y = rectf.centerY();
    // 根据圆心和半径计算外切矩形大小
    RectF newRectf = new RectF(center_X - r, center_Y - r, center_X + r, center_Y + r);
    if (isDownloading) {
      // 画扇形路径
      path.reset();
      // 确定路径圆心
      path.moveTo(center_X, center_Y);
      // 画扇形起始边
      path.lineTo((float) (center_X + r * Math.cos(startAngle * Math.PI / 180)), // 起始点角度在圆上对应的横坐标
          (float) (center_Y + r * Math.sin(startAngle * Math.PI / 180))); // 起始点角度在圆上对应的纵坐标
      // 扇形结束的边
      path.lineTo((float) (center_X + r * Math.cos((startAngle + sweep) * Math.PI / 180)), // 终点角度在圆上对应的横坐标
          (float) (center_Y + r * Math.sin((startAngle + sweep) * Math.PI / 180))); // 终点点角度在圆上对应的纵坐标
      path.close();
      // 根据区域添加完整扇形路径
      path.addArc(newRectf, startAngle, sweep);
      // 画矩形内切扇形
      c.drawPath(path, paint);
    } else {
      paint.setColor(mCircleAttribute.coverColor);
      paint.setStyle(Paint.Style.STROKE);
      // 设置圆环画笔宽度
      paint.setStrokeWidth(r / 3);
      c.drawArc(newRectf, startAngle, sweep, false, paint);
      paint.setStyle(Paint.Style.FILL);
      c.drawRect(center_X - 7 * r / 20, center_Y - 7 * r / 20, center_X - 7 * r / 60, center_Y + 7 * r / 20, paint);
      c.drawRect(center_X + 7 * r / 60, center_Y - 7 * r / 20, center_X + 7 * r / 20, center_Y + 7 * r / 20, paint);
    }
    paint.setColor(mCircleAttribute.coverColor);
    paint.setStyle(Paint.Style.STROKE);
    // 设置圆环画笔宽度
    paint.setStrokeWidth(mCircleAttribute.mArcWidth);
    // 画圆环
    c.drawCircle(center_X, center_Y, r, paint);
    return bm;
  }

  /**
   * 根据覆盖层与（中心圆环+扇形区）取交集显示
   * 
   * @param rectf
   *          区域
   * @param arcBitmap
   *          圆环加扇形区域
   * @param grayCover
   *          覆盖层
   * @return 半透明的覆盖层
   */
  private Bitmap makeArcRect(RectF rectf, Bitmap arcBitmap, Bitmap grayCover) {
    Bitmap bm = Bitmap.createBitmap((int) grayCover.getWidth(), (int) grayCover.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bm);
    int sc = canvas.saveLayer(rectf, null, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
        | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
    Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    canvas.drawBitmap(arcBitmap, 0, 0, p);
    // 图层异或模式
    p.setXfermode(mode);
    canvas.drawBitmap(grayCover, 0, 0, p);
    p.setXfermode(null);
    canvas.restoreToCount(sc);
    return bm;
  }

  public Drawable getBackgroundPicture() {
    return mBackgroundPicture;
  }

  public void setBackgroundPicture(Drawable mBackgroundPicture) {
    this.mBackgroundPicture = mBackgroundPicture;
  }

  /**
   * 设置进度值
   */
  public synchronized void setProgress(int progress) {
    mCurProgress = progress;
    if (mCurProgress < 0) {
      mCurProgress = 0;
    }
    if (mCurProgress > mMaxProgress) {
      mCurProgress = mMaxProgress;
    }
    invalidate();
  }

  /**
   * 获取进度值
   */
  public synchronized int getProgress() {
    return mCurProgress;
  }

  class CircleAttribute {
    public RectF mRectBorder; // 圆形所在矩形区域
    public Paint mArcPaint;  // 内部扇形画笔
    public int   mDrawPos;   // 绘制圆形的起点（默认为-90度即12点钟方向）
    public float mArcWidth;  // 圆形宽度
    public int   coverColor; // 覆盖层颜色
    public float widthBorder; // 内边缘宽度
    public int   maxProgress; // 进度最大值
    public int   alpha;      // 透明度

    public CircleAttribute() {
      mRectBorder = new RectF();
      mArcWidth = 5;
      mDrawPos = -90;
      mArcPaint = new Paint();
      mArcPaint.setAntiAlias(true);
      mArcPaint.setStyle(Paint.Style.FILL);
      alpha = DEFAULT_ALPHA;
      coverColor = DEFAULT_PAINT_COLOR;
      widthBorder = DEFAULT_WIDTH_BORDER;
      maxProgress = DEFAULT_MAX_PROGRESS_VALUE;
      mArcWidth = DEFAULT_ARC_BORDER_WIDTH;
    }

    /*
     * 自动修正
     */
    public void autoFix(int w, int h) {
      int sl = getPaddingLeft();
      int sr = getPaddingRight();
      int st = getPaddingTop();
      int sb = getPaddingBottom();
      mRectBorder.set(sl, st, w - sr, h - sb);
    }
  }
}
