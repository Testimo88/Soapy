package com.android.soapy.vm;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.soapy.utils.FragmentUtils;

public class BaseViewModel extends ViewModel {
    public static final String FROM_CARD = "Card";
    public static final String FROM_CASH = "Cash";
    public MutableLiveData<String> jumpType = new MutableLiveData<>();
    public Context context;

    public void jumpToNext(Fragment fragment, int id, Fragment targFragment, String type) {
        jumpType.setValue(type);
        FragmentUtils.show(fragment.requireActivity(), id, targFragment);
        FragmentUtils.hide(fragment.requireActivity(), fragment);
    }
    public void initContext(Context context) {
        this.context=context;
    }
}
