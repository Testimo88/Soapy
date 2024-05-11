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
        viewModel.readHardwareConfig();
        viewModel.getMinPayoutAmount();


        viewModel.initSerialParam();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ModbusManager.get().isModbusOpened()) {
            // 关闭设备
            ModbusManager.get().closeModbusMaster();
        }
        ModbusManager.get().release();
    }

}
