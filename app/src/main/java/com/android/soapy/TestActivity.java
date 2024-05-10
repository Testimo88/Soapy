package com.android.soapy;


import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;


import com.android.soapy.base.BaseActivity;
import com.android.soapy.databinding.ActivityTestBinding;
import com.android.soapy.vm.BaseViewModel;


public class TestActivity extends BaseActivity<ActivityTestBinding, BaseViewModel> {
    @Override
    protected ActivityTestBinding inflateViewBinding(LayoutInflater layoutInflater) {
        return ActivityTestBinding.inflate(layoutInflater);
    }

    private static String TAG = "TestActivity";

    private boolean[] buttonStates = new boolean[7]; // 记录7个按钮的状态
    private boolean pauseState = false; // 记录暂停键的状态
    private int lastOpenButtonIndex = -1; // 记录上一个打开的按钮索引

    @Override
    public void initView() {
        super.initView();
        // 初始化按钮
        Button[] buttons = new Button[7];
        buttons[0] = binding.button1;
        buttons[1] = binding.button2;
        buttons[2] = binding.button3;
        buttons[3] = binding.button4;
        buttons[4] = binding.button5;
        buttons[5] = binding.button6;
        buttons[6] = binding.button7;

        Button pauseButton = binding.pauseButton;

        // 设置按钮默认背景颜色为绿色
        for (Button button : buttons) {
            button.setBackgroundColor(Color.GREEN);
        }

        // 设置按钮点击事件
        for (int i = 0; i < 7; i++) {
            int finalI = i;
            buttons[i].setOnClickListener(v -> {
                // 先关闭所有按钮
                closeAllButtons(buttons);
                // 打开当前按钮
                buttonStates[finalI] = true;
                buttons[finalI].setBackgroundColor(Color.RED);
                lastOpenButtonIndex = finalI;
                sendBinaryData(); // 发送二进制数据
            });
        }

        // 设置暂停键点击事件
        pauseButton.setOnClickListener(v -> {
            if (!pauseState) {
                // 如果上次有按钮是开的，则打开上次开的按钮
                if (lastOpenButtonIndex != -1) {
                    buttonStates[lastOpenButtonIndex] = true;
                    buttons[lastOpenButtonIndex].setBackgroundColor(Color.RED);
                } else {
                    // 否则关闭所有按钮
                    closeAllButtons(buttons);
                }
                pauseState = true;
            } else {
                // 关闭所有按钮
                closeAllButtons(buttons);
                pauseState = false;
            }
            sendBinaryData(); // 发送二进制数据
        });
    }

    // 关闭所有按钮
    private void closeAllButtons(Button[] buttons) {
        for (int i = 0; i < 7; i++) {
            buttonStates[i] = false;
            buttons[i].setBackgroundColor(Color.GREEN);
        }
        pauseState = false;
    }

    // 发送二进制数据
    private void sendBinaryData() {
        int data = 0;
        // 将按钮状态转换为二进制数据
        for (int i = 0; i < buttonStates.length; i++) {
            if (buttonStates[i]) {
                data |= (1 << i); // 将第i位设为1
            }
        }
        // 如果暂停键按下，则将最高位设为1
//        if (pauseState) {
//            data |= (1 << 7);
//        }
        // 这里写发送数据的逻辑，发送data即可
        Log.d(TAG, "sendBinaryData: " + data);
        convertToBinary(data);
    }

    public String convertToBinary(int number) {
        StringBuilder binaryBuilder = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            binaryBuilder.append((number & (1 << i)) == 0 ? '0' : '1');
            if (i < 7) {
                binaryBuilder.append(' ');
            }
        }
        Log.d(TAG, "convertToBinary: " + binaryBuilder);
        return binaryBuilder.toString();
    }

}
