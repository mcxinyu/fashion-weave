package com.github.mcxinyu.weavelib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * 简单的单线条编织 view
 * <p>
 * Created by huangyuefeng on 2019/4/3.
 * Contact me : mcxinyu@foxmail.com
 */
@Keep
public class SimpleWeaveView extends View {
    private static final String TAG = SimpleWeaveView.class.getSimpleName();

    private static final int COMPLETE_CIRCLE_DEGREES = 360;
    private static final int HALF_CIRCLE_DEGREES = 180;

    /**
     * 背景颜色
     */
    @ColorInt
    private int mBackgroundColor;
    /**
     * 线条颜色
     */
    @ColorInt
    private int mLineColor;
    /**
     * 线条角度(px)
     */
    private float mLineDegrees;
    /**
     * 线条宽度(px)
     */
    @Px
    private int mLineWidth;
    /**
     * 线条间距(px)
     */
    @Px
    private int mLineGap;
    /**
     * 整个 view 的裁剪圆角半径
     */
    @Px
    private int mClipRadius;

    private Paint mPaintLine;
    private Paint mPaintBg;

    /**
     * 裁剪圆角
     */
    private Path mClipPath;

    private int mWidth;
    private int mHeight;
    private RectF mContentRect;
    private Path mLinePath;

    public SimpleWeaveView(Context context) {
        this(context, null);
    }

    public SimpleWeaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleWeaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleWeaveView, defStyleAttr, 0);
        mLineColor = typedArray.getColor(R.styleable.SimpleWeaveView_swv_lineColor, ContextCompat.getColor(getContext(), R.color.color_mask));
        mBackgroundColor = typedArray.getColor(R.styleable.SimpleWeaveView_swv_backgroundColor, -1);
        mLineDegrees = typedArray.getFloat(R.styleable.SimpleWeaveView_swv_lineDegrees, 0f);
        mLineWidth = typedArray.getDimensionPixelSize(R.styleable.SimpleWeaveView_swv_lineWidth, 8);
        mLineGap = typedArray.getDimensionPixelSize(R.styleable.SimpleWeaveView_swv_lineGap, 8);
        mClipRadius = typedArray.getLayoutDimension(R.styleable.SimpleWeaveView_swv_clipRadius, 0);

        if (mBackgroundColor == -1) {
            int alpha = Color.alpha(mLineColor);
            int newAlpha = Math.min(255, Math.max(0, (int) (alpha * 0.2))) << 24;
            int rgb = 0x00ffffff & mLineColor;
            mBackgroundColor = newAlpha + rgb;
        }

        typedArray.recycle();

        initView();
    }

    private void initView() {
        mPaintBg = new Paint();
        mPaintBg.setAntiAlias(true);

        mPaintLine = new Paint();
        mPaintLine.setAntiAlias(true);

        mContentRect = new RectF();

        mClipPath = new Path();
        mLinePath = new Path();

        attrInvalidate();
    }

    private void attrInvalidate() {
        mPaintBg.setColor(mBackgroundColor);
        mPaintLine.setColor(mLineColor);

        mClipPath.reset();
        if (mClipRadius <= -1) {
            if (mWidth > mHeight) {
                mClipPath.addRoundRect(mContentRect, mHeight / 2f, mHeight / 2f, Path.Direction.CW);
            } else {
                mClipPath.addRoundRect(mContentRect, mWidth / 2f, mWidth / 2f, Path.Direction.CW);
            }
        } else {
            mClipPath.addRoundRect(mContentRect, mClipRadius, mClipRadius, Path.Direction.CW);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mContentRect.set(0, 0, w, h);
        attrInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        //裁剪
        canvas.clipPath(mClipPath);
        //背景
        canvas.drawRoundRect(mContentRect, mClipRadius, mClipRadius, mPaintBg);

        //移动画布中心点
        canvas.translate(mWidth / 2f, mHeight / 2f);

        //旋转角度，减 90° 模拟时钟角度
        canvas.rotate(mLineDegrees - 90);

        //region 取 view 的外切圆的外切矩形做绘画区域，可参考图解 https://raw.githubusercontent.com/mcxinyu/fashion-weave/42532dee90b3fe608811d39f237d69a5401565b3/art/20190409164548.png
        //计算 view 外切圆半径
        int excisionCircleRadius = (int) Math.ceil((Math.sqrt(Math.pow(mWidth, 2) + Math.pow(mHeight, 2))) / 2d);

        //画线
        for (int pHeight = -excisionCircleRadius; pHeight <= excisionCircleRadius * 2; pHeight += mLineWidth + mLineGap) {
            mLinePath.reset();
            mLinePath.moveTo(-excisionCircleRadius, pHeight);
            mLinePath.lineTo(excisionCircleRadius, pHeight);
            mLinePath.lineTo(excisionCircleRadius, pHeight + mLineWidth);
            mLinePath.lineTo(-excisionCircleRadius, pHeight + mLineWidth);
            mLinePath.close();
            canvas.drawPath(mLinePath, mPaintLine);
        }
        //endregion

        canvas.restore();
    }

    public int getViewBackgroundColor() {
        return mBackgroundColor;
    }

    public void setViewBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        attrInvalidate();
        invalidate();
    }

    public int getLineColor() {
        return mLineColor;
    }

    public void setLineColor(int lineColor) {
        mLineColor = lineColor;
        attrInvalidate();
        invalidate();
    }

    public float getLineDegrees() {
        return mLineDegrees;
    }

    public void setLineDegrees(float lineDegrees) {
        mLineDegrees = lineDegrees;
        invalidate();
    }

    public int getLineWidth() {
        return mLineWidth;
    }

    public void setLineWidth(int lineWidth) {
        mLineWidth = lineWidth;
        invalidate();
    }

    public int getLineGap() {
        return mLineGap;
    }

    public void setLineGap(int lineGap) {
        mLineGap = lineGap;
        invalidate();
    }

    public int getClipRadius() {
        return mClipRadius;
    }

    public void setClipRadius(int clipRadius) {
        mClipRadius = clipRadius;
        attrInvalidate();
        invalidate();
    }
}
