package com.android.soapy;

import android.app.Application;


import com.serotonin.modbus4j.ModbusConfig;

import cc.uling.usdk.USDK;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        USDK.getInstance().init(this);
//        CrashReport.initCrashReport(getApplicationContext(), "1cd8e929eb", false);
        configModbus();
    }

    /**
     * 配置Modbus,可选
     */
    private void configModbus() {
        // 启用rtu的crc校验（默认就启用）
        ModbusConfig.setEnableRtuCrc(true);
        // 打印数据log（默认全禁用）
        // System.out: MessagingControl.send: 01030000000305cb
        // System.out: MessagingConnection.read: 010306000100020000bd75
        ModbusConfig.setEnableDataLog(true, true);
    }
}
