package com.android.soapy;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;


import androidx.annotation.Nullable;

import com.android.soapy.base.BaseActivity;
import com.android.soapy.databinding.ActivityMainBinding;
import com.android.soapy.modbus.ModbusManager;
import com.android.soapy.ui.HomeFragment;
import com.android.soapy.vm.MainViewModel;
import com.licheedev.modbus4android.ModbusCallback;
import com.licheedev.modbus4android.param.SerialParam;
import com.lztek.toolkit.Lztek;
import com.serotonin.modbus4j.ModbusMaster;


public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    private static String TAG = "MainActivity";

    @Override
    protected ActivityMainBinding inflateViewBinding(LayoutInflater layoutInflater) {
        return ActivityMainBinding.inflate(layoutInflater);
    }

    @Override
    public void initView() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.initView();
        getSupportFragmentManager().beginTransaction().replace(R.id.fr_container, new HomeFragment()).commitNow();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Lztek.create(this).showNavigationBar();
        viewModel.openEfDev();
        viewModel.openSerialPort(this);
        viewModel.readHardwareConfig();
        viewModel.getMinPayoutAmount();


        SerialParam param = SerialParam.create("/dev/ttyS7", 9600) // 串口地址和波特率
                .setDataBits(8) // 数据位
                .setParity(0) // 校验位
                .setStopBits(1) // 停止位
                .setTimeout(1000)
                .setRetries(0);// 不重试
        ModbusManager.get().closeModbusMaster(); // 先关闭一下
        ModbusManager.get().init(param, new ModbusCallback<ModbusMaster>() {
            @Override
            public void onSuccess(ModbusMaster modbusMaster) {

                Toast.makeText(context, "打开成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Throwable tr) {
                Log.d(TAG, "onFailure: "+tr.getMessage());
                Toast.makeText(context, "打开失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinally() {
//                updateDeviceSwitchButton();
            }
        });
        boolean modbusOpened = ModbusManager.get().isModbusOpened();
        Log.d(TAG, "modbusOpened: " + modbusOpened);
        if (modbusOpened) {
            Log.d(TAG, "modbusOpened: " + modbusOpened);
        }
    }


}
