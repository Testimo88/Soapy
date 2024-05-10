package com.android.soapy.ui;


import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.android.soapy.R;
import com.android.soapy.base.BaseFragment;
import com.android.soapy.databinding.FragmentStepOneBinding;
import com.android.soapy.vm.MainViewModel;
import com.android.soapy.weight.CustomNumericKeyboard;

public class StepOneFragment extends BaseFragment<FragmentStepOneBinding, MainViewModel> {
    @Override
    protected FragmentStepOneBinding inflateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentStepOneBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initObserver() {
        viewModel.amountText.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.llBottom.tvTotalMoney.setText(String.format(getString(R.string.total_money), s));
            }
        });
    }

    @Override
    public void initListener() {
        super.initListener();
        binding.llCustomContainer.numberKeyboard.setOnKeyboardClickListener(new CustomNumericKeyboard.OnKeyboardClickListener() {
            @Override
            public void onConfirmClicked(String digit) {
                viewModel.calMoney(TextUtils.isEmpty(digit)?0:Integer.parseInt(digit));
                viewModel.jumpToNext(StepOneFragment.this, R.id.fr_container, new StepTwoFragment());
            }

            @Override
            public void onDigitClicked(String digit) {
                viewModel.calMoney(TextUtils.isEmpty(digit) ? 0 : Integer.parseInt(digit));
                binding.llCustomContainer.tvTime.setText(digit);
            }
        });
        binding.llBottom.icHome.setOnClickListener(v -> viewModel.jumpToNext(this, R.id.fr_container, new HomeFragment()));
        binding.tvCustomTime.setOnClickListener(view -> customTime());
        jump();
    }

    private void jump() {
        binding.tvMin5.setOnClickListener(v -> cal(5));
        binding.tvMin10.setOnClickListener(v -> cal(10));
        binding.tvMin15.setOnClickListener(v -> cal(15));
    }

    private void cal(int i) {
        viewModel.calMoney(i);
        viewModel.jumpToNext(StepOneFragment.this, R.id.fr_container, new StepTwoFragment());
    }

    private void customTime() {
        binding.llBtnContainer.setVisibility(View.GONE);
        binding.llCustomContainer.getRoot().setVisibility(View.VISIBLE);
    }

    @Override
    public void initView() {
        super.initView();



    }
}
