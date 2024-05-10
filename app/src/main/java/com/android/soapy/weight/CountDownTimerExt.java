package com.android.soapy.weight;

import android.os.CountDownTimer;
import android.util.Log;

public abstract class CountDownTimerExt {
    private static final String TAG = "CountDownTimerExt";

    /**
     * 倒计时实现类
     */
    private CountDownTimer countDownTimer;
    /**
     * 间隔
     */
    private long mInterval;
    private boolean isTimerPaused;
    private long millisInFuture;
    private long remainingTime;

    /**
     * @param millisInFutureIn 总时长
     * @param interval onTick间隔
     */
    public CountDownTimerExt(long millisInFutureIn, long interval) {
        mInterval = interval;
        isTimerPaused = true;
        millisInFuture = millisInFutureIn;
        remainingTime = millisInFutureIn;
    }

    public final boolean isTimerPaused() {
        return isTimerPaused;
    }

    public final void setTimerPaused(boolean value) {
        isTimerPaused = value;
    }

    public final long getMillisInFuture() {
        return millisInFuture;
    }

    public final void setMillisInFuture(long value) {
        millisInFuture = value;
    }

    public final long getRemainingTime() {
        return remainingTime;
    }

    public final void setRemainingTime(long value) {
        remainingTime = value;
    }

    public final void start() {
        startIt(remainingTime, mInterval);
    }

    public final void start(long millisInFutureIn, long remainingTimeIn, long interval) {
        millisInFuture = millisInFutureIn;
        remainingTime = remainingTimeIn;
        mInterval = interval;
        start();
    }

    public final synchronized void startIt(long millisInFutureIn, long interval) {
        remainingTime = millisInFutureIn;
        mInterval = interval;
        if (millisInFuture > 0L && interval > 0L) {
            if (!isTimerPaused) {
                // 有运行中的先stop
                stop();
            }

            if (isTimerPaused) {
                // 有暂停或未运行的，创建CountDownTimer实现, 确保运行在主线程
                countDownTimer = (CountDownTimer)(new CountDownTimer(getRemainingTime(), mInterval) {
                    @Override
                    public void onFinish() {
                        onTimerFinish();
                        stop();
                    }

                    @Override
                    public void onTick(long millisUntilFinished) {
                        // 这里会记录剩余的时长保存到remainingTime, 方便暂停后能再恢复
                        setRemainingTime(millisUntilFinished);
                        onTimerTick(millisUntilFinished);
                    }
                });
                try {
                    countDownTimer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isTimerPaused = false;
            } else {
                Log.d(TAG, "ignore start");
            }

        } else {
            Log.d(TAG, "invalid parameter");
        }
    }

    /**
     * 停止
     */
    public final void stop() {
        try {
            countDownTimer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        isTimerPaused = true;
        remainingTime = millisInFuture;
    }

    /**
     * 暂停
     */
    public final void pause() {
        if (!isTimerPaused) {
            try {
                countDownTimer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isTimerPaused = true;
        }
    }

    /**
     * 恢复
     */
    public final void resume() {
        if (!isRunning()) {
            // 通过remainingTime得到剩余的时长
            startIt(remainingTime, mInterval);
        }
    }

    /**
     * 运行中
     * @return
     */
    public final boolean isRunning() {
        return !isTimerPaused;
    }

    /**
     * 每次触发倒计时回调
     * @param value
     */
    public abstract void onTimerTick(long value);

    /**
     * 倒计时完成回调
     */
    public abstract void onTimerFinish();

}
