package com.android.soapy.ui;


import static com.android.soapy.utils.Constants.BLOW;
import static com.android.soapy.utils.Constants.CONDITIONER;
import static com.android.soapy.utils.Constants.CONDITIONER_SENIOR;
import static com.android.soapy.utils.Constants.DISINFECT;
import static com.android.soapy.utils.Constants.PAUSE;
import static com.android.soapy.utils.Constants.RINSE;
import static com.android.soapy.utils.Constants.SHAMPOO;
import static com.android.soapy.utils.Constants.SHAMPOO_SENIOR;


import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.soapy.R;
import com.android.soapy.SettingsActivity;
import com.android.soapy.TestActivity;
import com.android.soapy.TestOneActivity;
import com.android.soapy.base.BaseFragment;
import com.android.soapy.databinding.DialogInputPwdBinding;
import com.android.soapy.databinding.FragmentHomeBinding;
import com.android.soapy.modbus.ModbusManager;
import com.android.soapy.utils.SPUtils;
import com.android.soapy.vm.MainViewModel;
import com.licheedev.modbus4android.ModbusCallback;
import com.licheedev.modbus4android.param.SerialParam;
import com.serotonin.modbus4j.ModbusMaster;


/**
 * Home page
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding, MainViewModel> {
    private static String TAG = "HomeFragment";
    private int clickCount = 0;
    private long lastClickTime = 0;
    private final long MAX_CLICK_INTERVAL = 1000; // 最大点击间隔，单位：毫秒

    @Override
    protected FragmentHomeBinding inflateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initObserver() {

    }

    @Override
    public void initView() {
        super.initView();
        initShardKey();
        binding.tvStart.setOnClickListener(view -> startNext());
        binding.videoView.setUp("https://v-cdn.zjol.com.cn/280443.mp4"
                , "");

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initShardKey() {
        String s = (String) SPUtils.get(getContext(), RINSE, "");
        if (TextUtils.isEmpty(s)) {
            SPUtils.put(getContext(), RINSE, "1");
            SPUtils.put(getContext(), SHAMPOO, "2");
            SPUtils.put(getContext(), SHAMPOO_SENIOR, "3");
            SPUtils.put(getContext(), CONDITIONER, "4");
            SPUtils.put(getContext(), CONDITIONER_SENIOR, "5");
            SPUtils.put(getContext(), BLOW, "6");
            SPUtils.put(getContext(), DISINFECT, "7");
            SPUtils.put(getContext(), PAUSE, "8");
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        binding.layoutHeader.ivLogo.setOnClickListener(v -> startNext());
        binding.layoutHeader.ivBubleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extracted();
            }
        });
        binding.layoutHeader.ivBubleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime < MAX_CLICK_INTERVAL) {
                    clickCount++;
                    if (clickCount == 5) {
                        // 连续点5次后跳转到另一个页面
                        showDialog();
                        clickCount = 0; // 重置点击次数
                    }
                } else {
                    clickCount = 1; // 第一次点击
                }
                lastClickTime = currentTime;
            }
        });
    }

    private void extracted() {
        Intent intent = new Intent(getActivity(), TestOneActivity.class);
        startActivity(intent);
    }

    private void updateDeviceSwitchButton() {
        boolean modbusOpened = ModbusManager.get().isModbusOpened();
        Log.d(TAG, "modbusOpened: " + modbusOpened);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        DialogInputPwdBinding dialogInputPwdBinding = DialogInputPwdBinding.inflate(layoutInflater);
        AlertDialog alertDialog = builder.setView(dialogInputPwdBinding.getRoot())
                .setTitle("请输入密码")
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }).create();
        alertDialog.setCancelable(false);
        alertDialog.setOnShowListener(dialogInterface -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                if ("123321".equals(dialogInputPwdBinding.etPwd.getText().toString())) {
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
                    alertDialog.dismiss();
                }
            });
        });
        alertDialog.show();

    }

    private void startNext() {
        viewModel.calMoney(0);
        viewModel.jumpToNext(this, R.id.fr_container, new StepOneFragment());
    }
}
