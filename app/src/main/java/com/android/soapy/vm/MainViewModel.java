package com.android.soapy.vm;

import static com.android.soapy.utils.Constants.UNIT_PRICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;


import com.android.soapy.modbus.ModbusManager;
import com.android.soapy.utils.ByteUtil;
import com.android.soapy.utils.FragmentUtils;
import com.android.soapy.utils.SPUtils;
import com.android.soapy.weight.CountDownTimerExt;
import com.licheedev.modbus4android.ModbusCallback;
import com.licheedev.modbus4android.param.SerialParam;
import com.lztek.toolkit.Lztek;
import com.lztek.toolkit.SerialPort;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;
import com.serotonin.modbus4j.msg.WriteRegisterResponse;

import cc.uling.usdk.USDK;
import cc.uling.usdk.board.UBoard;
import cc.uling.usdk.board.mdb.para.HCReplyPara;
import cc.uling.usdk.board.mdb.para.IPReplyPara;
import cc.uling.usdk.board.mdb.para.MPReplyPara;
import cc.uling.usdk.board.mdb.para.PMReplyPara;
import cc.uling.usdk.board.mdb.para.PayReplyPara;


public class MainViewModel extends BaseViewModel {

    public static String TAG = "MainViewModel";
    /**
     * 时长
     */
    public MutableLiveData<Long> timeLong = new MutableLiveData<>();
    /**
     * 倒计时
     */
    public MutableLiveData<String> countDown = new MutableLiveData<>();
    /**
     * 倒计时结束
     */
    public MutableLiveData<Boolean> timerFinish = new MutableLiveData<>(false);
    /**
     * 倒计时结束
     */
    public MutableLiveData<Boolean> lastTimerFinish = new MutableLiveData<>(false);

    /**
     * 支付超时
     */
    public MutableLiveData<Boolean> sensitive = new MutableLiveData<>(false);

    /**
     * 支付结果
     */
    public MutableLiveData<PMReplyPara> PMReplyPara = new MutableLiveData<>();
    /**
     * 完成（取消）收款通知
     */
    public MutableLiveData<PayReplyPara> PayReplyPara = new MutableLiveData<>();
    /**
     * 底部显示金额
     */
    public MutableLiveData<String> amountText = new MutableLiveData<>();
    /**
     * 最后一个页面累计金额
     */
    public MutableLiveData<String> accumulatedAmount = new MutableLiveData<>();
    /**
     * 功能倒计时
     */
    public MutableLiveData<String> timerDown = new MutableLiveData<>();
    /**
     * 打开工控机串口 true 打开成功  false 打开失败
     */
    public MutableLiveData<Boolean> openSerialPort = new MutableLiveData<>(false);
    private final Handler handler = new Handler();
    public CountDownTimerExt countDownTimer;
    public CountDownTimerExt countDownTimerLast;

    public MutableLiveData<Boolean> pauseTimer = new MutableLiveData<>(true);
    private SerialPort mSerialPort;
    public long millisUntilFinishedRecord;
    public int totalMoney;

    /**
     * 页面跳转
     *
     * @param fragment     当前页面
     * @param id           填充到xml的id
     * @param targFragment 目标页面
     */
    public void jumpToNext(Fragment fragment, int id, Fragment targFragment) {
        FragmentUtils.replaceFragment(fragment.requireActivity(), id, targFragment);
    }

    /**
     * 开始倒计时
     */
    public void startCountDownTimer(long time) {
        countDownTimer = new CountDownTimerExt(time, 1000) {
            @Override
            public void onTimerTick(long millisUntilFinished) {
                long l = millisUntilFinished / 1000;
                Log.d(TAG, "onTick_cal:" + l);
                millisUntilFinishedRecord = millisUntilFinished;
                countDown.postValue(formatTime(millisUntilFinished));
                if (l == 0) {
                    timerFinish.postValue(true);

                }
            }

            @Override
            public void onTimerFinish() {

            }
        };
        countDownTimer.start();
    }

