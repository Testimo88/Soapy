package com.android.soapy.weight;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.soapy.R;

public class BinaryControlView extends LinearLayout {
    private static String TAG = "BinaryControlView";
    private String binaryData = ""; // 存储二进制数据
    private String[] meanings; // 按钮含义数组

    public BinaryControlView(Context context) {
        super(context);
        init(context);
    }

    public BinaryControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BinaryControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL); // 水平方向排列
        setGravity(Gravity.CENTER_HORIZONTAL); // 设置水平居中
    }

    public void setBinaryData(String binaryData, String[] meanings) {
        this.binaryData = binaryData;
        this.meanings = meanings;
        updateUI();
    }

    private void updateUI() {
        removeAllViews(); // 清除之前的内容
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < binaryData.length(); i++) {
            char bit = binaryData.charAt(i);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.binary_control_view, this, false);
            CircleButtonView button = layout.findViewById(R.id.circleButton);
            TextView textView = layout.findViewById(R.id.buttonMeaning);

            if (bit == '1') {
                button.setColor(Color.RED); // 设置红色按钮
                button.startBlinking(); // 开始闪烁
            } else {
                button.setColor(Color.GREEN); // 设置绿色按钮
            }
            Log.d(TAG, "updateUI: " + meanings[i]);
            textView.setText(meanings[i]); // 设置按钮含义文本
            addView(layout);
        }
    }

}

