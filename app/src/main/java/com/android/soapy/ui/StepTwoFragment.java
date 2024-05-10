package com.android.soapy.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.android.soapy.R;
import com.android.soapy.base.BaseFragment;
import com.android.soapy.databinding.FragmentStepTwoBinding;
import com.android.soapy.vm.MainViewModel;


public class StepTwoFragment extends BaseFragment<FragmentStepTwoBinding, MainViewModel> {
    @Override
    protected FragmentStepTwoBinding inflateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentStepTwoBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initObserver() {
        viewModel.amountText.observe(this, s -> binding.llBottom.tvTotalMoney.setText(String.format(getString(R.string.total_money), s)));
        viewModel.amountText.observe(this, s -> binding.llReviewOrder.tvWashDuration.setText(String.format(getString(R.string.wash_duration), s)));
    }

    @Override
    public void initListener() {
        super.initListener();
        binding.llBottom.icHome.setOnClickListener(v -> viewModel.jumpToNext(this, R.id.fr_container, new HomeFragment()));
        binding.tvNormal.setOnClickListener(v -> updateUi(1));
        binding.tvSensitive.setOnClickListener(v -> sensitive());
        binding.llReviewOrder.tvYes.setOnClickListener(v -> setNextUi());
        binding.llReviewOrder.tvRestart.setOnClickListener(v -> updateUi(3));
        binding.llSelectPayment.ivCard.setOnClickListener(v -> viewModel.jumpToNext(this, R.id.fr_container, new ContactlessFragment(), viewModel.FROM_CARD));
        binding.llSelectPayment.ivCash.setOnClickListener(v -> viewModel.jumpToNext(this, R.id.fr_container, new ContactlessFragment(), viewModel.FROM_CASH));
    }

    private void sensitive() {
        viewModel.calSensitiveMoney(true);
        updateUi(2);
    }

    private void setNextUi() {
        binding.tvTitle.setVisibility(View.GONE);
        binding.llReviewOrder.getRoot().setVisibility(View.GONE);
        binding.llSelectPayment.getRoot().setVisibility(View.VISIBLE);
    }

    private void updateUi(int type) {
        binding.tvTitle.setText(type == 3 ? "Step 2: Select Shampoo" : "Review Order");
        binding.llBtnContainer.setVisibility(type == 3 ? View.VISIBLE : View.GONE);
        binding.llReviewOrder.getRoot().setVisibility(type == 3 ? View.GONE : View.VISIBLE);
        binding.llReviewOrder.llShampoo.setVisibility(type == 1 ? View.GONE : View.VISIBLE);
        if (type == 3&& viewModel.sensitive.getValue()) {
            viewModel.calSensitiveMoney(false);
        }


    }


    @Override
    public void initView() {
        super.initView();

    }
}