    /**
     * 结束画面倒计时
     */
    public void startLastCountDownTimer(long time) {
        countDownTimerLast = new CountDownTimerExt(time, 1000) {
            @Override
            public void onTimerTick(long millisUntilFinished) {
                long l = millisUntilFinished / 1000;
                Log.d(TAG, "onTick_cal:" + l);
                if (l == 1) {
                    lastTimerFinish.postValue(true);
                }
            }

            @Override
            public void onTimerFinish() {

            }
        };
        countDownTimerLast.start();
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        return hours == 0 ? String.format("%02d:%02d", minutes, seconds) : String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * 暂停倒计时 pause true 暂停  false 继续倒计时
     */
    public void onPauseTimer(boolean pause) {
        if (pause) {
            pauseTimer.postValue(false);
            countDownTimer.pause();
        } else {
            pauseTimer.postValue(true);
            countDownTimer.resume();
        }
    }

    /**
     * 传入时间计算金额
     *
     * @param min
     */
    public void calMoney(int min) {
        sensitive.setValue(false);
        //单价
        String unit_price = (String) SPUtils.get(context, UNIT_PRICE, "1");
        int money = min * Integer.parseInt(unit_price);
        timeLong.postValue(Long.valueOf(min * 60 * 1000L));
        amountText.postValue(String.valueOf(money));
    }

    /**
     * 计算Sensitive金额
     */
    public void calSensitiveMoney(boolean isAdd) {
        sensitive.postValue(isAdd);
        String value = amountText.getValue();
        int i = Integer.parseInt(value);
        if (isAdd) {
            amountText.postValue(String.valueOf(i + 2));
        } else {
            amountText.postValue(String.valueOf(i - 2));

        }
    }

    /**
     * 支付串口通讯
     */
    public UBoard getUBoard() {
        UBoard uBoard = USDK.getInstance().create("/dev/ttyS4");
        return uBoard;
    }

    /**
     * 打开支付串口
     */
    public void openEfDev() {
        getUBoard().EF_OpenDev("/dev/ttyS4", 9600);
    }

    /**
     * 读取硬件配置 Board. readHardwareConfig (HCReplyPara)
     */
    public void readHardwareConfig() {
        HCReplyPara hcReplyPara = new HCReplyPara();
        getUBoard().readHardwareConfig(hcReplyPara);
        Log.d(TAG, "readHardwareConfig: " + hcReplyPara);
    }

    /**
     * 获取外设支持最小面额
     */
    public void getMinPayoutAmount() {
        MPReplyPara mpReplyPara = new MPReplyPara();
        getUBoard().getMinPayoutAmount(mpReplyPara);
        Log.d(TAG, "getMinPayoutAmount: " + mpReplyPara);
    }

    /**
     * 发起收款
     */
    public void preparePayment(String money) {
        //累加金额
        totalMoney += Integer.parseInt(money);
        accumulatedAmount.postValue(String.valueOf(totalMoney));
        IPReplyPara ipReplyPara = new IPReplyPara((short) 1, Integer.parseInt(money) * 100);
        getUBoard().initPayment(ipReplyPara);
        Log.d(TAG, "preparePayment: " + ipReplyPara);
    }

    /**
     * 查询 MDB 收款金额
     */
    public void getPayAmount() {
        PMReplyPara pmReplyPara = new PMReplyPara();
        getUBoard().getPayAmount(pmReplyPara);
        Log.d(TAG, "getPayAmount: " + pmReplyPara);
        if (pmReplyPara.isOK()) {
            PMReplyPara.postValue(pmReplyPara);
        }
    }

    /**
     * 完成（取消）收款通知
     */
    public void notifyPayment(boolean flag) {

        PayReplyPara payReplyPara = new PayReplyPara(flag);
        getUBoard().notifyPayment(payReplyPara);
        Log.d(TAG, "notifyPayment: " + payReplyPara);
        if (payReplyPara.isOK()) {
            PayReplyPara.postValue(payReplyPara);
        }
    }

    /**
     * 开始轮询  查询 MDB 收款金额
     */
    public void startPolling() {
        // 使用Handler实现轮询
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getPayAmount();
                // 继续轮询
                handler.postDelayed(this, 2000); // 5000毫秒为轮询间隔
            }
        }, 1000); // 0毫秒表示立即执行第一次轮询
    }

    /**
     * 停止轮询
     */
    public void stopPolling() {
        getUBoard().notifyPayment(new PayReplyPara(true));
        handler.removeCallbacksAndMessages(null);
    }


    /**
     * 初始化Modbus串口参数
     */
    public void initSerialParam() {
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

                Log.d(TAG, "onSuccess: ModbusMaster:打开成功");

            }

            @Override
            public void onFailure(Throwable tr) {
                Log.d(TAG, "onFailure: " + tr.getMessage());

            }

            @Override
            public void onFinally() {
                boolean modbusOpened = ModbusManager.get().isModbusOpened();
                Log.d(TAG, "modbusOpened: " + modbusOpened);
            }
        });
    }


    /**
     * 写入数据
     */
    public void writeSingleRegister(int data) {
        ModbusManager.get()
                .writeSingleRegister(1, 200, data,
                        new ModbusCallback<WriteRegisterResponse>() {
                            @Override
                            public void onSuccess(WriteRegisterResponse writeRegisterResponse) {
                                Log.d(TAG, "onSuccess: F06写入成功\n");
                            }

                            @Override
                            public void onFailure(Throwable tr) {
                                Log.i(TAG, "F06", tr);
                            }

                            @Override
                            public void onFinally() {

                            }
                        });
    }

    /**
     * 读取数据
     */
    public void readHoldingRegisters() {
        ModbusManager.get()
            .readHoldingRegisters(1, 200, 8,
                new ModbusCallback<ReadHoldingRegistersResponse>() {
                    @Override
                    public void onSuccess(
                        ReadHoldingRegistersResponse readHoldingRegistersResponse) {
                        byte[] data = readHoldingRegistersResponse.getData();
                        Log.d(TAG,"F03读取：" + ByteUtil.bytes2HexStr(data));
                    }

                    @Override
                    public void onFailure(Throwable tr) {
                        Log.d(TAG,"F03", tr);
                    }

                    @Override
                    public void onFinally() {

                    }
                });
    }

    /**
     * 打开工控机串口
     */
    public void openSerialPort(Context context) {
        mSerialPort = Lztek.create(context).openSerialPort("/dev/ttyS7", 9600);
        if (mSerialPort == null) {
            Log.d(TAG, "SerialPort: null" + "打开失败");
            openSerialPort.postValue(false);
        } else {
            Log.d(TAG, "SerialPort:" + mSerialPort.toString());
            openSerialPort.postValue(true);
        }

    }

    public int write(byte[] data) {
        if (null == mSerialPort || null == data || data.length == 0) {
            return -1;
        }
        Log.i(TAG, "write:" + data.length + " Content:" + new String(data));
        java.io.OutputStream output = null;

        try {
            output = mSerialPort.getOutputStream();
            output.write(data);
            return data.length;
        } catch (Exception e) {
            Log.d("#ERROR#", "[COM]Write Faild: " + e.getMessage(), e);
            return -1;
        }

    }


    public byte[] read() {
        if (null == mSerialPort) {
            return null;
        }

        java.io.InputStream input = null;
        try {
            input = mSerialPort.getInputStream();

            byte[] buffer = new byte[1024];
            int len = input.read(buffer);
            // return len > 0? new String(buffer, 0, len) : "";
            return len > 0 ? java.util.Arrays.copyOfRange(buffer, 0, len) : null;
        } catch (Exception e) {
            Log.d("#ERROR#", "[COM]Read Faild: " + e.getMessage(), e);
            return null;
        }
    }


}
