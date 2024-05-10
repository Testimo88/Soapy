package com.android.soapy.weight;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.VideoView;

public class FullScreenVideoView extends VideoView {
    private int videoWidth;
    private int videoHeight;
    ////常量 在这里定义你需要的比例
    public static final String FULL_SCREEN = "full_screen";
    public static final String SIXTEEN_TO_NINE = "16:9";
    public static final String FOUR_TO_THREE = "4:3";
    public static final String ORIGIN_VIEW = "1:1";

    public FullScreenVideoView(Context context) {
        super(context);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(videoWidth, widthMeasureSpec);
        int height = getDefaultSize(videoHeight, heightMeasureSpec);
        if (videoWidth > 0 && videoHeight > 0) {
            if (videoWidth * height > width * videoHeight) {
                height = width * videoHeight / videoWidth;
            } else if (videoWidth * height < width * videoHeight) {
                width = height * videoWidth / videoHeight;
            }
        }
        setMeasuredDimension(width, height);
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

//
//    //变量 我这里写的默认比例1:1
//    private String mRatio = ORIGIN_VIEW;
//
//    //重写onMeasure方法
//    //setMeasuredDimension(),切换view的大小
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        switch (mRatio) {
//            //满屏 获取设备的宽高直接设置
//            case FULL_SCREEN:
//                int screenWidth = getScreenRatio()[0];
//                int screenHigh = getScreenRatio()[1];
//                setMeasuredDimension(screenWidth, screenHigh);
//                break;
//
//            //16:9
//            case SIXTEEN_TO_NINE:
//                Integer[] SixteenToNine = getVideoRatio(16, 9);
//                setMeasuredDimension(SixteenToNine[0], SixteenToNine[1]);
//                break;
//
//            //4:3
//            case FOUR_TO_THREE:
//                Integer[] FourToThree = getVideoRatio(4, 3);
//                setMeasuredDimension(FourToThree[0], FourToThree[1]);
//                break;
//
//            //..别的比例可以自定义
//
//            //默认视频原大小
//            default:
//                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//                break;
//        }
//    }
//
//    //fragment传入当前选择的比例
//    public void setScreenRatio(String ratio) {
//        this.mRatio = ratio;
//        requestLayout();
//    }
//
//    //根据传入比例换算view显示的大小 无论显示什么样的比例大小，视频的宽高有一边都需要贴边
//    private Integer[] getVideoRatio(double width, double height) {
//        int screenWidth = getScreenRatio()[0];
//        int screenHigh = getScreenRatio()[1];
//
//        // 计算要设置的视频宽度和高度
//        int videoWidth = (int) (screenHigh * width / height);
//        int videoHeight = screenHigh;
//
//        // 如果视频宽度超过屏幕宽度，则调整
//        if (videoWidth > screenWidth) {
//            videoHeight = (int) (screenWidth * height / width);
//            videoWidth = screenWidth;
//        }
//
//        return new Integer[]{videoWidth, videoHeight};
//    }
//
//    //获取当前设备屏幕宽高 返回的是数组
//    private Integer[] getScreenRatio() {
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        int screenWidth = dm.widthPixels;
//        int screenHigh = dm.heightPixels;
//        return new Integer[]{screenWidth, screenHigh};
//    }

}
