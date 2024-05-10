package com.android.soapy;

import android.view.LayoutInflater;

import com.android.soapy.base.BaseActivity;
import com.android.soapy.databinding.ActivitySettingsBinding;
import com.android.soapy.vm.BaseViewModel;

public class SettingsActivity extends BaseActivity<ActivitySettingsBinding, BaseViewModel> {
    @Override
    protected ActivitySettingsBinding inflateViewBinding(LayoutInflater layoutInflater) {
        return ActivitySettingsBinding.inflate(layoutInflater);
    }
}
