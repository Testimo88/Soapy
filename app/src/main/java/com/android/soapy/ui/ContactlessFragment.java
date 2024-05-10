package com.android.soapy.ui;

import static com.android.soapy.vm.BaseViewModel.FROM_CARD;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;


import com.android.soapy.R;
import com.android.soapy.base.BaseFragment;
import com.android.soapy.databinding.FragmentContactlessBinding;
import com.android.soapy.vm.BaseViewModel;
import com.android.soapy.vm.MainViewModel;
import com.bumptech.glide.Glide;

import java.util.Objects;

import cc.uling.usdk.board.mdb.para.PMReplyPara;
import cc.uling.usdk.board.mdb.para.PayReplyPara;

public class ContactlessFragment extends BaseFragment<FragmentContactlessBinding, MainViewModel> {

    private static final String TAG = "ContactlessFragment";

    @Override
    protected FragmentContactlessBinding inflateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentContactlessBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initObserver() {
        viewModel.amountText.observe(this, s -> binding.llBottom.tvTotalMoney.setText(String.format(String.format(getString(R.string.total_money), s))));
        //支付结果回调
        viewModel.PMReplyPara.observe(this, pmReplyPara -> {
            if (pmReplyPara == null) return;
            if (pmReplyPara.getPayType() == 64) {
                jumpHome();
            }
            if (FROM_CARD.equals(viewModel.jumpType.getValue()) && pmReplyPara.getPayType() == 4) {
                int replyParaMultiple = pmReplyPara.getMultiple();
                if (replyParaMultiple >= Integer.parseInt(Objects.requireNonNull(viewModel.amountText.getValue())) * 100) {
                    viewModel.stopPolling();
                    viewModel.notifyPayment(true);
                    jumpFunctionFragment();
                }
            }
        });
    }

    @Override
    public void initView() {
        super.initView();
        Log.d(TAG, "initView from_type:" + viewModel.jumpType.getValue());
        if (FROM_CARD.equals(viewModel.jumpType.getValue())) {
            // 使用Glide加载GIF动画
            Glide.with(this)
                    .load(R.mipmap.card_gif) // 替换成你的GIF资源文件
                    .into(binding.llInsertCard.ivCardTerminal);
            binding.llInsertCard.ivCardTerminal.setVisibility(View.VISIBLE);
            binding.llInsertCash.getRoot().setVisibility(View.GONE);
            viewModel.preparePayment(viewModel.amountText.getValue());//发起收款
            viewModel.startPolling();

        } else {
            binding.llInsertCard.getRoot().setVisibility(View.GONE);
            binding.llInsertCash.getRoot().setVisibility(View.VISIBLE);
            binding.llInsertCash.tvInsertAmount.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewModel.startCountDownTimer(5000);
                }
            }, 2000);
        }
        viewModel.timerFinish.setValue(false);
        viewModel.timerFinish.observe(this, aBoolean -> {
            if (aBoolean) {
//                Toast.makeText(getActivity(), R.string.pay_timeout,Toast.LENGTH_SHORT).show();
//                viewModel.jumpToNext(this, R.id.fr_container, new HomeFragment());
                jumpFunctionFragment();
            }
        });

    }


    @Override
    public void initListener() {
        super.initListener();
        binding.llBottom.icHome.setOnClickListener(v -> jumpHome());
    }

    /**
     * 跳转功能页面
     */
    private void jumpFunctionFragment() {
        viewModel.jumpToNext(this, R.id.fr_container, new FunctionFragment());
    }

    /**
     * 跳转首页
     */
    private void jumpHome() {
        viewModel.stopPolling();
        viewModel.jumpToNext(this, R.id.fr_container, new HomeFragment());
    }
}
