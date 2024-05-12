package com.android.soapy.ui;


import static com.android.soapy.vm.BaseViewModel.FROM_CARD;


import android.app.AlertDialog;
import android.text.TextUtils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.soapy.R;
import com.android.soapy.base.BaseFragment;
import com.android.soapy.databinding.DialogFinishBinding;
import com.android.soapy.databinding.FragmentFunctionBinding;

import com.android.soapy.vm.MainViewModel;
import com.android.soapy.weight.CustomNumericKeyboard;
import com.bumptech.glide.Glide;


import java.util.Objects;


public class FunctionFragment extends BaseFragment<FragmentFunctionBinding, MainViewModel> {

    private TextView[] buttons;
    private static String TAG = "FunctionFragment";
    private boolean[] buttonStates = new boolean[7]; // 按钮状态，true代表打开，false代表关闭

    private boolean paused = false; // 暂停状态
    private int lastOpenButtonIndex = -1; // 记录上一个打开的按钮索引
    private AlertDialog alertDialog;

    @Override
    protected FragmentFunctionBinding inflateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentFunctionBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initObserver() {

        viewModel.accumulatedAmount.observe(this, s -> binding.llFunctionBottom.tvTotalMoney.setText(String.format(getString(R.string.total_money), s)));

        viewModel.countDown.observe(this, s -> binding.chronometer.setText(s));

        viewModel.timerFinish.setValue(false);
        viewModel.timerFinish.observe(this, aBoolean -> {
            if (aBoolean) {
                showDialog();
                viewModel.startLastCountDownTimer(5000);
            }
        });
        viewModel.lastTimerFinish.setValue(false);
        viewModel.lastTimerFinish.observe(this, aBoolean -> {
            if (aBoolean) {
                alertDialog.dismiss();
                viewModel.totalMoney=0;
                jumpHome();
            }
        });
        //支付结果回调
        viewModel.PMReplyPara.setValue(null);
        viewModel.PMReplyPara.observe(this, pmReplyPara -> {
            if (pmReplyPara == null) return;
            if (pmReplyPara.getPayType() == 64) {
                //todo 这里支付失败
            }
            if (FROM_CARD.equals(viewModel.jumpType.getValue()) && pmReplyPara.getPayType() == 4) {
                int replyParaMultiple = pmReplyPara.getMultiple();
                if (replyParaMultiple >= Integer.parseInt(Objects.requireNonNull(viewModel.amountText.getValue())) * 100) {
                    binding.llInsertCard.getRoot().setVisibility(View.GONE);
                    viewModel.stopPolling();
                    viewModel.notifyPayment(true);
                    Long timeLongValue = viewModel.timeLong.getValue();
//                    long remainingTime = viewModel.countDownTimer.getRemainingTime();
                    long l = timeLongValue + viewModel.millisUntilFinishedRecord;
                    Log.d(TAG, "initObserver: time buy:" + timeLongValue);
                    Log.d(TAG, "initObserver: time remaining:" + viewModel.millisUntilFinishedRecord);
                    Log.d(TAG, "initObserver: time total:" + l);
                    viewModel.countDownTimer.setRemainingTime(l);
                    viewModel.countDownTimer.resume();
                }
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        DialogFinishBinding dialogInputPwdBinding = DialogFinishBinding.inflate(layoutInflater);
        alertDialog = builder.setView(dialogInputPwdBinding.getRoot()).create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    /**
     * 跳转首页
     */
    private void jumpHome() {
        viewModel.stopPolling();
        viewModel.jumpToNext(this, R.id.fr_container, new HomeFragment());
    }

    @Override
    public void initView() {
        super.initView();
        binding.llFunctionBottom.icHome.setVisibility(View.GONE);
        binding.llFunctionBottom.tvTemperature.setVisibility(View.VISIBLE);
        binding.llFunctionBottom.tvTemperature.setText(String.format(getString(R.string.temperature), "30℃"));
        initChronometer();
        initFunctionClick();

    }


    private void initFunctionClick() {
        buttons = new TextView[7];
        buttons[0] = binding.rbRinse;
        buttons[1] = binding.rbShampoo;
        buttons[2] = binding.rbShampooSenior;
        buttons[3] = binding.rbConditioner;
        buttons[4] = binding.rbConditionerSenior;
        buttons[5] = binding.rbBlow;
        buttons[6] = binding.rbDisinfect;
        // 初始化按钮
        for (int i = 0; i < 7; i++) {
            final int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.onPauseTimer(false);
                    toggleButton(finalI);
                    lastOpenButtonIndex = finalI;
                    sendBinaryData();
                }
            });
        }
    }

    private void toggleButton(int index) {
//        if (!paused) {
        // 切换按钮状态
        buttonStates[index] = !buttonStates[index];
        buttons[index].setSelected(buttonStates[index]);
        // 关闭其他按钮
        for (int i = 0; i < 7; i++) {
            if (i != index) {
                buttonStates[i] = false;
                buttons[i].setSelected(false);
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
                buttons[lastOpenButtonIndex].setSelected(true);
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
    private void closeAllButtons(TextView[] buttons) {
        for (int i = 0; i < 7; i++) {
            buttonStates[i] = false;
            buttons[i].setSelected(false);
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
        viewModel.writeSingleRegister(data);
    }


    private void initChronometer() {

        Long timeLongValue = viewModel.timeLong.getValue();
        Log.d(TAG, "initChronometer: "+timeLongValue);
        viewModel.startCountDownTimer(timeLongValue);
    }

    @Override
    public void initListener() {
        super.initListener();
        binding.tvPause.setOnClickListener(view -> {
            viewModel.onPauseTimer(viewModel.pauseTimer.getValue());
            togglePause();
            sendBinaryData();
            setRadioButtonStates();
        });

        binding.tvAddTime.setOnClickListener(view -> addTimeSetUi());
        binding.llCustomContainer.numberKeyboard.setOnKeyboardClickListener(new CustomNumericKeyboard.OnKeyboardClickListener() {
            @Override
            public void onConfirmClicked(String digit) {
                viewModel.calMoney(TextUtils.isEmpty(digit) ? 0 : Integer.parseInt(digit));
                binding.llCustomContainer.getRoot().setVisibility(View.GONE);
                binding.llSelectPayment.getRoot().setVisibility(View.VISIBLE);
                binding.llCustomContainer.tvTime.setText("");
            }

            @Override
            public void onDigitClicked(String digit) {
                viewModel.calMoney(TextUtils.isEmpty(digit) ? 0 : Integer.parseInt(digit));
                binding.llCustomContainer.tvTime.setText(digit);
            }
        });

        binding.llSelectPayment.ivCard.setOnClickListener(view -> {
            viewModel.countDownTimer.stop();
            viewModel.preparePayment(viewModel.amountText.getValue());
            viewModel.startPolling();
            setPayLayoutVisible(binding.llInsertCard.getRoot());
        });
        binding.llSelectPayment.ivCash.setOnClickListener(view -> setPayLayoutVisible(binding.llInsertCash.getRoot()));


    }


    private void addTimeSetUi() {
        binding.llCustomContainer.getRoot().setVisibility(View.VISIBLE);
        binding.llSelectPayment.getRoot().setVisibility(View.GONE);
        binding.llInsertCard.getRoot().setVisibility(View.GONE);
        binding.llInsertCash.getRoot().setVisibility(View.GONE);
    }

    private void setPayLayoutVisible(View view) {
        // 使用Glide加载GIF动画
        Glide.with(this)
                .load(R.mipmap.card_gif) // 替换成你的GIF资源文件
                .into(binding.llInsertCard.ivCardTerminal);
        binding.llSelectPayment.getRoot().setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * 点击暂停键 上面功能按钮置为false
     */
    private void setRadioButtonStates() {

    }
}
