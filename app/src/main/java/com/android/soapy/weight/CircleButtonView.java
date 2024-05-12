package com.android.soapy.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class CircleButtonView extends View {
    private boolean isVisible = true;
    private boolean isBlinking = false;
    private Paint paint;
    private Handler handler;
    private final int INTERVAL_MS = 500; // 闪烁时间间隔

    public CircleButtonView(Context context) {
        super(context);
        init();
    }

    public CircleButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED); // 默认为红色
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        handler = new Handler();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isVisible) {
            int width = getWidth();
            int height = getHeight();
            int radius = Math.min(width, height) / 2;
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        }
    }

    public void setColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    public void toggle() {
        isVisible = !isVisible;
        invalidate();
    }

    public void startBlinking() {
        if (!isBlinking) {
            isBlinking = true;
            handler.post(blinkRunnable);
        }
    }

    public void stopBlinking() {
        if (isBlinking) {
            isBlinking = false;
            handler.removeCallbacks(blinkRunnable);
            isVisible = true; // 确保最终显示按钮
            invalidate();
        }
    }

    private Runnable blinkRunnable = new Runnable() {
        @Override
        public void run() {
            isVisible = !isVisible;
            invalidate();
            handler.postDelayed(this, INTERVAL_MS);
        }
    };
}
