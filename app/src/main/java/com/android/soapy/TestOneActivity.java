package com.android.soapy;


import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.android.soapy.base.BaseActivity;
import com.android.soapy.databinding.ActivityTestBinding;
import com.android.soapy.modbus.ModbusManager;
import com.android.soapy.utils.ByteUtil;
import com.android.soapy.vm.MainViewModel;
import com.licheedev.modbus4android.ModbusCallback;
import com.licheedev.modbus4android.ModbusObserver;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;
import com.serotonin.modbus4j.msg.WriteRegisterResponse;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.android.schedulers.AndroidSchedulers;


public class TestOneActivity extends BaseActivity<ActivityTestBinding, MainViewModel> {
    @Override
    protected ActivityTestBinding inflateViewBinding(LayoutInflater layoutInflater) {
        return ActivityTestBinding.inflate(layoutInflater);
    }

    private static String TAG = "TestActivity";
    private Button[] buttons;
    private Button pauseButton;

    private boolean[] buttonStates = new boolean[7]; // 按钮状态，true代表打开，false代表关闭

    private boolean paused = false; // 暂停状态
    private int lastOpenButtonIndex = -1; // 记录上一个打开的按钮索引

    @Override
    public void initView() {
        super.initView();
        viewModel.openSerialPort(this);
        // 初始化按钮
        buttons = new Button[7];
        buttons[0] = binding.button1;
        buttons[1] = binding.button2;
        buttons[2] = binding.button3;
        buttons[3] = binding.button4;
        buttons[4] = binding.button5;
        buttons[5] = binding.button6;
        buttons[6] = binding.button7;
        pauseButton = binding.pauseButton;

        // 设置按钮默认背景颜色为绿色
        for (Button button : buttons) {
            button.setBackgroundColor(Color.GREEN);
        }
        // 初始化按钮
        for (int i = 0; i < 7; i++) {
            final int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleButton(finalI);
                    lastOpenButtonIndex = finalI;
                    sendBinaryData();
                }
            });
        }
        // 初始化暂停按钮
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePause();
                sendBinaryData();
            }
        });
//        byte[] read = viewModel.read();
//        if (read != null) {
//            Log.d(TAG, "read: " + new String(read));
//        }

    }

    private void toggleButton(int index) {
//        if (!paused) {
        // 切换按钮状态
        buttonStates[index] = !buttonStates[index];
        buttons[index].setBackgroundColor(buttonStates[index] ? Color.RED : Color.GREEN);
        // 关闭其他按钮
        for (int i = 0; i < 7; i++) {
            if (i != index) {
                buttonStates[i] = false;
                buttons[i].setBackgroundColor(Color.GREEN);
            }
        }
//        }
    }

    private void togglePause() {
        if (paused) {
            // 关闭所有按钮
            // 如果上次有按钮是开的，则打开上次开的按钮
            if (lastOpenButtonIndex != -1) {
                buttonStates[lastOpenButtonIndex] = true;
                buttons[lastOpenButtonIndex].setBackgroundColor(Color.RED);
            } else {
                // 否则关闭所有按钮
                closeAllButtons(buttons);
            }
            paused = false;
        } else {
            // 关闭所有按钮
            closeAllButtons(buttons);
            paused = true;
        }
    }

    // 关闭所有按钮
    private void closeAllButtons(Button[] buttons) {
        for (int i = 0; i < 7; i++) {
            buttonStates[i] = false;
            buttons[i].setBackgroundColor(Color.GREEN);
        }
        paused = false;
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
        // 这里写发送数据的逻辑，发送data即可
        Log.d(TAG, "sendBinaryData: " + data);
//        convertToBinary(data);
//        int write = viewModel.write(convertToBinary(data).getBytes());
//        Log.d(TAG, "write: " + write);

        //// 普通写法
//        ModbusManager.get()
//            .readHoldingRegisters(1, 64, 1,
//                new ModbusCallback<ReadHoldingRegistersResponse>() {
//                    @Override
//                    public void onSuccess(
//                        ReadHoldingRegistersResponse readHoldingRegistersResponse) {
//                        byte[] data = readHoldingRegistersResponse.getData();
//                        Log.d(TAG, "onSuccess: F03读取：" + ByteUtil.bytes2HexStr(data) + "\n");
//                    }
//
//                    @Override
//                    public void onFailure(Throwable tr) {
//                        Log.i(TAG,"F03", tr);
//                    }
//
//                    @Override
//                    public void onFinally() {
//
//                    }
//                });
        viewModel.writeSingleRegister(data);
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
