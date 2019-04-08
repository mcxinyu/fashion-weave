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
    public Path mClipPath;

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

        if (mClipRadius == -1) {
            if (mWidth > mHeight) {
                mClipPath.addRoundRect(mContentRect, mHeight / 2, mHeight / 2, Path.Direction.CW);
            } else {
                mClipPath.addRoundRect(mContentRect, mWidth / 2, mWidth / 2, Path.Direction.CW);
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        //裁剪
        canvas.clipPath(mClipPath);
        //背景
        canvas.drawRoundRect(mContentRect, mClipRadius, mClipRadius, mPaintBg);
        //旋转角度
        canvas.rotate(mLineDegrees - 90);

        //取 view 的斜边做半径，并对改半径形成的圆取外切圆的矩形做绘画区域
        int measureWidth = (int) Math.ceil(Math.sqrt(Math.pow(mWidth, 2) + Math.pow(mHeight, 2)));

        //画线
        for (int pHeight = -measureWidth; pHeight <= measureWidth * 2; pHeight += mLineWidth + mLineGap) {
            mLinePath.reset();
            mLinePath.moveTo(-measureWidth, pHeight);
            mLinePath.lineTo(measureWidth, pHeight);
            mLinePath.lineTo(measureWidth, pHeight + mLineWidth);
            mLinePath.lineTo(-measureWidth, pHeight + mLineWidth);
            mLinePath.close();
            canvas.drawPath(mLinePath, mPaintLine);
        }

        canvas.restore();
    }
}
